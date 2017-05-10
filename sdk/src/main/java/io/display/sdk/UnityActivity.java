package io.display.sdk;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnityActivity extends Activity {
    Controller ctrl;
    static String unityCatchMethod = null;
    static String unityCatchObject = null;
    String appId,placementId;
    ArrayList<JSONObject> msgQueue = new ArrayList<JSONObject>();

    UnityPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout);
        //load units for All Units placement from values
        Bundle extras = getIntent().getExtras();

        player = new UnityPlayer((ContextWrapper) getApplicationContext());

        appId = extras.getString("appId");
        placementId = extras.getString("placementId");
        //display ads with EventListener
        ctrl = Controller.getInstance();

        if(!ctrl.isInitialized() ) {
            ctrl.init(this, appId);
            ctrl.setEventListener(new EventListener() {
                @Override
                public void onInit() {
                    showAd();
                }
                public void onAdCompleted(String placementId) {addEventQueue(placementId, "adCompleted");}
                public void onAdShown(String placementId) {addEventQueue(placementId, "adShown");}
                public void onAdClick(String placementId) {addEventQueue(placementId, "adClick");}
                public void onAdClose(String placementId) {
                    addEventQueue(placementId, "adClose");
                    sendEventQueue();
                }

                @Override
                public void onInitError(String error) {
                    finish();
                }
            });
        } else {
            ctrl.setEventListener(new EventListener() {
                public void onAdCompleted(String placementId) {addEventQueue(placementId, "adCompleted");}
                public void onAdShown(String placementId) {addEventQueue(placementId, "adShown");}
                public void onAdClick(String placementId) {addEventQueue(placementId, "adClick");}
                public void onAdClose(String placementId) {
                    addEventQueue(placementId, "adClose");
                    sendEventQueue();
                }
            });
            showAd();
        }
    }



    private void sendUnityMessage(String placementId, String event) {
        if(unityCatchMethod != null) {
            Log.i("io.display.io", "triggering unity event " + event);
            JSONObject msgObj = new JSONObject();
            try {
                msgObj.put("evName", event);
                msgObj.put("placementId", placementId);
                player.UnitySendMessage(unityCatchObject, unityCatchMethod, msgObj.toString());
            } catch (JSONException e) {

            }
        }
    }



    private void showAd() {
        if (!ctrl.showAd(this, placementId)) {
            finish();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    public static void showAd(Activity activity, String appId, String placementId) {
        Intent myIntent = new Intent(activity, UnityActivity.class);
        myIntent.putExtra("appId",appId);
        myIntent.putExtra("placementId", placementId);
        activity.startActivity(myIntent);
    }

    public static void showAd(Activity activity, String appId) {
        Intent myIntent = new Intent(activity, UnityActivity.class);
        myIntent.putExtra("appId",appId);
        activity.startActivity(myIntent);
    }
    public static void init(Activity activity, String appId) {
        Controller ctrl = Controller.getInstance();
        ctrl.init(activity, appId);
    }
    public static void setUnityCatchMethod(String object, String method) {
        unityCatchMethod = method;
        unityCatchObject = object;
    }
    public void addEventQueue(String placementId, String evName){
        JSONObject msgObj = new JSONObject();

        if(unityCatchMethod != null) {
            Log.i("io.display.io", " Queueing event " + evName);
            try {
                msgObj.put("evName", evName);
                msgObj.put("placementId", placementId);
                msgQueue.add(msgObj);
                //  player.UnitySendMessage(unityCatchObject, unityCatchMethod, msgObj.toString());
            } catch (JSONException e) {

            }
        }


    }
    public void sendEventQueue(){
        Log.i("io.display.io", "Sending all Events");
        for (int  i = 0; i < msgQueue.size(); i++) {
            player.UnitySendMessage(unityCatchObject, unityCatchMethod, msgQueue.get(i).toString());
        }
        msgQueue = new ArrayList<>();

    }

    public void onDestroy() {

        super.onDestroy();
//        unregisterReceiver();
    }
}
