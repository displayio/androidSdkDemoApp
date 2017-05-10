package io.display.displayioshowcase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nicolae on 08.05.2017.
 */

public class JsonStubs {
    public JSONObject getInterstitialStaticJsonStub() {
        try {
            return new JSONObject("{\n" +
                    "\t\t\"name\": \"Interstitial\",\n" +
                    "\t\t\"status\": \"enabled\",\n" +
                    "\t\t\"ads\": [{\n" +
                    "\t\t\t\"adId\": \"IB_03672865163d0311f5cf2143c6cde44cbanner320480\",\n" +
                    "\t\t\t\"ad\": {\n" +
                    "\t\t\t\t\"type\": \"interstitial\",\n" +
                    "\t\t\t\t\"subtype\": \"banner\",\n" +
                    "\t\t\t\t\"data\": {\n" +
                    "\t\t\t\t\t\"clk\": \"https:\\/\\/play.google.com/store/apps/details?id=com.buzi.phonecleaner\",\n" +
                    "\t\t\t\t\t\"imp\": \"\",\n" +
                    "\t\t\t\t\t\"ctv\": \"interstitial_static_portrait_1\",\n" +
                    "\t\t\t\t\t\"w\": 320,\n" +
                    "\t\t\t\t\t\"h\": 480\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"offering\": {\n" +
                    "\t\t\t\t\"type\": \"app\",\n" +
                    "\t\t\t\t\"cpn\": 10357267,\n" +
                    "\t\t\t\t\"id\": \"com.bigfishgames.fairwaysolitaireuniversalf2pgoogle\"\n" +
                    "\t\t\t}\n" +
                    "\t\t}]\n" +
                    "\t}");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getInterstitialVideoJsonStub() {
        try {
            return new JSONObject(
                    "{" +
                            "\t\t\t\t\"name\": \"TestingPlacement\",\n" +
                            "\t\t\t\t\"status\": \"enabled\",\n" +
                            "\t\t\t\t\"ads\": [{\n" +
                            "\t\t\t\t\t\"adId\": \"IV_ef51adce01b7685ac3cb38d299e22ee8\",\n" +
                            "\t\t\t\t\t\"ad\": {\n" +
                            "\t\t\t\t\t\t\"type\": \"interstitial\",\n" +
                            "\t\t\t\t\t\t\"subtype\": \"video\",\n" +
                            "\t\t\t\t\t\t\"data\": {\n" +
                            "\t\t\t\t\t\t\t\"clk\": \"https:\\/\\/play.google.com/store/apps/details?id=com.buzi.phonecleaner\",\n" +
                            "\t\t\t\t\t\t\t\"imp\": \"https:\\/\\/appsrv.display.io\\/imp?msessId=5912bc018884c&tls=18916546_67_3\",\n" +
                            "\t\t\t\t\t\t\t\"video\": \"interstitial_video_1_landscape_no_landing_card\",\n" +
                            "\t\t\t\t\t\t\t\"duration\": 15,\n" +
                            "\t\t\t\t\t\t\t\"landingCard\": \"http:\\/\\/cdn.display.io\\/ctv\\/asset\\/interstitial_video_landing.png\",\n" +
                            "\t\t\t\t\t\t\t\"w\": 480,\n" +
                            "\t\t\t\t\t\t\t\"h\": 320\n" +
                            "\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t},\n" +
                            "\t\t\t\t\t\"offering\": {\n" +
                            "\t\t\t\t\t\t\"type\": \"app\",\n" +
                            "\t\t\t\t\t\t\"cpn\": 3402675,\n" +
                            "\t\t\t\t\t\t\"id\": \"\"\n" +
                            "\t\t\t\t\t}\n" +
                            "\t\t\t\t}]\n" +
                            "\t\t\t}"
            );

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getInfeedStaticJsonStub() {
        try {
            return new JSONObject(
                    "{" +
                            "\t\t\"name\": \"Interstitial\",\n" +
                            "\t\t\"status\": \"enabled\",\n" +
                            "\t\t\"ads\": [{\n" +
                            "\t\t\t\"adId\": \"IB_03672865163d0311f5cf2143c6cde44cbanner320480\",\n" +
                            "\t\t\t\"ad\": {\n" +
                            "\t\t\t\t\"type\": \"infeed\",\n" +
                            "\t\t\t\t\"subtype\": \"banner\",\n" +
                            "\t\t\t\t\"data\": {\n" +
                            "\t\t\t\t\t\"clk\": \"https:\\/\\/play.google.com/store/apps/details?id=com.buzi.phonecleaner\",\n" +
                            "\t\t\t\t\t\"imp\": \"\",\n" +
                            "\t\t\t\t\t\"ctv\": \"infeed_static_1\",\n" +
                            "\t\t\t\t\t\"w\": 480,\n" +
                            "\t\t\t\t\t\"h\": 320\n" +
                            "\t\t\t\t}\n" +
                            "\t\t\t},\n" +
                            "\t\t\t\"offering\": {\n" +
                            "\t\t\t\t\"type\": \"app\",\n" +
                            "\t\t\t\t\"cpn\": 10357267,\n" +
                            "\t\t\t\t\"id\": \"com.bigfishgames.fairwaysolitaireuniversalf2pgoogle\"\n" +
                            "\t\t\t}\n" +
                            "\t\t}]\n" +
                            "\t}");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getInfeedVideoJsonStub() {
        try {
            return new JSONObject(
                    "{\n" +
                            "\t\t\t\t\"name\": \"TestingPlacement\",\n" +
                            "\t\t\t\t\"status\": \"enabled\",\n" +
                            "\t\t\t\t\"ads\": [{\n" +
                            "\t\t\t\t\t\"adId\": \"IV_ef51adce01b7685ac3cb38d299e22ee8\",\n" +
                            "\t\t\t\t\t\"ad\": {\n" +
                            "\t\t\t\t\t\t\"type\": \"infeed\",\n" +
                            "\t\t\t\t\t\t\"subtype\": \"video\",\n" +
                            "\t\t\t\t\t\t\"data\": {\n" +
                            "\t\t\t\t\t\t\t\"clk\": \"https:\\/\\/play.google.com/store/apps/details?id=com.buzi.phonecleaner\",\n" +
                            "\t\t\t\t\t\t\t\"imp\": \"https:\\/\\/appsrv.display.io\\/imp?msessId=5912bc018884c&tls=18916546_67_3\",\n" +
                            "\t\t\t\t\t\t\t\"video\": \"infeed_video_1_no_landing_card\",\n" +
                            "\t\t\t\t\t\t\t\"duration\": 15,\n" +
                            "\t\t\t\t\t\t\t\"landingCard\": \"http:\\/\\/cdn.display.io\\/ctv\\/asset\\/interstitial_video_landing.png\",\n" +
                            "\t\t\t\t\t\t\t\"vwidth\": 480,\n" +
                            "\t\t\t\t\t\t\t\"vheight\": 320\n" +
                            "\t\t\t\t\t\t}\n" +
                            "\t\t\t\t\t},\n" +
                            "\t\t\t\t\t\"offering\": {\n" +
                            "\t\t\t\t\t\t\"type\": \"app\",\n" +
                            "\t\t\t\t\t\t\"cpn\": 3402675,\n" +
                            "\t\t\t\t\t\t\"id\": \"\"\n" +
                            "\t\t\t\t\t}\n" +
                            "\t\t\t\t}]\n" +
                            "\t\t\t}}");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
