package io.display.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TimeUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.display.sdk.ads.Ad;

public abstract class DioGenericActivity extends Activity {
    public static final String ORIENTATION_LANDSCAPE = "landscape";
    public static final String ORIENTATION_PORTRAIT = "portrait";
    String redirectAppId, redirectCpnId, prevUrl;
    private Boolean backEnabled = true;
    public Boolean suppressShutdownHandling = false;
    String placementId;
    Ad shownAd;
    WebView webv;

    OnRestartListener onRestartListener;
    private static final String TAG = "io.display.sdk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preCreate();
        super.onCreate(savedInstanceState);
        postCreate();
        Intent i = getIntent();
        switch (i.getStringExtra("cmd")) {
            case "renderAdComponents":
                try {
                    render();
                } catch (DioSdkException E) {
                    finish();
                }
                break;
            case "redirect":
                try {
                    redirect();
                } catch (Exception E) {
                    Log.e(TAG, "Click redirect failed due to an exception : " + E.toString());
                    E.printStackTrace();
                    finish();
                }
                break;
        }
    }

    abstract protected void preCreate();

    abstract protected void postCreate();

    private void redirect() {
        final DioGenericActivity activitRef = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Code for WebView goes here
                setBackEnabled(true);
                webv = new WebView(activitRef);
                Intent i = getIntent();
                if(i.hasExtra("appId") && i.hasExtra("cpnId")) {
                    redirectAppId = i.getStringExtra("appId");
                    redirectCpnId = i.getStringExtra("cpnId");
                }

                doRedirect(i.getStringExtra("clk"));
            }
        });

    }
    public void redirectToApp(final String url, final String appId, final String cpnId) {
        final DioGenericActivity activitRef = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webv = new WebView(activitRef);
                redirectAppId = appId;
                redirectCpnId = cpnId;
                doRedirect(url);
            }
        });
    }

    public void doRedirect(String url){
        Log.i(TAG, "Redirecting to ad click");

        webv.setWebViewClient(new WebViewClient() {
            int redirects = 0;
            long lastRedir = new Date().getTime();
            JSONArray stack = new JSONArray();
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverride(view, url);
            }
            @Override
            @RequiresApi(21)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest webRes) {
                return shouldOverride(view, webRes.getUrl().toString());
            }
            private boolean shouldOverride(WebView view,String url) {
                if (url != null) {
                    if (url.matches(".*://play.google.com.*")) {
                        url = url.replaceFirst(".*://play.google.com/.*/details", "market://details");
                    }
                    if (url.startsWith("market://")) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            finish();
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
            @Override
            public void onPageStarted(WebView view, final String url, Bitmap favi) {
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                if(hitTestResult != null && hitTestResult.getType() == 0) {
                    redirects++;
                }
                prevUrl = url;
                long now = new Date().getTime();
                try {
                    stack.put(new JSONObject().put("url", url).put("redirTime", now - lastRedir));
                } catch (JSONException e) {
                    // we dont build te stack for some readon - doesnt matter really
                }
                lastRedir = now;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                if(hitTestResult != null && hitTestResult.getType() == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (url.equals(prevUrl)) {
                                if (redirectAppId != null) {
                                    boolean redirectedCorrectly = (url.matches(".*://play.google.com.*") || url.startsWith("market://")) && url.matches(".*" + redirectAppId + ".*");
                                    Controller.getInstance().sendRedirectReporting(redirectAppId, redirectCpnId, redirectedCorrectly, url, stack, redirects - 1);// no reach
                                }
                            }
                        }
                    }, 1500);
                }

            }
        });

        webv.getSettings().setJavaScriptEnabled(true);
        try {
            webv.loadUrl(url);
            setContentView(webv);
        } catch (Exception e) {
            finish();
        }
    }


    private void render() throws DioSdkException {
        Controller ctrl = Controller.getInstance();
        Intent i = getIntent();
        placementId = i.getStringExtra("placement");
        if (!ctrl.placements.containsKey(placementId)) {
            throw new DioSdkException("placement " + placementId + " is not present when trying to renderAdComponents ad");
        }

        final Ad ad = ctrl.placements.get(placementId).getAd(i.getStringExtra("ad"));
        if (ad == null) {
            finish();
        } else {
            ad.setOnErrorListener(new Ad.OnErrorListener() {
                @Override
                public void onError() {
                    finish();
                }
            });
            shownAd = ad;
            ad.render(DioGenericActivity.this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!suppressShutdownHandling) {
            if(shownAd != null) {
                shownAd.detachActivityRefs();
            }
            Intent i = getIntent();
            placementId = i.getStringExtra("placement");
            Controller.getInstance().freeInterstitialLock();
            if(placementId != null)
                Controller.getInstance().triggerPlacementAction("onAdClose", placementId);
            Log.d("io.display.sdk", "Ending activity");
//            this.finish();
        }
    }
    public void requestOrientation(String oritentation)  {
        final int ovalue = getActityOrieentation(oritentation);
        //noinspection WrongConstant
        setRequestedOrientation(ovalue);

    }
    public int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    public boolean ensureOrientation(String orientation) {
        int confOrientation = (orientation == ORIENTATION_LANDSCAPE) ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT;
        if(getOrientation() != confOrientation) {
            suppressShutdownHandling = true;
        }
        requestOrientation(orientation);
        return getOrientation() == confOrientation;
    }
    protected int getActityOrieentation(String type) {
        int o =  ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
        switch(type) {
            case ORIENTATION_LANDSCAPE:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    o = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
                } else {
                    o = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                }
                break;
            case ORIENTATION_PORTRAIT:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    o = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
                } else {
                    o = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
                }
                break;
        }
        return o;
    }

    @Override
    public void onBackPressed() {
        if(backEnabled) {
            super.onBackPressed();
            Log.d("io.display.sdk", "Back pressed");
            this.finish();
        }
    }

    public void setBackEnabled(Boolean enabled) {
        backEnabled = enabled;
    }
    public void setOnRestartListener(OnRestartListener listener) {
        onRestartListener = listener;
    }
    protected void onRestart() {
        super.onRestart();
        if(onRestartListener != null) {
            onRestartListener.onRestart();
        }
    }
    public static abstract class OnRestartListener {
        public abstract void onRestart();
    }

}
