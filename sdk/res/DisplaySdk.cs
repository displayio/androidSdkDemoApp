using UnityEngine;
using System.Collections;


public class DisplaySdk : MonoBehaviour
{
    private static string appId = "YOUR_APPID#";
    [System.Serializable]
    public class AdEvent
    {
        public string placementId;
        public string evName;
    }

    // Use this for initialization
    void Start()
    {
    #if UNITY_EDITOR
        //Showing ads is only available on an Android.
        if (Application.isEditor) return;
    #endif

    #if UNITY_ANDROID
        string obj;
        obj = this.gameObject.name.ToString();
        var dioSdk = InitDioSdk();
        dioSdk.CallStatic("setUnityCatchMethod", obj, "HandleEvent");
        Debug.Log("Initializing Display.io SDK ");
    #endif

    }

    public void ShowAd(string placementId)
    {
        #if UNITY_EDITOR
            //Showing ads is only available on an Android.
            if (Application.isEditor)
            {
                UnityEditor.EditorUtility.DisplayDialog("Not Android", "You can only use ShowAd() on an Android.", "Ok, I'll try on an Android.");
                return;
            }
        #endif

        #if UNITY_ANDROID
            Debug.Log("Showing Ad");
            var androidUnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            var activity = androidUnityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            var dioSdk = new AndroidJavaClass("io.display.sdk.UnityActivity");
            dioSdk.CallStatic("showAd", activity, appId, placementId);
        #endif
    }

    /*
	* msg, serialized json, e.g  { "event" : "adClick", placementId: "xyz" }
	* supported events :
	* adShown
	* adClose
	* adCompleted (interstitial video only)
	* adClick
	*/

    public void HandleEvent(string json)
    {
        AdEvent ev = JsonUtility.FromJson<AdEvent>(json);
        Debug.Log("Display.io SDK - got event: " + ev.evName + " on placement Id: " + ev.placementId);

        //handle in your own words/code
    }
    public static AndroidJavaClass InitDioSdk() {
        var androidUnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        var activity = androidUnityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        var dioSdk = new AndroidJavaClass("io.display.sdk.UnityActivity");

        dioSdk.CallStatic("init", activity, appId);

        return dioSdk;
    }
}
