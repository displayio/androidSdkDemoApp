package io.displayio.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import io.displayio.sdk.ads.Ad;
import io.displayio.sdk.ads.InfeedAdContainer;
import io.displayio.sdk.ads.supers.InterstitialAdInterface;
import io.displayio.sdk.device.DeviceDescriptor;
import io.displayio.sdk.device.DeviceEventsListener;
import io.displayio.sdk.device.PermissionsHandler;

public class Controller {
    private static final String TAG = "io.display.sdk";
    public static final String AD_INFEED = "infeed";
    public static final String AD_INTERSTITIAL = "interstitial";
    static Controller Instance;
    public DeviceDescriptor deviceDescriptor;
    public HashMap<String, Placement> placements = new HashMap<>();
    public ErrorLogger errLogger;
    private ServiceClient serviceClient;
    private EventListener listener = null;
    private ArrayList<EventListener> internalEventListeners = new ArrayList<>();
    private Context context;
    private Context activityContext;
    private Boolean interstitialLock = false;
    private boolean mUseLocation = false;
    public static final int REQUEST_CODE_ASK_GEO_PERMISSIONS = 100;
    String appId;
    Boolean initialized = false, deviceReady = false, initializing = false;
    private boolean mDoInitialFetch = false;
    private boolean mDeviceGeoProcessed = false;
    private boolean mProcessingPermissions = false;
    private boolean adReadyToDisplay;
    private HashMap<String, InfeedAdContainer> feedContainers = new HashMap<>();
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String STATUS_ENABLED = "enabled";

    private Boolean obtainInterstitialLock() {
        if (!interstitialLock) {
            interstitialLock = true;
            return true;
        }
        return false;
    }

    public void freeInterstitialLock() {
        interstitialLock = false;
    }

    private Controller() {
        errLogger = new ErrorLogger(appId);
        serviceClient = new ServiceClient(this);
//        placements = new HashMap<>();
    }

    public static Controller getInstance() {
        if (Instance == null) {
            Instance = new Controller();
        }
        return Instance;
    }

    public Boolean isInitialized() {
        return initialized;
    }

    public void init(Activity activtyContext, String appId) {
        if (initialized || initializing) {
            return;
        }
        doInitialize(activtyContext, appId);
    }

    public void forceInit(Activity activity) {
        initialized = true;
        deviceReady = true;
        context = activity.getApplicationContext();
        activityContext = activity;
        deviceDescriptor = new DeviceDescriptor(activity, new DeviceEventsListener() {
            @Override
            public void onDeviceIdRetrieved() {
            }
        });
    }

    void sendRedirectReporting(String bundleId, String cpnId, boolean redirected, String endUrl, JSONArray stack, int numRedirects) {
        try {
            serviceClient.sendRedirectReporting(appId, bundleId, cpnId, redirected, endUrl, stack, numRedirects);
        } catch (DioSdkException e) {
            e.printStackTrace();
        }
    }

    void processCommands(JSONArray commands) {
        if (commands != null) {
            for (int x = 0; x < commands.length(); x++) {
                JSONObject command = commands.optJSONObject(x);
                if (command != null) {
                    switch (command.optString("type")) {
                        case "getApps":
                            JSONArray apps = deviceDescriptor.getInstalledAppList(context);
                            serviceClient.reportAppList(appId, apps);
                            break;
                    }
                }
            }
        }
    }

