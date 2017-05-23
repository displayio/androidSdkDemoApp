package io.displayio.sdk;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by gidi on 07/03/16.
 */
public class DioSdkException extends Exception {
    public DioSdkException(String s) {
        super(s);
        Controller.getInstance().logError(s, Log.getStackTraceString(this));
    }
    public DioSdkException(String s, JSONObject additionalData) {
        super(s);
        Controller.getInstance().logError(s, Log.getStackTraceString(this), additionalData);
    }
    public DioSdkException(Throwable e) {
        super(e);
        Controller.getInstance().logError(e.getMessage(), Log.getStackTraceString(e));
    }

    public DioSdkException(String s, Throwable e) {
        super(s, e);
    }
}
