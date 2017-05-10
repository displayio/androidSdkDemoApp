package io.display.sdk.ads;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

import org.json.JSONObject;

import io.display.sdk.Controller;
import io.display.sdk.DioActivity;
import io.display.sdk.DioGenericActivity;
import io.display.sdk.DioSdkException;
import io.display.sdk.ads.components.Banner;
import io.display.sdk.ads.components.Container;
import io.display.sdk.ads.components.VideoPlayer;
import io.display.sdk.ads.supers.BannerAd;
import io.display.sdk.ads.supers.HouseVideoAd;
import io.display.sdk.ads.supers.HtmlAd;
import io.display.sdk.ads.supers.InterstitialAdInterface;
import io.display.sdk.ads.supers.VastAd;

public class Interstitial {
     Ad factory(String sType, String id, JSONObject data, JSONObject offering) {
        Ad factoredAd = null;
        switch (sType) {
            case "video":
                factoredAd = new InterstitialHouseVideo(id, data, offering);
                break;
            case "videoVast":
                factoredAd = new InterstitialVast(id, data, offering);
                break;
            case "banner":
                factoredAd = new InterstitialHouseBanner(id, data, offering);
                break;
            case "html":
                factoredAd = new InterstitialHtml(id, data, offering);
                break;
        }
        return factoredAd;
    }


    class InterstitialVast extends VastAd implements InterstitialAdInterface {
        public InterstitialVast(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }
        protected void setVideoFeatures() {
            player.setFeature(VideoPlayer.FEATURE_SHOW_TIMER, true);
            player.setFeature(VideoPlayer.FEATURE_SKIPPABLE, true);
            player.setFeature(VideoPlayer.FEATURE_CONTINUOUS, true);
        }
        public void render(Context dioActivity)  {
            context = dioActivity;
            activity = (DioActivity)dioActivity;
            try {
                parseMediaFile();
                boolean correctOrientation = activity.ensureOrientation(getHeight() > getWidth() ? DioGenericActivity.ORIENTATION_PORTRAIT: DioGenericActivity.ORIENTATION_LANDSCAPE);
                if(correctOrientation) {
                    renderAdComponents();
                    activity.setBackEnabled(false);
                    player.setOnCompleteListener(new VideoPlayer.OnCompletionListener() {
                        @Override
                        public void onComplete() {
                            Controller.getInstance().triggerPlacementAction("onAdCompleted", placementId);
                            activity.finish();
                        }
                    });
                    player.setOnSkipListener(new VideoPlayer.OnSkipListener() {
                        @Override
                        public void onSkip() {
                            activity.finish();
                        }
                    });
                    View video = player.getView();
                    video.setBackgroundColor(Color.BLACK);
                    activity.setContentView(video);
                    playFromWeb();
                    activity.setOnRestartListener(new DioGenericActivity.OnRestartListener() {
                        public void onRestart() {
                            player.showLoader();
                        }
                    });
                }
            } catch (DioSdkException e) {
                e.printStackTrace();
                if(errorListener != null) {
                    errorListener.onError();
                }
            }
        }
    }


    class InterstitialHouseVideo extends HouseVideoAd implements InterstitialAdInterface {
        protected void setVideoFeatures() {
            player.setFeature(VideoPlayer.FEATURE_CLICK_BOX, true);
            int skipIn = data.optInt("skippableIn", 0);
            if(skipIn > 0) {
                player.setFeature(VideoPlayer.FEATURE_SKIPPABLE, true);
                player.setOption(VideoPlayer.OPTION_SKIP_AFTER, skipIn);
                player.setOnSkipListener(new VideoPlayer.OnSkipListener() {
                    @Override
                    public void onSkip() {
                        showLanding();
                    }
                });
            }
            player.setFeature(VideoPlayer.FEATURE_SHOW_TIMER, true);
            player.setOption(VideoPlayer.OPTION_CLICK_BOX_TEXT, "Get Application");
            player.setOnClickListener(new VideoPlayer.OnClickListener() {
                @Override
                public void onClick() {
                    player.pause();
                    showLanding();
                    redirect();
                }
            });
            player.setOnCompleteListener(new VideoPlayer.OnCompletionListener() {
                @Override
                public void onComplete() {
                    Controller.getInstance().triggerPlacementAction("onAdCompleted", placementId);
                }
            });
        }
        protected void setLandingCardFeatures() {
            landingCard.getContainer().setFeature(Container.FEATURE_CLOSE_BUTTON, true);
            landingCard.getContainer().setFeature(Container.FEATURE_ROTATE, true);
            landingCard.getContainer().setOnCloseListener(new Container.OnCloseListener() {
                @Override
                public void onClose() {
                    activity.finish();
                }
            });
        }