    public void doInitialize(Activity activityContext, String appId) {
        Log.i("io.display.sdk", "initializing");
        initialized = false;
        initializing = true;
        this.appId = appId;
        this.activityContext = activityContext;
        this.context = activityContext.getApplicationContext();
        if (BuildConfig.IS_DEBUG && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // cleaning legacy files
        cleanMediaFiles();
        final Thread.UncaughtExceptionHandler appHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                try {
                    String trace = Log.getStackTraceString(e);
                    if (trace.matches("(?is).*io.display.sdk.*")) {
                        logError("uncauught fatal exception : " + e.toString(), trace);
                    }
                    if (appHandler != null) {
                        appHandler.uncaughtException(t, e);
                    }
                } catch (Exception e2) {

                }
            }
        });
        deviceDescriptor = new DeviceDescriptor(activityContext, new DeviceEventsListener() {
            @Override
            public void onDeviceIdRetrieved() {
                setDeviceReady();
                mDoInitialFetch = true;
                if (!mDeviceGeoProcessed && !mProcessingPermissions) {
                    onGeoPermissionRequestResult();
                    mDeviceGeoProcessed = false;
                }
            }

            @Override
            public void onGeoPermissionRequestResult() {
                if (mDoInitialFetch) {
                    fetchPlacements();
                    mDoInitialFetch = false;
                }
                mDeviceGeoProcessed = true;
            }
        });

        /**
         * geo permission check
         * then retrieve the device's geo location if possible and continue
         */
        if (!isPermissionForDeviceGeoLocationGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getGeoPermRequestEnabled()) {
                    mProcessingPermissions = true;
                    getActivityContext().startActivity(new Intent(getActivityContext(), PermissionsHandler.class));
                }
            }
        } else {
            mProcessingPermissions = false;
            getLocation();
        }

        placements = new HashMap<>();
    }

    private void cleanMediaFiles() {
        File[] filesDir = getContext().getFilesDir().listFiles();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < filesDir.length; i++) {
            File f = filesDir[i];

            long lastChangeTs = filesDir[i].lastModified();
            float fileAgeDays = (float) (currentTime - lastChangeTs) / (24 * 60 * 60 * 1000);
            if (filesDir[i].getName().contains(".") && fileAgeDays > 2)
                if (!filesDir[i].delete())
                    Log.d(TAG, "file " + filesDir[i] + " could not be deleted");
        }
    }


    /**
     * method to check the geo location if enabled
     *
     * @return true if permission is granted
     */
    private boolean isPermissionForDeviceGeoLocationGranted() {
        try {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } catch (NoClassDefFoundError e) {

            return false;
        }
    }

    public void setG(String g) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            serviceClient.add("forceGeo", g);
        }
    }

    public void setH(String i, double ln, double lg) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            serviceClient.add("emulatedIp", i);
        }
    }

    public void setCourse(String course) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            serviceClient.uri = course;
        }
    }

    public void triggerPlacementAction(String action, String placement) {
        Log.d(TAG, "Trigger " + action + "() for placement " + placement);

        if (listener != null) {
            switch (action) {
                case "onAdClick":
                    listener.onAdClick(placement);
                    break;
                case "onAdShown":
                    listener.onAdShown(placement);
                    break;
                case "onNoAds":
                    listener.onNoAds(placement);
                    break;
                case "onAdClose":
                    listener.onAdClose(placement);
                    break;
                case "onAdCompleted":
                    listener.onAdCompleted(placement);
                case "onAdReady":
                    adReadyToDisplay = true;
                    listener.onAdReady(placement);
            }
        }
    }

    void fetchPlacements() {
        try {
            serviceClient.getPlacements(appId, new ServiceClient.ServiceResponseListener() {
                public void onSuccessResponse(JSONObject resp) {
                    try {
                        if (!resp.has("placements")) {
                            throw new DioSdkException("bad getPlacements() response, no placements");
                        }

                        JSONObject plcmnts = resp.getJSONObject("placements");
                        Iterator pkeys = plcmnts.keys();

                        while (pkeys.hasNext()) {
                            try {
                                String placementId = (String) pkeys.next();
                                //checking for empty adss
                                JSONObject plcData = plcmnts.getJSONObject(placementId);
                                Placement placement = new Placement(placementId);
                                placement.setup(plcData);
                                placements.put(placementId, placement);
                            } catch (DioSdkException | JSONException e) {
                                onError(e.getMessage(), resp);
                            }

                        }
                        setInitialized();

                    } catch (DioSdkException | JSONException e) {
                        onError(e.getMessage(), resp);
                    }
                }

                public void onErrorResponse(JSONObject resp) {
                    String serial = "";
                    if (resp != null) {
                        serial = resp.toString();
                    }
                    onInitError("badly formed response : " + serial);
                }

                public void onErrorResponse(String msg, JSONObject resp) {
                    String serial = "";
                    if (resp != null) {
                        serial = resp.toString();
                    }
                    onInitError(msg + ". response : " + serial);
                }

                public void onError(String msg, JSONObject resp) {
                    String serial = "";
                    if (resp != null) {
                        serial = resp.toString();
                    }
                    onInitError(msg + ". response : " + serial);
                }
            });
        } catch (DioSdkException e) {
            onInitError(e.getMessage());
        }
    }

    // this stupid access point is used by the b4a plugin -
    // should be replaced by a placement specific implementation ASAP
    public boolean isAdReadyToDisplay() {
        return adReadyToDisplay;
    }

    private void setInitialized() {
        initialized = true;
        if (deviceReady) {
            onInit();
        }
    }

    private void setDeviceReady() {
        deviceReady = true;
        if (initialized) {
            onInit();
        }
    }

    void onInit() {
        Log.i(TAG, "Inititialized");
        initializing = false;

        if (listener != null)
            listener.onInit();

        for (EventListener listener : internalEventListeners) {
            listener.onInit();
        }

    }

    void onInitError(String msg) {
        Log.d(TAG, "Init Error : " + msg);
        initializing = false;
        if (listener != null)
            listener.onInitError(msg);

        for (EventListener listener : internalEventListeners) {
            listener.onInitError(msg);
        }
    }

    public void refetchPlacement(final String placementId) {
        try {
            serviceClient.getPlacement(appId, placementId, new ServiceClient.ServiceResponseListener() {
                public void onErrorResponse(String msg, JSONObject data) {
                }

                public void onSuccessResponse(JSONObject resp) {
                    try {
                        placements.get(placementId).setup(resp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onError(String error, JSONObject resp) {
                }

            });
        } catch (DioSdkException e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    public Context getActivityContext() {
        return activityContext;
    }

    public String getVer() {
        return BuildConfig.VERSION_NAME;
    }

    public boolean showAd(final Context ctx, final String placementId) {
        Log.i(TAG, "Calling showAd() for placement " + placementId);
        if (this.initialized) {
            if (context == null) {
                context = ctx.getApplicationContext();
            }
            Placement placement = this.placements.get(placementId);
            Boolean viewLoaded = false;
            if (placement != null) {
                if (placement.isOperative()) {
                    Ad ad = placement.getNextAd();
                    if (ad != null) {
                        if (!(ad instanceof InterstitialAdInterface)) {
                            Log.e(Controller.TAG, "trying to call showAd() on a non-interstitial ad placement");
                            return false;
                        }
                        Intent i;
                        switch (ad.getActivityType()) {
                            case Ad.ACTIVITY_TYPE_TRANSLUCENT:
                                i = new Intent(ctx, DioTranslucentActivity.class);
                                break;
                            case Ad.ACTIVITY_TYPE_NORMAL:
                            default:
                                i = new Intent(ctx, DioActivity.class);
                                break;
                        }
                        i.putExtra("placement", placementId);
                        i.putExtra("ad", ad.getId());
                        i.putExtra("cmd", "renderAdComponents");
                        if (!obtainInterstitialLock()) {
                            Log.i(TAG, "Adlock occupied ignoring showAd()");
                            return false;
                        }
                        try {
                            ctx.startActivity(i);
                            viewLoaded = true;
                            Log.i(TAG, "Showing ad for placement " + placementId);
                        } catch (Exception e) {
                            logError("couldnt starts activity: " + e.toString(), Log.getStackTraceString(e));
                        }
                    } else {
                        Log.i(TAG, "Don't have an Ad for placement " + placementId);
                    }
                } else {
                    Log.i(TAG, "Placement " + placementId + " is not operative");

                }
            } else {
                Log.i(TAG, "Don't know placement " + placementId);
            }
            return viewLoaded;
        } else {
            if (!initializing) {
                Log.e(TAG, "calling showAd with before calling init()");
                return false;
            } else {
                setInternalEventListener(new EventListener() {
                    public void onInit() {
                        showAd(ctx, placementId);
                    }
                });
                return false;
            }
        }
    }

    private void setInternalEventListener(EventListener listener) {
        internalEventListeners.add(listener);
    }

    public void logError(String err, String trace) {
        errLogger.log(err);
        serviceClient.reportError(appId, err, trace, null);
    }

    public void logError(String err, String trace, JSONObject data) {
        errLogger.log(err);
        serviceClient.reportError(appId, err, trace, data);
    }

    public void logError(String err) {
        errLogger.log(err);
        serviceClient.reportError(appId, err, null, null);
    }

    public EventListener setEventListener(EventListener listener) {
        Log.d(TAG, "setting event listener");
        this.listener = listener;
        return listener;
    }

    /**
     * public setter to enable usage of geo location
     *
     * @param enable flag denoting whether to use geo or not
     */
    public void setGeoPermRequestEnabled(boolean enable) {
        mUseLocation = enable;
    }

    public boolean getGeoPermRequestEnabled() {
        return mUseLocation;
    }

    public EventListener getEventListener() {
        return listener;
    }

    public void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            Location mLocation = null;
            LocationManager mLocationManager;

            mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            ArrayList<String> locationProviders = (ArrayList<String>) mLocationManager.getAllProviders();

            if (locationProviders != null) {
                Collections.sort(locationProviders, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareToIgnoreCase(o2);
                    }
                });

                for (String provider : locationProviders) {
                    mLocation = mLocationManager.getLastKnownLocation(provider);
                    if (mLocationManager.isProviderEnabled(provider) && mLocation != null) {
                        break;
                    }
                }
            }

            if (mLocation != null) {
                deviceDescriptor.deviceLatitude = String.valueOf(mLocation.getLatitude());
                deviceDescriptor.deviceLongitude = String.valueOf(mLocation.getLongitude());
                deviceDescriptor.deviceLocationAccuracy = String.valueOf(mLocation.getAccuracy());
            }

        } catch (NoClassDefFoundError e) {
            return;
        }
    }

    public void addSearchTerm(String searchContext, String searchTerm, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject data = new JSONObject();
            data.put("searchContext", searchContext);
            data.put("searchTerm", searchTerm);
            feedData("searchTerm", data, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reportYob(int year, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        feedData("yob", Integer.toString(year), listener);
    }

    public void reportGender(String gender, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        feedData("gender", gender, listener);
    }

    public void reportUserName(String username, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        feedData("username", username, listener);
    }

    public void addKeyWords(ArrayList<String> keywords, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        feedData("keywords", keywords.toArray(), listener);
    }

    public void addContentConsumed(String title, ArrayList<String> keywords, String contentId, String contentText, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("keywords", keywords.toArray());
            data.put("contentId", contentId);
            data.put("contentText", contentText);
            feedData("contentConsumed", data, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addProductView(String productCategory, String productName, String productId, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject data = new JSONObject();
            data.put("productCategory", productCategory);
            data.put("productName", productName);
            data.put("productId", productId);
            feedData("productView", data, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        context = null;
    }

    public void addInAppPurchase(String productCategory, String productName, String productId, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject data = new JSONObject();
            data.put("productName", productName);
            data.put("productCategory", productCategory);
            data.put("productId", productId);
            feedData("inAppPurchase", data, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addInAppPurchase(String productCategory, String productName, String productId, double price, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject data = new JSONObject();
            data.put("productName", productName);
            data.put("productCategory", productCategory);
            data.put("productId", productId);
            data.put("price", price);
            feedData("inAppPurchase", data, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void feedData(String dataType, JSONObject data, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        if (appId == null)
            throw new DioSdkException("SDK must be initialized before calling data apis");

        serviceClient.feedData(dataType, appId, data, listener);
    }

    private void feedData(String dataType, String data, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        if (appId == null)
            throw new DioSdkException("SDK must be initialized before calling data apis");

        serviceClient.feedData(dataType, appId, data, listener);
    }

    private void feedData(String dataType, Object[] data, ServiceClient.ServiceResponseListener listener) throws DioSdkException {
        if (appId == null)
            throw new DioSdkException("SDK must be initialized before calling data apis");

        serviceClient.feedData(dataType, appId, data, listener);
    }

    public String getAppId() {
        return appId;
    }

    public InfeedAdContainer getInfeedAdContainer(Context ctx, String placementId) {
        if (!feedContainers.containsKey(placementId)) {
            feedContainers.put(placementId, new InfeedAdContainer(ctx, placementId));
        } else {
            feedContainers.get(placementId).updateContext(ctx);
        }
        return feedContainers.get(placementId);
    }
}

