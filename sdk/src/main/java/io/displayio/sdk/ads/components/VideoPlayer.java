package io.displayio.sdk.ads.components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.displayio.sdk.Controller;
import io.displayio.sdk.DioSdkException;
import io.displayio.sdk.ads.Ad;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideoPlayer extends Component {
    final public static String FEATURE_SKIPPABLE = "skippable";
    final public static String FEATURE_CONTINUOUS = "continuous";
    final public static String FEATURE_SHOW_TIMER = "showTimer";
    final public static String FEATURE_CLICK_BOX = "clickBox";
    final public static String FEATURE_SOUND_CONTROL = "soundControl";
    final public static String OPTION_SKIP_AFTER = "skipAfter";
    final public static String OPTION_CLICK_BOX_TEXT = "clickBoxText";

    private VideoView videoView;
    private MediaPlayer mp;
    private RelativeLayout wrappingLayout;
    private Context ctx;
    private TextView skipText;
    private TextView timerText;
    private ImageView btnSoundOn;
    private ImageView btnSoundOff;
    private TextView clickBox;
    private Loader loader = new Loader();


    private HashMap<String, Boolean> signaledOnce = new HashMap<>();
    private HashMap<String, ArrayList<String>> evBeacons = new HashMap<String, ArrayList<String>>();
    private ArrayList<OnCompletionListener> onCompletionListners = new ArrayList<>();
    private ArrayList<OnClickListener> onClickListeners = new ArrayList<>();
    private ArrayList<OnErrorListener> onErrorListeners = new ArrayList<>();
    private ArrayList<OnStartListener> onStartListeners = new ArrayList<>();

    private Timer timer;
    private double duration;
    private int commonTextSize = 14;
    private int currentLayer = 0;
    private Boolean isSkippable = false;
    private OnSkipListener onSkipListner;
    private Boolean isSoundOn = true;
    private String url;
    protected int lastPos = 0;
    private String state = "uninitialized";

    public VideoPlayer() {
    }
    public void setOnSkipListener(OnSkipListener listener) {
        onSkipListner = listener;
    }
    public void setOnCompleteListener(OnCompletionListener listener) {
        onCompletionListners.add(listener);
    }
    public void setOnClickListener(OnClickListener listener) {
        onClickListeners.add(listener);
    }
    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListeners.add(listener);
    }
    public void setOnStartListener(OnStartListener listener) {
        onStartListeners.add(listener);
    }
    public void hideView() {
        pause();
        wrappingLayout.setVisibility(GONE);
    }

    private void seekToLastPos() {
        if(lastPos > 0) {
            mp.seekTo(lastPos);
        }
    }


    public void start(Uri uri, final double duration) throws DioSdkException {
        state = "initializing";
        this.url = uri.toString();
        loader.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if(state == "stopped") {
                    return;
                }
                initMediaPlayer(mediaPlayer);
                loader.hide();
                mp.start();

                if(isFeatureSet(FEATURE_SOUND_CONTROL)) {
                    setSound(isSoundOn);
                    alignSoundControlIcons();
                }
                if(isFeatureSet(FEATURE_CONTINUOUS)) {
                    if(state == "playing") {
                        seekToLastPos();
                    }
                }
                if(state == "initializing") {
                    for(OnStartListener listener: onStartListeners) {
                        listener.onStart();
                    }
                }
                state = "playing";
                double videoDuration = duration;
                try {
                    videoDuration = (double) (mp.getDuration())/ 1000;
                } catch (IllegalStateException e) {

                }

                initTimer(videoDuration);
                timer.start();
                signalEventOnce("start");
                signalEventOnce("impression");
            }
        });
        videoView.setVideoURI(uri);

    }
    private void initTimer(double duration) {
        this.duration = duration;
        if(timer != null) {
            timer.cancel();
        }
        timer = new Timer((long)(duration * 1000));
        registerFeatureHandlers();
        regiseterEventHandler();
    }
    private void initMediaPlayer(MediaPlayer mediaPlayer) {
        mp = mediaPlayer;
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                signalEventOnce("finish");
                for(OnCompletionListener onCompletionListner: onCompletionListners) {
                    onCompletionListner.onComplete();
                }
            }
        });
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int r) {
            switch(i) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    loader.show();
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    loader.hide();
            }
            return false;
            }
        });
    }

    public boolean isPlaying() {
        if(state == "playing") {
            // check state
            try {
                boolean isPlaying = mp.isPlaying();
                return isPlaying;
            } catch (IllegalStateException e) {
                return false;
            }

        }
        return false;
    }
    public void pause() {
        if(mp != null) {
            mp.pause();
        }
        if(timer != null) {
            timer.pause();
        }
    }
    public void stop() {
        try {
            mp.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        timer.cancel();
        state = "stopped";
    }
    public void resume() {
        videoView.start();
        timer.resume();
    }
    public RelativeLayout getView() {
        return wrappingLayout;
    }

    public void render(Context ctx) {
        this.ctx = ctx;
        videoView = new VideoView(ctx);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                for(OnErrorListener listener: onErrorListeners) {
                    listener.onError(i, i1, url);
                }
                return true;
            }
        });
        wrappingLayout = new RelativeLayout(ctx);
        final RelativeLayout.LayoutParams videoLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        videoLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
            videoLp.addRule(Gravity.CENTER);
        wrappingLayout.setLayoutParams(videoLp);
        videoView.setLayoutParams(videoLp);
        addLayer(videoView);
        processFeatureRendering();

    }

    private void addLayer(View view) {
        wrappingLayout.addView(view);
    }
    private void addSkipTextContainer() {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int paddingTopLeft = (int) (3 * scale + 0.5f);
        int paddingBottomRight = (int) (25 * scale + 0.5f);

        skipText = new TextView(ctx);
        skipText.setTextColor(Color.parseColor("#EEEEEE"));
        skipText.setShadowLayer(1, 2, 2, Color.parseColor("#222222"));
        skipText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        skipText.setPadding(paddingTopLeft, paddingTopLeft, paddingBottomRight, paddingBottomRight);
        skipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, commonTextSize);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do nothing, just catch onClick
            }
        });
        addLayer(skipText);
    }
    private void addTimerTextContainer() {
        RelativeLayout.LayoutParams timerLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        timerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        timerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        timerText = new TextView(ctx);
        timerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, commonTextSize);
        timerText.setTextColor(Color.parseColor("#555555"));
        timerText.setShadowLayer(1, 2, 2, Color.parseColor("#EEEEEE"));
        timerText.setLayoutParams(timerLp);

        addLayer(timerText);
    }


    public void loadEvents(JSONObject events) {
        Iterator keys = events.keys();
        while(keys.hasNext()) {
            String event = (String) keys.next();
            try {
                JSONArray urls = events.getJSONArray(event);
                for (int x = 0; x < urls.length(); x++) {
                    String url = (String) urls.get(x);
                    if(!evBeacons.containsKey(event)) {
                        evBeacons.put(event, new ArrayList<String>());
                    }
                    ArrayList<String> list = evBeacons.get(event);
                    if(!list.contains(url)) {
                        list.add(url);
                    }
                }
            } catch (JSONException e) {
                Log.e(Ad.TAG, "error processing events url for event " + event);
            }
        }
    }

    private synchronized void signalEventOnce(String name) {
        if(!signaledOnce.containsKey(name)) {
            signaledOnce.put(name, true);
            signalEvent(name);
        }
    }
    private void signalEvent(String name) {
        Log.d(Ad.TAG, "calling event " + name);
        if(evBeacons.containsKey(name)) {
            for(String url: evBeacons.get(name)) {
                Log.d(Ad.TAG, "calling event " + name + " url " + url);
                Ad.callBeacon(url);
            }
        }
    }

    public void setSound( boolean on ) {
        isSoundOn = on;
        try {
            if(mp != null) {
                if (on) {
                    mp.setVolume(0.8f, 0.8f);
                } else {
                    mp.setVolume(0f, 0f);
                }
            }
        } catch (IllegalStateException e) {
            // this means we're not yet set with the mediaplayer - which is fine
        }
    }
    private void alignSoundControlIcons() {
        if(isSoundOn) {
            btnSoundOff.setVisibility(VISIBLE);
            btnSoundOn.setVisibility(GONE);
        } else {
            btnSoundOff.setVisibility(GONE);
            btnSoundOn.setVisibility(VISIBLE);
        }

    }
    private void addSoundControls() {
        final int dimPx = 40;
        final int paddingPx = (Controller.getInstance().deviceDescriptor.getInchScreenSize() > 4) ? 25 : 10;

        final InputStream isBtnOn = this.getClass().getResourceAsStream("/images/ic_sound_on.png");
        final InputStream isBtnOff = this.getClass().getResourceAsStream("/images/ic_sound_off.png");
        btnSoundOff = new ImageView(ctx);
        btnSoundOn = new ImageView(ctx);

        final RelativeLayout soundCtrlContainer = new RelativeLayout(ctx);
        int soundCtrlSize = getPxToDp(32);
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(soundCtrlSize, soundCtrlSize);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            lp.addRule(RelativeLayout.ALIGN_PARENT_START);
        soundCtrlContainer.setLayoutParams(lp);
        if(isBtnOn == null || isBtnOff == null) {
           return;
        }

        soundCtrlContainer.setPadding(15, 0, 0, 25);
        btnSoundOn.setImageBitmap(BitmapFactory.decodeStream(isBtnOn));
        btnSoundOff.setImageBitmap(BitmapFactory.decodeStream(isBtnOff));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {Color.parseColor("#111111"), Color.parseColor("#444444")});
            bg.setShape(GradientDrawable.OVAL);
            bg.setGradientCenter(20, 10);
            bg.setAlpha(90);
            btnSoundOn.setBackground(bg);
            btnSoundOff.setBackground(bg);
        }
        int borderPaddingPx = 2;
        int borderPaddingPx2 = 6;

        btnSoundOff.setPadding(borderPaddingPx2,borderPaddingPx2,borderPaddingPx2,borderPaddingPx2);
        btnSoundOn.setPadding(borderPaddingPx,borderPaddingPx,borderPaddingPx,borderPaddingPx);

        soundCtrlContainer.addView(btnSoundOff);
        soundCtrlContainer.addView(btnSoundOn);



        btnSoundOn.setLayoutParams(new RelativeLayout.LayoutParams(getPxToDp(dimPx), getPxToDp(dimPx)));
        btnSoundOff.setLayoutParams(new RelativeLayout.LayoutParams(getPxToDp(dimPx), getPxToDp(dimPx)));

        btnSoundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSound(false);
                alignSoundControlIcons();
            }
        });
        btnSoundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSound(true);
                alignSoundControlIcons();
            }
        });
        addLayer(soundCtrlContainer);
    }
    private int getPxToDp(int valueInPx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) valueInPx, ctx.getResources().getDisplayMetrics());
    }

    /**
     *
     * Internal method to renderAdComponents feature visual elemetns
     * Called by renderAdComponents
     */
    private void processFeatureRendering() {
        if(isFeatureSet(FEATURE_SKIPPABLE)) {
            addSkipTextContainer();
        }
        if(isFeatureSet(FEATURE_SHOW_TIMER)) {
            addTimerTextContainer();
        }
        if(isFeatureSet(FEATURE_SOUND_CONTROL)) {
            addSoundControls();
        }
        if(isFeatureSet(FEATURE_CLICK_BOX) && onClickListeners.size() > 0 ) {
            addClickBox();
        }

    }

    private void addClickBox() {
        String clickBoxText = "Go To Advertiser";
        if(hasStringOption(OPTION_CLICK_BOX_TEXT)) {
            clickBoxText = getStrOption(OPTION_CLICK_BOX_TEXT);
        }
        clickBox = new TextView(ctx);
        clickBox.setText(clickBoxText);

        int padX = getPxToDp(6);
        int padY = getPxToDp(2);
        int margin = getPxToDp(10);
        clickBox.setTypeface(Typeface.DEFAULT_BOLD);
        clickBox.setTextColor(Color.parseColor("#FFFFFF"));
        clickBox.setShadowLayer(2, 2, 2, Color.parseColor("#000000"));
        clickBox.setPadding(padX, padY, padX, padY);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, margin, margin, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GradientDrawable bg1 = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {Color.parseColor("#111111"), Color.parseColor("#444444")});
            bg1.setGradientCenter(20, 10);
            bg1.setCornerRadius(5);
            bg1.setSize(10, 10);
            bg1.setAlpha(90);
            GradientDrawable bg2 = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {Color.parseColor("#AAAAAA"), Color.parseColor("#FFFFFF")});
            bg2.setGradientCenter(40, 12);
            bg2.setCornerRadius(0);
            bg2.setSize(20, 20);
            bg2.setAlpha(70);
            bg2.setStroke(2, Color.parseColor("#222222"));
            Drawable[] bgLayers = new Drawable[]{bg1, bg2};
            LayerDrawable bg = new LayerDrawable(bgLayers);
            clickBox.setAlpha(1);
            clickBox.setLayoutParams(lp);
            clickBox.setBackground(bg);
        } else {
            clickBox.setBackgroundColor(Color.DKGRAY);
        }
        final Handler handler = new Handler();
        clickBox.setVisibility(GONE);
        setOnStartListener(new OnStartListener() {
            @Override
            public void onStart() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickBox.setVisibility(VISIBLE);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                            ObjectAnimator animation = ObjectAnimator.ofFloat(clickBox, "rotationY", 270f, 360f);
                            animation.setDuration(1000);
                            animation.setInterpolator(new AccelerateDecelerateInterpolator());
                            animation.start();
                        }
                    }
                }, 1500);
            }
        });
        addLayer(clickBox);
    }
    private void processClickHandler() {
        if(onClickListeners.size() > 0) {
            if(isFeatureSet(FEATURE_CLICK_BOX)) {
                clickBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleClickEvent();
                    }
                });
            } else {
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            handleClickEvent();
                        }
                        return true;
                    }
                });
            }
        }
    }
    private void handleClickEvent() {
        signalEvent("click");
        for(OnClickListener listener : onClickListeners) {
            listener.onClick();
        }
    }
    private void registerPositionTrackHandler() {
        timer.addTickListener(new TimerListener() {
            @Override
            public void onTick(int pos) {
                if(isPlaying()) {
                    lastPos = pos;
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }
    public void hideLoader() {
        loader.hide();
    }
    public void showLoader() {
        loader.show();
    }
    /**
     * Internal method to register time handlers
     * Called when timer is initiated
     */
    private void registerFeatureHandlers() {
        if(isFeatureSet(FEATURE_SKIPPABLE)) {
            registerSkipHandler();
        }
        if(isFeatureSet(FEATURE_SHOW_TIMER)) {
            registerProgressTimerHandler();
        }
        if(isFeatureSet(FEATURE_CONTINUOUS)) {
            registerPositionTrackHandler();
        }
        processClickHandler();
    }

    private void registerSkipHandler() {
        final int skipAfter = getIntOption(OPTION_SKIP_AFTER);
        if(skipAfter > 0) {

            timer.addListener(new TimerListener() {
                @Override
                public void onTick(int pos) {
                    if(pos < skipAfter) {
                        int skipIn = skipAfter - pos;
                        String skipMessage = "Skippable in " + Integer.toString(skipIn) + " seconds";
                        skipText.setText(skipMessage);
                    } else {
                       makeSkippable();
                    }
                }

                @Override
                public void onFinish() {

                }
            });
        }
    }
    private void registerProgressTimerHandler() {
        timer.addListener(new TimerListener() {
            @Override
            public void onTick(int pos) {
                int endIn = (int)(duration - pos);
                timerText.setText("Video will end in " + Integer.toString(endIn) + " seconds");
            }

            @Override
            public void onFinish() {
                timerText.setText("");
            }
        });
    }
    public MediaPlayer getMediaPlayer() {
        return mp;
    }
    private void makeSkippable() {
        if(!isSkippable) {
            isSkippable = true;
            skipText.setText("Skip");
            skipText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signalEventOnce("skip");
                    if(onSkipListner != null) {
                        onSkipListner.onSkip();
                    }
                }
            });
        }
    }

    /**
     * Register timer bindings
     *
     */
    private void regiseterEventHandler() {
        final int midPoint = (int)Math.floor(duration / 2);
        final int firstQuartile = (int)Math.floor(duration / 4);
        final int thirdQuartile = firstQuartile * 3;


        timer.addListener(new TimerListener() {
            @Override
            public void onTick(int sec) {
                if(sec == midPoint ) {
                    signalEventOnce("midPoint");
                }
                if(sec == firstQuartile) {
                    signalEventOnce("firstQuartile");
                }
                if(sec == thirdQuartile) {
                    signalEventOnce("thirdQuartile");
                }
            }

            @Override
            public void onFinish() {

            }

        });
    }
    class Loader {
        Looper looper;
        ProgressBar loader;

        public Loader() {

        }
        public void initLoader() {
            if(loader == null) {
                loader = new ProgressBar(ctx);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getPxToDp(45), getPxToDp(45));
                lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                loader.setLayoutParams(lp);
                addLayer(loader);
            }

        }
        public void hide() {
            if(loader != null) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                }, 0);
            }
        }
        public void show() {
            initLoader();

            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(VISIBLE);
                }
            }, 0);

        }
        Handler getHandler() {
            if(looper == null) {
                Handler handler = new Handler();
                looper = handler.getLooper();
                return handler;
            }
            return new Handler(looper);

        }

    }
    /**
     * Timer object
     *
     *
     *
     */

    public class Timer {
        ArrayList<TimerListener> listeners = new ArrayList<TimerListener>();
        ArrayList<TimerListener> tickListeners = new ArrayList<TimerListener>();
        long revPos, length;
        CountDownTimer timer;
        ArrayList<Integer> posSignaled = new ArrayList<>();
        public Timer(long ms) {
            length = ms;
            revPos = ms;
            initTimer();
        }
        public void cancel() {
            timer.cancel();
        }
        void initTimer() {
            timer = new CountDownTimer(revPos, 20) {
                @Override
                public void onTick(long ms) {
                    if(!isPlaying()) {
                        pause();
                    }
                    int realPosition = 0;
                    try {
                        realPosition = videoView.getCurrentPosition();
                        ms = length - (long) realPosition;
                    } catch (IllegalStateException e ) {
                        realPosition = (int)(length - ms);
                    }
                    revPos = ms;

                    int posSec = (int)Math.floor( (length - revPos) / 1000);
                    for(TimerListener listener: tickListeners) {
                        listener.onTick(realPosition);
                    }
                    if(!posSignaled.contains(posSec)) {
                        for(TimerListener listener: listeners) {
                            listener.onTick(posSec);
                        }
                    }
                }
                @Override
                public void onFinish() {
                    for(TimerListener listener: listeners) {
                        listener.onFinish();
                    }
                    for(TimerListener listener: tickListeners) {
                        listener.onFinish();
                    }
                }
            };
        }
        public void addListener(TimerListener listener) {
            listeners.add(listener);
        }
        public void addTickListener(TimerListener listener) {
            tickListeners.add(listener);
        }

        public void start() {
            timer.start();

        }
        public void pause() {
            timer.cancel();

        }
        public void resume() {
            initTimer();
            start();
        }


    }
    abstract class TimerListener {
        public abstract void onTick(int pos);
        public abstract void onFinish();
    }
    public abstract static class OnSkipListener {
        abstract public void onSkip();
    }
    public abstract static class OnCompletionListener {
        abstract public void onComplete();
    }

    public abstract static class OnErrorListener {
        abstract public void onError(int codeMajor, int codeMinor, String url);
    }
    public abstract static class OnStartListener {
        abstract public void onStart();
    }
    public abstract static class OnClickListener {
        abstract public void onClick();
    }
}
