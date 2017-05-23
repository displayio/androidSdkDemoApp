package io.displayio.sdk.ads.supers;


import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.displayio.sdk.Controller;
import io.displayio.sdk.DioSdkException;
import io.displayio.sdk.ads.Ad;
import io.displayio.sdk.ads.components.Banner;
import io.displayio.sdk.ads.components.VideoPlayer;
import io.displayio.sdk.ads.tools.FileLoader;


public abstract class HouseVideoAd  extends Ad {
    protected VideoPlayer player;
    private FileLoader videoLoader;
    protected Banner landingCard;
    protected FrameLayout layout;
    public HouseVideoAd(String id , JSONObject data, JSONObject offering) {
        super(id, data, offering);
    }
    public void preload() throws DioSdkException {
        setMultiLoadElmCount(1);
        if (hasLandingCard()) {
            try {
                setMultiLoadElmCount(2);
                String url = data.getString("landingCard");
                landingCard = new Banner();
//                landingCard.setUrl(url);
                landingCard.setResource(url);
                landingCard.setOnPreloadErrorListener(new Banner.OnPreloadErrorListener() {
                    @Override
                    public void onPreloadError() {
                        broadcCastPreloadError();
                    }
                });
                landingCard.setOnClickListener(new Banner.OnClickListener() {
                    @Override
                    public void onClick() {
                        redirect();
                    }
                });

                landingCard.setOnPreloadListener(new Banner.OnPreloadListener() {
                    @Override
                    public void onPreload() {
                        incLoadCursor();
                    }
                });
                landingCard.preload();
            } catch (JSONException e) {
                broadcCastPreloadError();
                throw new DioSdkException(e);
            }
        }


        try {
//            videoLoader = new FileLoader(getVideoAdUrl());
            videoLoader = new FileLoader(getVideoAdRawRes(getVideoAdUrl()));
            videoLoader.setListener(new FileLoader.OnLoadedListener() {
                @Override
                public void onLoaded() {
                    incLoadCursor();
                }
                @Override
                public void onLoadError() {
                    broadcCastPreloadError();
                }
            });
            videoLoader.exec();
        } catch (Exception e) {
            broadcCastPreloadError();
            throw new DioSdkException("could not preload video ad, loading landing card");
        }
    }
    protected boolean hasLandingCard() {
        return data.has("landingCard");
    }
    protected String getVideoAdUrl(){
        return data.optString("video");
    }
    protected int getVideoAdRawRes(String rawResName){
        return Controller.getInstance().getContext().getResources().getIdentifier(rawResName, "raw", Controller.getInstance().getContext().getPackageName());
    }
    protected abstract void setLandingCardFeatures();
    protected abstract void setVideoFeatures();

    protected void showLanding() {
            // this needs to go - feed container should take care of next ad
        try {
            landingCard.initContainer(context);
            setLandingCardFeatures();
            landingCard.render(context);

            player.hideView();
            player.pause();
            layout.removeAllViews();
            layout.addView(landingCard.getContainer().getLayout());
        } catch (Exception e) {
            errorListener.onError();
        }
    }

    protected void renderAdComponents() throws DioSdkException {
        layout = new FrameLayout(context);

        player = new VideoPlayer();
        setVideoFeatures();
        player.render(context);
        player.start(videoLoader.getResUri(), getDuration());
        callImpBeacon();
        videoLoader.getUri();

        player.setOnCompleteListener(new VideoPlayer.OnCompletionListener() {
            @Override
            public void onComplete() {
                if(finishListner != null) {
                    finishListner.onFinish();
                }
                if(hasLandingCard()) {
                    showLanding();
                }
            }
        });

        player.setOnErrorListener(new VideoPlayer.OnErrorListener() {
            @Override
            public void onError(int i ,int  i1,String url) {
                JSONObject errData = new JSONObject();
                try {
                    errData.put("placement", placementId);
                    errData.put("demand", "house");
                    if(isInterstitial()) {
                        errData.put("interstitial", true);
                    }
                    if(isInfeed()) {
                        errData.put("infeed", true);
                    }
                    if(url.matches("^/")) {
                        File file = new File(url);
                        errData.put("readable", file.canRead());
                        errData.put("size", file.length());
                        errData.put("ctime", file.lastModified());
                    }
                } catch (JSONException e) {

                }
                Controller.getInstance().logError("video error no." + Integer.toString(i) + "-" + Integer.toString(i1) + " when loading url " + url, "", errData);
                if(errorListener != null) {
                    errorListener.onError();
                }
            }
        });
        View playerView = player.getView();
        FrameLayout.LayoutParams vLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        vLp.gravity = Gravity.CENTER;
        playerView.setLayoutParams(vLp);
        layout.addView(playerView);
        layout.setForegroundGravity(Gravity.CENTER);


    }


    protected double getDuration() {
        return data.optDouble("duration");
    }
}
