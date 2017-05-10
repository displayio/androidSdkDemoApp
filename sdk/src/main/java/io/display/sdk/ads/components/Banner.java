package io.display.sdk.ads.components;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import io.display.sdk.Controller;
import io.display.sdk.DioImageView;
import io.display.sdk.DioSdkException;
import io.display.sdk.ads.tools.FileLoader;

/**
 * Created by jynx on 25/12/16.
 */

public class Banner extends Component {
    public static final String FEATURE_ROUND_FRAME = "roundFrame";

    FrameLayout layout;
    DioImageView img;
    Container container;


    Context context;

    OnClickListener onClickListener;
    OnPreloadListener onPreloadListener;
    OnPreloadErrorListener onPreloadErrorListener;
    OnErrorListener onErrorListener;

    FileLoader imgLoader;
    String imgUrl;
    int res;

    public Banner() {

    }
    public void setUrl(String url) {
        imgUrl = url;
    }
    public void setResource(String res) {
        this.res = Controller.getInstance().getContext().getResources().getIdentifier(res, "drawable", Controller.getInstance().getContext().getPackageName());
    }
    public void show() {
        container.show();
    }
    public void hide() {
        container.hide();
    }
    public void preload() {
        if(imgLoader == null) {
//            imgLoader = new FileLoader(imgUrl);
            imgLoader = new FileLoader(res);
            imgLoader.setListener(new FileLoader.OnLoadedListener() {
                @Override
                public void onLoaded() {
                    if (onPreloadListener != null) {
                        onPreloadListener.onPreload();
                    }
                }

                @Override
                public void onLoadError() {
                    if (onPreloadErrorListener != null) {
                        onPreloadErrorListener.onPreloadError();
                    }

                }
            });
            imgLoader.exec();
        }
    }


    public void initContainer(Context context) {
        container = new Container(context);
    }
    public void render(Context context) throws DioSdkException {
        this.context = context;
        layout = new FrameLayout(context);
        img = new DioImageView(context);
        if(isFeatureSet(FEATURE_ROUND_FRAME)) {
            img.setRoundFrame();
        }

        /***************/
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
        if(imgLoader.isFileLoaded()) {
            setupImageView(imgLoader.fileRes);
        } else {
            imgLoader.setListener(new FileLoader.OnLoadedListener() {
                @Override
                public void onLoaded() {
                    try {
                        setupImageView(imgLoader.fileRes);
                    } catch (Exception e) {
                        onLoadError();
                    }
                }

                @Override
                public void onLoadError() {
                    if(onErrorListener != null)
                        onErrorListener.onError();
                }
            });
        }

    }
    private void setupImageView(int res) throws DioSdkException {
//        img.setImageURI(url);
        img.setImageResource(res);
        FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgLp.gravity = Gravity.TOP;
        img.setLayoutParams(imgLp);
        img.setAdjustViewBounds(true);
        layout.addView(img);
        container.setView(layout);

        container.render();
    }
    public Container getContainer() {
        return container;
    }
    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnPreloadListener(OnPreloadListener listener) {
        onPreloadListener = listener;
    }
    public void setOnPreloadErrorListener(OnPreloadErrorListener listener) {
        onPreloadErrorListener = listener;
    }
    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
    }


    public static abstract class OnClickListener {
        abstract public void onClick();
    }

    public static abstract class OnPreloadListener {
        abstract public void onPreload();
    }
    public static abstract class OnPreloadErrorListener {
        abstract public void onPreloadError();
    }
    public static abstract class OnErrorListener {
        abstract public void onError();
    }

}
