package io.displayio.sdk.ads.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jynx on 25/12/16.
 */

public class Container extends Component {
    final int closeBtnTextSize = 14;
    final int closeButtonPaddingPx = 14;

    View view;
    RelativeLayout layout;
    RelativeLayout closeButtonContainer;
    Context context;
    TextView closeButton;



    OnCloseListener onCloseListener;
    OnCloseEnabledListener onCloseEnabledListener;
    OnOpenListener onOpenListener;



    public static final String FEATURE_CLOSE_BUTTON = "closeButton";
    public static final String FEATURE_ROTATE = "rotate";
    public static final String OPTION_CLOSE_BUTTON_DELAY = "closeButtonDelay";
    public static final String OPTION_PADDING_VERTICAL = "paddingY";
    public static final String OPTION_PADDING_HORIZONTAL = "paddingX";
    ArrayList<Integer> closeButtonSecsTriggered = new ArrayList<>();
    private void setCloseButtonBackground(int[] colorArray) {
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArray);
        shape.setShape(GradientDrawable.OVAL);
        shape.setStroke(6, Color.WHITE);
        shape.setAlpha(230);

        shape.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            closeButton.setBackground(shape);
        }else{
            closeButton.setBackgroundDrawable(shape);
        }
    }
    public Container(Context ctx) {
        context = ctx;
        layout = new RelativeLayout(ctx);
    }
    public int getCloseButtonSize() {
        return closeBtnTextSize / 2 + closeButtonPaddingPx;
    }
    private void renderClsButton() {
        closeButton = new TextView(context);
        closeButtonContainer = new RelativeLayout(context);

        final int heightWithPad = getPxToDp(getCloseButtonSize());

        closeButton.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams buttonLp = new RelativeLayout.LayoutParams(heightWithPad * 2, heightWithPad * 2);
        buttonLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        buttonLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeButton.setLayoutParams(buttonLp);
        closeButton.setGravity(Gravity.CENTER);
        closeButton.setTypeface(closeButton.getTypeface(), Typeface.BOLD);

        setCloseButtonBackground(new int[] { Color.LTGRAY, Color.DKGRAY});

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Listen to clicks but do nothing.
            }
        });

        closeButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Listen to clicks but do nothing.
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightWithPad * 3, heightWithPad * 3);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY, Gravity.RIGHT);
        closeButtonContainer.setLayoutParams(lp);
        closeButtonContainer.addView(closeButton);
        RelativeLayout.LayoutParams containerViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        containerViewLayout.setMargins(heightWithPad, heightWithPad,heightWithPad,heightWithPad);
        view.setLayoutParams(containerViewLayout);

        if(hasIntOption(OPTION_CLOSE_BUTTON_DELAY)) {
            CountDownTimer counter = new CountDownTimer((long)getIntOption(OPTION_CLOSE_BUTTON_DELAY), 250L) {
                public void onTick(long millisUntilFinished) {
                    int sec = (int) Math.ceil((float) millisUntilFinished / 1000.0);
                    if (!closeButtonSecsTriggered.contains(sec)) {
                        closeButtonSecsTriggered.add(sec);
                        closeButton.setText(String.format(Locale.getDefault(), Integer.toString(sec)));
                        final float shrinkTo = 0.95f;
                        ScaleAnimation shrink = new ScaleAnimation(1, shrinkTo, 1, shrinkTo, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        shrink.setDuration(500);
                        shrink.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                ScaleAnimation grow = new ScaleAnimation(shrinkTo, 1, shrinkTo, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                grow.setDuration(500);
                                closeButton.startAnimation(grow);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        closeButton.startAnimation(shrink);
                    }
                }

                public void onFinish() {
                    closeButtonSecsTriggered = new ArrayList<>();
                    enableClose();
                }
            };
            counter.start();
        } else {
            enableClose();
        }
    }
    public View getContainedView() {
        return view;
    }
    private void enableClose() {
        closeButton.setText("X");
        setCloseButtonBackground(new int[] { Color.DKGRAY, Color.BLACK});


        closeButton.setOnClickListener(null);
        closeButtonContainer.setOnClickListener(null);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCloseListener != null) {
                    onCloseListener.onClose();
                }
            }
        });
        if(onCloseEnabledListener != null) {
            onCloseEnabledListener.onCloseEnabled();
        }
        closeButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCloseListener != null) {
                    onCloseListener.onClose();
                }
            }
        });
    }
    public void show() {
        layout.setVisibility(View.VISIBLE);
    }
    public void hide() {
        layout.setVisibility(View.GONE);
    }
    public void setOnCloseEnabledListener(OnCloseEnabledListener listener) {
        onCloseEnabledListener = listener;
    }
    public void setOnOpenListener(OnOpenListener listener) {
        onOpenListener = listener;
    }

    public void setOnCloseListener(OnCloseListener listener) {
        onCloseListener = listener;
    }

    public void render() {
        int padX = 0, padY = 0;
        if(hasIntOption(OPTION_PADDING_VERTICAL)) {
            padY = getIntOption(OPTION_PADDING_VERTICAL);
        }
        if(hasIntOption(OPTION_PADDING_HORIZONTAL)) {
            padX = getIntOption(OPTION_PADDING_HORIZONTAL);
        }
        if(isFeatureSet(FEATURE_ROTATE)) {
            layout.setVisibility(View.INVISIBLE);
        }
        layout.setPadding(padX, padY, padX, padY);
        layout.addView(view, 0);
        layout.post(new Runnable() {
            @Override
            public void run() {
                checkAppear(1500);
            }
        });

        if(isFeatureSet(FEATURE_CLOSE_BUTTON)) {
            renderClsButton();
            layout.addView(closeButtonContainer, 1);
        }
    }
    private void checkAppear(final int interval) {
        Rect scrollBounds = new Rect();
        layout.getHitRect(scrollBounds);
        if (layout.getLocalVisibleRect(scrollBounds)) {
            onAppear();
        } else {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAppear(interval);
                }
            }, interval);
        }
    }
    private void onOpen() {
        if(onOpenListener != null) {
            onOpenListener.onOpen();
        }
    }
    private void onAppear() {
        if (isFeatureSet(FEATURE_ROTATE)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {


                //Object animator
                ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "rotationY", 270f, 360f);
                animation.setDuration(1200);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        layout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onOpen();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animation.start();


            }
        } else {
            layout.setVisibility(View.VISIBLE);
            onOpen();
        }
    }
    public void setView(View view) {
        this.view = view;
    }
    public View getLayout() {
        return layout;
    }
    private int getPxToDp(int valueInPx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) valueInPx, context.getResources().getDisplayMetrics());
    }
    public static abstract class OnCloseEnabledListener {
        public abstract void onCloseEnabled();
    }
    public static abstract class OnOpenListener {
        public abstract void onOpen();
    }

    public static abstract class OnCloseListener {
        public abstract void onClose();
    }
}
