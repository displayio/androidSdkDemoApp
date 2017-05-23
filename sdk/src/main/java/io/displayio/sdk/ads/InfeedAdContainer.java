package io.displayio.sdk.ads;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.displayio.sdk.Controller;
import io.displayio.sdk.DioSdkException;
import io.displayio.sdk.Placement;
import io.displayio.sdk.ads.supers.InfeedAdInterface;


/**
 * Created by jynx on 27/11/16.
 */

public class InfeedAdContainer {
    public static final int VIEWED = 1;
    public static final int NOT_VIEWED = 0;
    public static final int LOCK_OCCUPIED = 1;
    public static final int LOCK_FREE = 0;
    int loadLock = LOCK_FREE;
    Ad ad;
    InfeedView layout;
    Context ctx;
    Placement placement;
    String placementId;
    View currentView;
    OnHeightUpdatesListener onHeightsUpdateListener;

    public InfeedAdContainer(Context ctx, String placementId) {
        this.ctx = ctx;
        layout = new InfeedView(ctx, placementId);
        this.placementId = placementId;
        load();
    }

    public View getContainerView() {
        return layout;
    }

    public int getFeedAdHeight() {
        if(ad == null) {
            return 0;
        }
        float rel = (float)ad.getWidth() / ad.getHeight();
        int devWidth = Controller.getInstance().deviceDescriptor.getPxWidth();
        return rel > 0 ? Math.round(devWidth / rel) : 0;
    }
    public void bindTo(final ViewGroup attachedTo) {
        ViewGroup.LayoutParams rlParams =  attachedTo.getLayoutParams();
        rlParams.height = getFeedAdHeight();
        attachedTo.setLayoutParams(rlParams);
        ViewGroup curParent = (ViewGroup)layout.getParent();
        if(curParent != null) {
            curParent.removeView(layout);
        }
        attachedTo.addView(layout);
        setHeightUpdatesListener(new OnHeightUpdatesListener() {
            @Override
            public void onUpdate(int newHeight) {
                ViewGroup.LayoutParams rlParams =  attachedTo.getLayoutParams();
                rlParams.height = getFeedAdHeight();
                attachedTo.setLayoutParams(rlParams);
            }
        });
    }
    public void updateContext(Context ctx) {
        this.ctx = ctx;
    }
    private void loadDelayed(int timeout) {
        loadDelayed(timeout, 0);
    }
    private void loadDelayed(int timeout, final int retryCount) {
        if(loadLock == LOCK_FREE) {
            loadLock = LOCK_OCCUPIED;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadLock = LOCK_FREE;
                    load(retryCount);
                }
            }, timeout);
        }

    }

    private void loadAd(Ad givenAd) throws DioSdkException {
        if(givenAd!= null) {
            if(!(givenAd instanceof InfeedAdInterface)) {
                throw new DioSdkException("trying to load a non-infeed ad as infeed");

            }
            ad = givenAd;
            // only videos ads emit onFinish
            ad.setOnFinishListener(new Ad.OnFinishListener() {
                @Override
                public void onFinish() {
                    loadDelayed(7000);
                }
            });
            ad.setOnStaticAdViewListener(new Ad.OnStaticViewListener() {
                @Override
                public void onView() {
                    loadDelayed(18000);
                }
            });
            ad.setOnErrorListener(new Ad.OnErrorListener() {
                @Override
                public void onError() {
                    if (onHeightsUpdateListener != null) {
                        Log.d("debug", "updating height 0");
                        onHeightsUpdateListener.onUpdate(0);
                    }
                    loadDelayed(7000);
                }
            });
            try {
                ad.render(ctx);

                final View adView = ((InfeedAdInterface) ad).getView();
                if (adView != null) {
                    if (ad.loaded) {
                        transitionView(((InfeedAdInterface) ad).getView());
                    } else {
                        ad.addPreloadListener(new Ad.OnPreloadListener() {
                            @Override
                            public void onError() {
                                loadDelayed(2500);

                            }

                            @Override
                            public void onLoaded() {
                                transitionView(((InfeedAdInterface) ad).getView());

                            }
                        });
                    }

                } else {
                    loadDelayed(2500);
                }
            } catch (Exception e) {
                loadDelayed(5000);
            }
        } else {
            loadDelayed(5000);

        }
    }

    private void load() {
        load(0);
    }

    private void load(int retryCount) {
        try {
            Controller ctrl = Controller.getInstance();
            if (ctrl.isInitialized()) {
                if(placement == null) {
                    if (ctrl.placements.containsKey(placementId)) {
                        placement = ctrl.placements.get(placementId);
                    } else {
                        throw new DioSdkException("no placement found with id " + placementId + " on app " + ctrl.getAppId() + " when trying to load infeed ad");
                    }
                }
                if(placement != null) {
                    if(!placement.isOperative()) {
                        Log.i(Ad.TAG, "placement " + placement.getName() + " not operative, mot loading ads into ad container");
                    } else {

                        if (!placement.hasAd()) {
                            int secs = 10 + retryCount * 5;
                            Log.i(Ad.TAG, "no ads for placement " + placementId + ", trying to reload in " + secs + " seconds");
                            ctrl.deviceDescriptor.updateDeviceResolution(ctx);
                            placement.refetch();
                            loadDelayed(secs * 1000, ++retryCount);
                        } else {
                            loadAd(placement.getNextAd());
                        }
                    }
                }
            } else {
                Log.i(Ad.TAG, "when trying to load ad into placement " + placementId + ", SDK uninitialized, will retry in 5 seconds");
                loadDelayed(5000);
            }
        } catch (DioSdkException e) {
            e.printStackTrace();
        }
    }
    private void transitionView(final View newView) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            if(currentView != null) {
                currentView.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onHeightsUpdateListener != null) {
                            onHeightsUpdateListener.onUpdate(getFeedAdHeight());
                        }
                    }
                });
            }
            newView.setAlpha(0f);
            layout.addView(newView);
            //layout.animate().
            newView.animate().alpha(1f).setDuration(2000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(currentView != null) {
                        layout.removeView(currentView);
                    }
                    currentView = newView;

                }
            });
        } else {
            if(currentView != null) {
                layout.removeView(currentView);
            }
            layout.addView(newView);
            if (onHeightsUpdateListener != null) {
                onHeightsUpdateListener.onUpdate(getFeedAdHeight());
            }
            currentView = newView;
        }

    }
    public void setHeightUpdatesListener(OnHeightUpdatesListener listener) {
        onHeightsUpdateListener = listener;
    }

    public Boolean hasAd() {
        return (ad != null);
    }
    public static abstract class OnHeightUpdatesListener {
        public abstract void onUpdate(int newHeight);
    }
    class InfeedView extends FrameLayout {
        String placementId;
        public InfeedView(Context context, String placementId) {
            super(context);
            this.placementId = placementId;
        }

    }
}
