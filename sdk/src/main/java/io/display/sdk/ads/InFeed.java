package io.display.sdk.ads;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.json.JSONObject;

import io.display.sdk.DioSdkException;
import io.display.sdk.ads.components.Banner;
import io.display.sdk.ads.components.Container;
import io.display.sdk.ads.components.VideoPlayer;
import io.display.sdk.ads.supers.BannerAd;
import io.display.sdk.ads.supers.HouseVideoAd;
import io.display.sdk.ads.supers.HtmlAd;
import io.display.sdk.ads.supers.InfeedAdInterface;
import io.display.sdk.ads.supers.VastAd;


public class InFeed {
    Ad factory(String sType, String id, JSONObject data, JSONObject offering) {
        Ad factoredAd = null;
        switch (sType) {
            case "video":
                factoredAd = new InFeedHouseVideo(id, data, offering);
                break;
            case "videoVast":
                factoredAd = new InFeedVast(id, data, offering);
                break;
            case "banner":
                factoredAd = new InFeedHouseBanner(id, data, offering);
                break;
            case "html":
                factoredAd = new InFeedHtml(id, data, offering);
                break;
        }
        return factoredAd;
    }

    class InFeedVast extends VastAd  implements InfeedAdInterface {
        public InFeedVast(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }
        public void render(Context ctx) throws DioSdkException {
            context = ctx;
            renderAdComponents();
            playFromFile();
        }
        protected void setVideoFeatures() {
            player.setFeature(VideoPlayer.FEATURE_SOUND_CONTROL, true);
            player.setSound(false);
            player.setFeature(VideoPlayer.FEATURE_CONTINUOUS, true);
        }
    }

    class InFeedHouseVideo extends HouseVideoAd implements InfeedAdInterface {
        protected void setVideoFeatures() {
            player.setFeature(VideoPlayer.FEATURE_CLICK_BOX, true);
            player.setOption(VideoPlayer.OPTION_CLICK_BOX_TEXT, "Get Application");
            player.setFeature(VideoPlayer.FEATURE_CONTINUOUS, true);
            player.setFeature(VideoPlayer.FEATURE_SOUND_CONTROL, true);
            player.setOnClickListener(new VideoPlayer.OnClickListener() {
                @Override
                public void onClick() {
                    redirect();
                }
            });
            player.setSound(false);
        }
        protected void setLandingCardFeatures() {

        }
        public InFeedHouseVideo(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }
        public int getWidth() {
            return data.optInt("vwidth");
        }
        public int getHeight() {
            return data.optInt("vheight");
        }
        public void render(Context ctx) throws DioSdkException {
            context = ctx;
            renderAdComponents();
            layout.setBackgroundColor(Color.TRANSPARENT);

        }

        public View getView() {
            return layout;
        }
    }

    class InFeedHouseBanner extends BannerAd implements InfeedAdInterface {
        public InFeedHouseBanner(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }

        public void render(Context ctx) throws DioSdkException {
            context = ctx;
            banner.initContainer(context);
            banner.render(context);
            banner.getContainer().setOnOpenListener(new Container.OnOpenListener() {
                @Override
                public void onOpen() {
                    callImpBeacon();
                    if(staticViewListener != null) {
                        staticViewListener.onView();
                    }

                }
            });
            banner.setOnClickListener(new Banner.OnClickListener() {
                @Override
                public void onClick() {
                    redirect();
                }
            });

        }
        public View getView() {
            return banner.getContainer().getLayout();
        }
    }

    class InFeedHtml extends HtmlAd  implements InfeedAdInterface {
        public InFeedHtml(String id, JSONObject data,JSONObject offering) {
            super(id, data, offering);
        }
        public void render(Context ctx) {
            context = ctx;
            renderComponents();
            container.setOnOpenListener(new Container.OnOpenListener() {
                @Override
                public void onOpen() {
                    callImpTracking();
                    if(staticViewListener != null) {
                        staticViewListener.onView();
                    }
                }
            });
        }
        public View getView() {
            return container.getLayout();
        }
        @Override
        public void setupContainerFeatures() {


        }
    }

}
