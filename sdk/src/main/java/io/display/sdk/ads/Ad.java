package io.display.sdk.ads;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import io.display.sdk.Controller;
import io.display.sdk.DioActivity;
import io.display.sdk.DioGenericActivity;
import io.display.sdk.DioSdkException;
import io.display.sdk.ads.supers.InfeedAdInterface;
import io.display.sdk.ads.supers.InterstitialAdInterface;

public abstract class Ad {
    public static final String TAG = "io.display.sdk.ads";
    public static final String ACTIVITY_TYPE_NORMAL = "normal";
    public static final String ACTIVITY_TYPE_TRANSLUCENT= "translucent";


    protected String placementId;
    protected JSONObject data;
    protected JSONObject offering;
    protected String id;
    protected boolean loaded = false;
    boolean multiLoadFired = false;
    private int multiLoadCount;
    private int multiLoadCountCursor = 0;

    protected DioGenericActivity activity;
    protected Context context;

    protected OnFinishListener finishListner;
    protected OnErrorListener errorListener;
    protected OnCloseListener closeListener;
    protected OnClickListener clickListener;
    protected OnStaticViewListener staticViewListener;
    protected ArrayList<OnPreloadListener> preloadListeners = new ArrayList<>();

    abstract public void preload() throws DioSdkException;
    abstract public void render(Context ctx) throws DioSdkException;
    protected void broadcastPreloadSuccess() {
        if(!loaded) {
            loaded = true;
            for (OnPreloadListener listener : preloadListeners) {
                listener.onLoaded();
            }
        }
    }
    protected void broadcCastPreloadError() {
        for(OnPreloadListener listener: preloadListeners) {
            listener.onError();
        }
    }
    public void setMultiLoadElmCount(int count) {
        multiLoadCount = count;
    }
    public void incLoadCursor() {
        multiLoadCountCursor++;
        if(multiLoadCountCursor >= multiLoadCount) {
            broadcastPreloadSuccess();

        }
    }

    public void addPreloadListener(OnPreloadListener listener) {
        preloadListeners.add(listener);
    }
    public Ad(String id, JSONObject data, JSONObject offering) {
        this.data = data;
        this.id = id;
        this.offering = offering;
    }
    public String getPlacementId() {
        return placementId;
    }

    public int getWidth() {
        return data.optInt("w");
    }

    public int getHeight() {
        return data.optInt("h");
    }
    public String getOrientation() {
        return (getHeight() > getWidth())? DioGenericActivity.ORIENTATION_PORTRAIT : DioGenericActivity.ORIENTATION_LANDSCAPE;
    }
    public boolean hasOfferDetails() {
        return offering != null;
    }
    public String getOffetType() {
        return offering.optString("type");
    }
    public String getOfferId() {
        return offering.optString("id");
    }
    public int getOfferCpn() {
        return offering.optInt("cpn");
    }
    public static Ad factory(String id, JSONObject data, JSONObject offering) {
        Ad ad = null;

        try {
            JSONObject adData = data.getJSONObject("data");
            switch (data.getString("type")) {
                case "interstitial":
                    ad = new Interstitial().factory(data.getString("subtype"), id, adData, offering);

                    break;
                case "infeed":
                    ad = new InFeed().factory(data.getString("subtype"), id, adData, offering);

                    break;
            }
        } catch (JSONException e) {
            return null;
        }
        return ad;
    }
    protected boolean isInterstitial() {
        return this instanceof InterstitialAdInterface;
    }
    protected boolean isInfeed() {
        return this instanceof InfeedAdInterface;
    }

    public String getId() {
        return id;
    }

    public String getActivityType() {
        return ACTIVITY_TYPE_NORMAL;
    }
    public void setPlacementId(String id) {
        placementId = id;
    }
    protected int getPxToDp(int valueInPx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) valueInPx, context.getResources().getDisplayMetrics());
    }
    public void detachActivityRefs() {
        if(activity != null) {
            activity = null;
        }
        if(context != null) {
            context =null;
        }
    }

    protected void redirect() {
        try {
            String url = data.getString("clk");

            redirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void callImpBeacon() {
        Log.d(TAG, "Calling impression beacon for ad in placement " + placementId );
        String impUrl = data.optString("imp");
        if(impUrl != null) {
            callBeacon(impUrl);
        }
        Controller.getInstance().triggerPlacementAction("onAdShown", getPlacementId());
    }
    protected void redirect(String url) {
        try {
            Controller.getInstance().triggerPlacementAction("onAdClick", placementId);
            String offerType = offering.optString("type");
            if(context instanceof DioGenericActivity) {
                if ("app".equals(offerType)) {
                    ((DioGenericActivity) context).redirectToApp(url, offering.optString("id"), offering.optString("cpn"));
                } else {
                    ((DioGenericActivity) context).doRedirect(url);
                }
            } else {
                Intent intent = new Intent(context, DioActivity.class);
                intent.putExtra("clk", url);
                intent.putExtra("cmd", "redirect");
                if ("app".equals(offerType)) {
                    intent.putExtra("appId", offering.optString("id"));
                    intent.putExtra("cpnId", offering.optString("cpn"));
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void callBeacon(String url) {
        CallBeaconTask beaconTask = new CallBeaconTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            beaconTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            beaconTask.execute(url);
        }
    }






    protected static class CallBeaconTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground (String[] url) {
            try {
                URL aURL = new URL(url[0]);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }



    public void setOnFinishListener(OnFinishListener listener) {
        finishListner = listener;
    }
    public void setOnErrorListener(OnErrorListener listener) {
        errorListener = listener;
    }
    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }
    public void setOnCloseListener(OnCloseListener listener) {
        closeListener = listener;
    }
    public void setOnStaticAdViewListener(OnStaticViewListener listener) {
        staticViewListener = listener;
    }

    public static abstract class OnStaticViewListener {
        public abstract void onView();
    }
    public static abstract class OnFinishListener {
        public abstract void onFinish();
    }
    public static abstract class OnErrorListener {
        public abstract void onError();
    }
    public static abstract class OnClickListener {
        public abstract void onClick();
    }
    public static abstract class OnCloseListener {
        public abstract void onClose();
    }
    abstract static public class OnPreloadListener {

        public abstract void onError();

        public abstract void onLoaded();

    }
}
