package io.display.sdk.ads.supers;

import android.net.Uri;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.display.sdk.Controller;
import io.display.sdk.DioSdkException;
import io.display.sdk.ads.Ad;
import io.display.sdk.ads.components.VideoPlayer;
import io.display.sdk.ads.tools.FileLoader;

/**
 * Created by jynx on 22/12/16.
 */

public abstract class VastAd extends Ad {
    protected VideoPlayer player;
    protected JSONObject videoData;
    protected String url;
    protected int duration;
    public VastAd(String id, JSONObject data, JSONObject offering) {
        super(id, data, offering);
    }
    protected void renderAdComponents() throws DioSdkException {
        parseMediaFile();

        if(videoData == null) {
            throw new DioSdkException("bad video mediafile data in vast ad", data);
        }
        url = videoData.optString("url");
        duration = data.optInt("duration", 0);
        if(url == null) {
            throw new DioSdkException("couldn't find vast video url");
        }
        if(duration == 0) {
            throw new DioSdkException("couldn't find vast video duration");
        }
        JSONObject events = data.optJSONObject("trackingEvents");

        if(events == null) {
           events = new JSONObject();
        }
        JSONArray clickEvents = data.optJSONArray("clickTracking");
        if(clickEvents != null) {
            try {
                events.putOpt("click", clickEvents);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONArray impEvents = data.optJSONArray("impressions");
        if(clickEvents != null) {
            try {
                events.putOpt("impression", impEvents);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        player = new VideoPlayer();

        player.loadEvents(events);
        player.setOnStartListener(new VideoPlayer.OnStartListener() {
            @Override
            public void onStart() {
                Controller.getInstance().triggerPlacementAction("onAdShown", getPlacementId());
            }
        });
        final String clickUrl = data.optString("clickUrl", null);
        if(clickUrl != null) {
            player.setOnClickListener(new VideoPlayer.OnClickListener() {
                @Override
                public void onClick() {
                    if(clickListener != null) {
                        clickListener.onClick();
                    }
                    redirect(clickUrl);
                }
            });
        }
        if(data.has("skippableIn")) {
            int skipAfter = data.optInt("skippableIn", 0);
            player.setOption(VideoPlayer.OPTION_SKIP_AFTER, skipAfter);
        }
        player.setOnCompleteListener(new VideoPlayer.OnCompletionListener() {
            @Override
            public void onComplete() {

                if(finishListner != null) {
                    finishListner.onFinish();
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
        player.setFeature(VideoPlayer.FEATURE_CLICK_BOX, true);
        setVideoFeatures();
        player.render(context);
    }
    protected void playFromWeb() throws DioSdkException {
        player.start(Uri.parse(url), duration);
    }

    protected void playFromFile() {
        final FileLoader loader = new FileLoader(url);
        loader.setListener(new FileLoader.OnLoadedListener() {
            @Override
            public void onLoaded() {
                try {
                    player.hideLoader();
                    player.start(loader.getUri(), duration);

                } catch (DioSdkException e) {
                    if(errorListener != null) {
                        errorListener.onError();
                    }
                }
            }

            @Override
            public void onLoadError() {
                try {
                    player.hideLoader();
                    player.start(Uri.parse(url), duration);

                } catch (DioSdkException e) {
                    if(errorListener != null) {
                        errorListener.onError();
                    }
                }

            }
        });
        player.showLoader();
        loader.exec();
    }
    abstract protected void setVideoFeatures();
    public int getWidth() {
        return videoData.optInt("width");
    }
    public int getHeight() {
        return videoData.optInt("height");
    }
    public void parseMediaFile() throws DioSdkException {
        JSONArray videos = data.optJSONArray("videos");
        if(videos == null) {
            throw new DioSdkException("no videos in vast ad", data);
        }
        if(videos.length() == 0) {
            throw new DioSdkException("empty video list in vast ad", data);
        }
        videoData = videos.optJSONObject(0);
    }
    public void preload() {
        broadcastPreloadSuccess();
    }

    public View getView() {
        return player.getView();
    }
}
