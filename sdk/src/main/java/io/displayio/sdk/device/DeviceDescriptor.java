package io.displayio.sdk.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;

public class DeviceDescriptor {
    public String googleAid;
    public Boolean dnt = true;
    public String deviceLatitude = "";
    public String deviceLongitude = "";
    public String deviceLocationAccuracy = "";
    private HashMap<String, String> props;
    DeviceEventsListener mDeviceEventsListener;
    private String cpuData = "", cpuVendor = "", cpuModel = "";
    private int cpuCores = 0;
    public DeviceDescriptor(final Context context, final DeviceEventsListener listener) {
        mDeviceEventsListener = listener;
        getCpuData();
        props = new HashMap<>();
        props.put("model", Build.MODEL);
        props.put("make", Build.MANUFACTURER);
        props.put("os", "android");
        props.put("osVer", Build.VERSION.RELEASE);
        props.put("hardware", Build.HARDWARE);
        props.put("fingerprint", Build.FINGERPRINT);
        props.put("brand", Build.BRAND);
        props.put("product", Build.PRODUCT);
        props.put("cpuCores", Integer.toString(cpuCores));
        props.put("cpuModel", cpuModel);
        props.put("cpuVendor", cpuVendor);
        props.put("cpuArch", System.getProperty("os.arch"));

        updateDeviceResolution(context);
        props.put("inch", getScreenInInches(context));
        props.put("carrier", getCarrierName(context));
        props.put("net", getNetworkInfo(context));
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    WebView view = new WebView(context);
                    props.put("ua", view.getSettings().getUserAgentString());
                    view.destroy();
                }
            });


        } catch (Exception e) {
            props.put("ua", "");
        }
//        props.put("apps", getInstalledAppList(context).toString());

        AdvertisingIdFetcher fetcher = new AdvertisingIdFetcher() {
            @Override
            public void onPostExecute(String respText) {
                String id;
                Boolean dnt;
                try {
                    JSONObject resp = new JSONObject(respText);
                    try {
                        googleAid = resp.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        dnt = resp.getBoolean("dnt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onDeviceIdRetrieved();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            fetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        } else {
            fetcher.execute(context);
        }
    }

    public void updateDeviceResolution(Context context) {
        int apiLevel = Build.VERSION.SDK_INT;
        if(apiLevel >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getDeviceResolution1(context);
        } else {
            getDeviceResolution2(context);
        }
    }
    private void getCpuData() {
        StringBuffer sb = new StringBuffer();
        String cpufile = "/pr"  + "oc/cp" + "uinfo";
        if (new File(cpufile).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(cpufile)));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "\n");
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }
        cpuData = sb.toString();
        Pattern vpattern = Pattern.compile("vendor_id\\s*: (.*)");
        Matcher vmatcher = vpattern.matcher(cpuData);

        if(vmatcher.find()) {
            cpuVendor = vmatcher.group(1);
        }

        Pattern mpattern = Pattern.compile("model name\\s*: (.*)");
        Matcher mmatcher = mpattern.matcher(cpuData);

        if(mmatcher.find()) {
            cpuModel = mmatcher.group(1);
        }

        Pattern cpattern = Pattern.compile("^processor");
        Matcher cmatcher = cpattern.matcher(cpuData);
        while(cmatcher.find()) {
            cpuCores++;
        }
    }
    public int getPxWidth() {
        return Integer.valueOf(getProps().get("w").toString());
    }
    public int getPxHeight() {
        return Integer.valueOf(getProps().get("h").toString());
    }
    public double getInchScreenSize() {
        return Double.valueOf((String)getProps().get("inch"));
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getDeviceResolution1(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        props.put("w", String.valueOf(size.x));
        props.put("h", String.valueOf(size.y));
    }
    private void getDeviceResolution2(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        props.put("w", String.valueOf(display.getWidth()));
        props.put("h", String.valueOf(display.getHeight()));
    }

    public HashMap getProps() {
        return props;
    }

    class AdvertisingIdFetcher extends AsyncTask<Context, String, String> {
        @Override
        protected String doInBackground(Context[] context) {
            JSONObject resp = new JSONObject();
            try {
                AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context[0]);
                resp.put("id", adInfo.getId());
                resp.put("dnt", adInfo.isLimitAdTrackingEnabled());
            } catch (Exception e) {
                Log.i("io.display.sdk", "couldn't get advertising ID");

            }
            return resp.toString();
        }
    }

    private String getScreenInInches(Context mContext) {
        if(mContext instanceof Activity) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int dens = dm.densityDpi;
            double wi = (double) width / dens;
            double hi = (double) height / dens;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            return String.valueOf(Math.sqrt(x + y));
        }
        return "";
    }

    private String getNetworkInfo(Context mContext){
        String mConnectionType = "";
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected())
            return "not_connected";
        switch (cm.getActiveNetworkInfo().getType()) {
            case ConnectivityManager.TYPE_MOBILE_DUN :
            case ConnectivityManager.TYPE_MOBILE :
                TelephonyManager mTelephonyManager = (TelephonyManager)
                        mContext.getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = mTelephonyManager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        mConnectionType =  "2G";
                        break;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        mConnectionType =  "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        mConnectionType =  "4G";
                    default:
                        mConnectionType =  "mobile";
                }
                break;
            case ConnectivityManager.TYPE_WIFI :
                mConnectionType = "wifi";
                break;
        }
        return mConnectionType;
    }


    private String getCarrierName(Context mContext){
        return ((TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE)).getNetworkOperatorName();
    }

    private List<ApplicationInfo> getInstalledAppInfoList(Context context){
        return context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
    }

    public JSONArray getInstalledAppList(Context context){
        JSONArray mAppList = new JSONArray();
        List<ApplicationInfo> mAppInfoList = getInstalledAppInfoList(context);
        for(int i = 0; i < mAppInfoList.size(); i ++) {
            JSONObject mApp = new JSONObject();
            if((mAppInfoList.get(i).flags & ApplicationInfo.FLAG_SYSTEM) != 1)
                try {
                    mApp.put("package", mAppInfoList.get(i).packageName);
                    mApp.put("installedAt", String.valueOf(context.getPackageManager().getPackageInfo(mAppInfoList.get(i).packageName, 0).firstInstallTime));

                    mAppList.put(mApp);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
        return mAppList;
    }

}