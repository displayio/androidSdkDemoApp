package io.display.sdk;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.display.sdk.ads.components.Container;

public class ServiceClient {
    Controller controller;
    private JSONObject additions;
    private int sigChar1 = 402;
    private int sigChar2 = 178;
    private int remainder;
    private int remainderFactor = 12;

    String uri = BuildConfig.BASE_URL;
    public ServiceClient(Controller ctrl) {

        additions = new JSONObject();
        controller = ctrl;
        if(BuildConfig.BUILD_TYPE.equals("debug")) {

            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLSocketFactory sfact = sc.getSocketFactory();
                HttpsURLConnection.setDefaultSSLSocketFactory(sfact);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        remainder = 1536 / remainderFactor;
    }

    private String getSprefix() {
        return  String.valueOf(Character.toChars((int)Math.ceil(sigChar1 / 3.5))) + String.valueOf(Character.toChars((int)Math.ceil(sigChar2 / 1.7)));
    }
    public void add(String k, String e) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            try {
                additions.put(k, e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private JSONObject getSignedRequest(JSONObject payload) throws DioSdkException, JSONException {
        JSONObject req = new JSONObject();
        try {
            JSONObject deviceData;
            JSONObject deviceIds  = new JSONObject();
            JSONObject deviceGeoLocation = new JSONObject();
            // checking  if deviceDescriptor exists - due to unexplainable issue with b4a
            if(controller.deviceDescriptor != null) {
                deviceData = new JSONObject( controller.deviceDescriptor.getProps() );
                deviceIds.put("google_aid", controller.deviceDescriptor.googleAid);
                deviceData.put("dnt", controller.deviceDescriptor.dnt);
                deviceGeoLocation.put("lat", controller.deviceDescriptor.deviceLatitude);
                deviceGeoLocation.put("lng", controller.deviceDescriptor.deviceLongitude);
                deviceGeoLocation.put("precision", controller.deviceDescriptor.deviceLocationAccuracy);
            } else {
                deviceData = new JSONObject();
            }

            deviceData.put("ids", deviceIds);
            payload.put("device", deviceData);
            payload.put("geo", deviceGeoLocation);
            String SigKey = getSprefix() + "g";



            payload.put("sdkVer", controller.getVer());
            payload.put("pkgName", controller.getContext().getPackageName());

            if(BuildConfig.BUILD_TYPE.equals("debug")) {
                if (additions instanceof JSONObject) {
                    Iterator<?> keys = additions.keys();
                    while(keys.hasNext()) {
                        String key = (String)keys.next();
                        payload.put(key, additions.get(key));
                    }
                }
            }
            String json = payload.toString() + "ss";
            byte[] bytesOfMessage;
            json =  json + "d";
            String ObsfucationJson =  json + remainder;
            bytesOfMessage = ObsfucationJson.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1,thedigest);

            req.put(SigKey, bigInt.toString(16));
            req.put("data", payload);
            return req;
        } catch (NoSuchAlgorithmException e) {
            Log.e("io.display.sdk" , "Cannot sign request - no support for hashing algo");
            return req;
        } catch (Exception e) {
            Log.e("io.display.sdk" , "Uncaught Exception when signing request");
            e.printStackTrace();
        }
        return req;
    }
    void reportError(String app, String err, String trace, JSONObject data) {
        try {
            JSONObject req = new JSONObject();

            req.put("action", "reportError");
            req.put("error", err);
            req.put("trace", trace);
            req.put("app", app);
            req.put("additionalData", data);
            this.makeCall(getSignedRequest(req), new ServiceResponseListener() {
                @Override
                public void onSuccessResponse(JSONObject response) {

                }

                @Override
                public void onErrorResponse(String msg, JSONObject response) {

                }

                @Override
                public void onError(String err, JSONObject data) {

                }
            });
        } catch (DioSdkException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void sendRedirectReporting(String app, String bundleId, String cpnId, boolean redirected, String endUrl, JSONArray stack,int numredirect) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "redirectReport");
            req.put("cpnId", cpnId);
            req.put("app", app);
            req.put("bundleId", bundleId);
            req.put("redirected", redirected);
            req.put("endUrl", endUrl);
            req.put("numRedirects", numredirect);
            req.put("stack", stack);
            makeCall(getSignedRequest(req), null);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }
    void getPlacements(String app, final ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "getPlacements");
            req.put("app", app);
            this.makeCall(this.getSignedRequest(req), listener);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }

    void getPlacement(String app, String placementId, final ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "getPlacement");
            req.put("app", app);
            req.put("placement", placementId);
            this.makeCall(this.getSignedRequest(req), listener);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }

    public void feedData(String dataType, String appId, JSONObject data, final ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "feedData");
            req.put("app", appId);
            req.put("dataType", dataType);
            req.put("data", data);
            this.makeCall(this.getSignedRequest(req), listener);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }

    public void feedData(String dataType, String appId, Object[] data, final ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "feedData");
            req.put("app", appId);
            req.put("dataType", dataType);
            req.put("data", data);
            this.makeCall(this.getSignedRequest(req), listener);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }

    public void feedData(String dataType, String appId, String data, final ServiceResponseListener listener) throws DioSdkException {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "feedData");
            req.put("app", appId);
            req.put("dataType", dataType);
            req.put("data", data);
            this.makeCall(this.getSignedRequest(req), listener);
        } catch (JSONException e) {
            throw new DioSdkException("JSON exception ", e);
        }
    }
    void reportAppList( String appId, JSONArray applist) {
        try {
            JSONObject req = new JSONObject();
            req.put("action", "reportAppList");
            req.put("app", appId);
            req.put("list", applist);
            makeCall(this.getSignedRequest(req), null);
        } catch (JSONException e) {

        } catch (DioSdkException e) {
            e.printStackTrace();
        }
    }
    private void makeCall(final JSONObject params, final ServiceResponseListener listener) {
        ServiceRequest asyncCall = new ServiceRequest() {
            @Override
            protected void onPostExecute(JSONObject response)  {
                if(listener != null) {
                    if (exception != null) {
                        listener.onErrorResponse(this.exception.getClass() + " Exception: " + exception.getMessage(), response);
                    }
                    try {
                        if (response == null) {
                            listener.onErrorResponse("null response on " + params.getString("action"), response);
                        } else {
                            if (BuildConfig.IS_DEBUG) Log.d("displayio", response.toString(4));
                            if (!response.has("data")) {
                                listener.onErrorResponse("no data section in response", response);
                            }
                            if (response.has("commands")) {
                                controller.processCommands(response.optJSONArray("commands"));
                            }
                            listener.onSuccessResponse(response.getJSONObject("data"));
                        }
                    } catch (JSONException e) {
                        listener.onErrorResponse("no data section in response", response);
                    }
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncCall.execute(params);
        }
    }

    private class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }
    }

    abstract private class ServiceRequest extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        protected Throwable exception;
        public String rawResponse = "";
        @Override
        protected JSONObject doInBackground(JSONObject[] requests)  {
            JSONObject request = requests[0];
            StringBuilder str = new StringBuilder();
            JSONObject resp = null;

            try {
                JSONObject data = request.optJSONObject("data");
                String action = "";
                if(data != null) {
                    action = data.optString("action");
                }
                Log.i("io.display.sdk", "calling (" + action + ") on: " + uri.toString());
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(request.toString());
                writer.flush();
                writer.close();
                os.close();
                //conn.setUseCaches(false);
                conn.connect();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str.append(line + "\n");
                }
                in.close();
                rawResponse = str.toString();
                resp = new JSONObject( rawResponse );
            } catch (IOException | JSONException e) {
                this.exception = e;
            }

            return resp;
        }
    }

    public interface ServiceResponseListener {
        void onSuccessResponse(JSONObject response);
        void onErrorResponse(String msg, JSONObject response);
        void onError(String err, JSONObject data);
    }
}
