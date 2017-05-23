package io.displayio.sdk.ads.supers;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.json.JSONObject;

import io.displayio.sdk.Controller;
import io.displayio.sdk.ads.Ad;
import io.displayio.sdk.ads.components.Container;

public abstract class HtmlAd extends Ad {
    protected Container container;
    protected WebView webView;
    public HtmlAd(String id, JSONObject data, JSONObject offering) {
        super(id, data, offering);
    }
    public void renderComponents() {
        container = new Container(context);
        webView = new WebView(context);
        String markup = data.optString("markup", "<html/>");
        webView.loadData(markup,"text/html", "UTF-8");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        boolean jsEnabled = data.optBoolean("jsEnabled", false);
        webView.getSettings().setJavaScriptEnabled(jsEnabled);
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true) ;
        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.BASE)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                callClickTracking();
                redirect(url);
                return true;
            }
            @TargetApi(24)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,  WebResourceRequest request) {
                super.shouldOverrideUrlLoading(view, request);
                callClickTracking();

                redirect(request.getUrl().toString());
                return true;
            }

        });
        int devHeight = Controller.getInstance().deviceDescriptor.getPxHeight();

        webView.setPadding(0, 0, 0, 0);
        FrameLayout adLayout = new FrameLayout(context);
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        adLayout.addView(webView);
        container.setView(adLayout);
        setupContainerFeatures();
        container.render();
        Double val = new Double(devHeight)/new Double(webView.getContentHeight());
        int scale = (int)Math.round(val * 100d);
        webView.setInitialScale(scale);
    }
    abstract public void setupContainerFeatures();

    protected void callClickTracking() {
        String clickUrl = data.optString("clickTracking");
        if(clickUrl != null) {
            callBeacon(clickUrl);
        }
    }
    protected void callImpTracking() {
        String impUrl = data.optString("imp");
        Controller.getInstance().triggerPlacementAction("onAdShown", getPlacementId());
        if(impUrl != null) {
            callBeacon(impUrl);
        }
    }


    public void preload() {
        broadcastPreloadSuccess();
    }
}