        public void render(Context dioActivity) {
            activity = (DioGenericActivity) dioActivity;
            boolean correctOrientation = activity.ensureOrientation(getOrientation());
            if(correctOrientation) {
                activity.setBackEnabled(false);
                context = dioActivity;
                try {
                    renderAdComponents();
                    layout.setBackgroundColor(Color.BLACK);
                    activity.setContentView(layout);
                    player.setOnErrorListener(new VideoPlayer.OnErrorListener() {
                        @Override
                        public void onError(int i ,int  i1,String url) {
                            showLanding();
                        }
                    });
                } catch (DioSdkException e) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                    activity.finish();
                }
            }
        }
        protected void showLanding() {
            if(activity != null) {
                activity.setBackEnabled(true);
                super.showLanding();
                if (!hasLandingCard()) {
                    activity.finish();
                }
            }
        }

        public InterstitialHouseVideo(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }

    }

    class InterstitialHouseBanner extends BannerAd implements InterstitialAdInterface {
        public InterstitialHouseBanner(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }
        public void render(final Context dioAcitivity) {
            activity = (DioGenericActivity)dioAcitivity;
            boolean correctOrientation = activity.ensureOrientation(getOrientation());
            if(correctOrientation) {
                context = activity;
                banner.initContainer(activity);
                Container container = banner.getContainer();
                container.setFeature(Container.FEATURE_CLOSE_BUTTON, true);
                container.setOption(Container.OPTION_CLOSE_BUTTON_DELAY, 5000);
                container.setOption(Container.OPTION_PADDING_HORIZONTAL, 5);
                container.setOption(Container.OPTION_PADDING_VERTICAL, 5);
                container.setFeature(Container.FEATURE_ROTATE, true);
                banner.setFeature(Banner.FEATURE_ROUND_FRAME, true);
                try {
                    banner.render(activity);
                    banner.setOnErrorListener(new Banner.OnErrorListener() {
                        @Override
                        public void onError() {
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    });
                    container.setOnCloseEnabledListener(new Container.OnCloseEnabledListener() {
                        @Override
                        public void onCloseEnabled() {
                            if (activity != null && !activity.isFinishing())
                                activity.setBackEnabled(true);
                        }
                    });
                    container.setOnCloseListener(new Container.OnCloseListener() {
                        @Override
                        public void onClose() {
                            if (closeListener != null) {
                                closeListener.onClose();
                            }
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    });
                    container.setOnOpenListener(new Container.OnOpenListener() {
                        @Override
                        public void onOpen() {
                            callImpBeacon();
                            banner.setOnClickListener(new Banner.OnClickListener() {
                                @Override
                                public void onClick() {
                                    redirect();
                                }
                            });
                        }
                    });

                    activity.setContentView(container.getLayout());
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.finish();
                }
            }
        }
        public String getActivityType() { return Ad.ACTIVITY_TYPE_TRANSLUCENT; }
    }




    class InterstitialHtml extends HtmlAd  implements InterstitialAdInterface {
        public InterstitialHtml(String id, JSONObject data, JSONObject offering) {
            super(id, data, offering);
        }
        public void render(Context dioActivity) throws DioSdkException {
            activity = (DioGenericActivity)dioActivity;
            context = dioActivity;
            renderComponents();
            callImpTracking();
            activity.setContentView(container.getLayout());
        }
        public void setupContainerFeatures() {
            container.setFeature(Container.FEATURE_CLOSE_BUTTON, true);
            container.setFeature(Container.FEATURE_ROTATE, true);
            int adFramePad = getPxToDp(5);

            container.setOption(Container.OPTION_PADDING_VERTICAL, 5);
            container.setOption(Container.OPTION_PADDING_HORIZONTAL, 5);
            container.setOption(Container.OPTION_CLOSE_BUTTON_DELAY, 5000);
            container.setOnCloseListener(new Container.OnCloseListener() {
                @Override
                public void onClose() {
                    activity.finish();
                }
            });
            View  adView = container.getContainedView();
            adView.setPadding(adFramePad, adFramePad, adFramePad, adFramePad);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { Color.parseColor("#AAAAAA"), Color.parseColor("#DDDDDD")});
                bg.setCornerRadius(15);
                bg.setStroke(3, Color.parseColor("#000000"));
                adView.setBackground(bg);
            }
            activity.setBackEnabled(false);
            container.setOnCloseEnabledListener(new Container.OnCloseEnabledListener() {
                @Override
                public void onCloseEnabled() {
                    activity.setBackEnabled(true);
                }
            });
        }
        public String getActivityType() {
            return Ad.ACTIVITY_TYPE_TRANSLUCENT;
        }
    }
}