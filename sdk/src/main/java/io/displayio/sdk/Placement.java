package io.displayio.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.displayio.sdk.ads.Ad;

/**
 * Created by gidi on 07/03/16.
 */
public class Placement {
    public String id;
    private String status;
    public JSONObject data;
    private int viewsLeft;
    private boolean capViews = false;

    private LinkedHashMap<String, Ad> ads = new LinkedHashMap<>();
    private LinkedHashMap<String, Ad> lastAdStack = new LinkedHashMap<>();
    private ArrayList<String> queue = new ArrayList<>();

    public Placement(String id) throws DioSdkException{
        this.id = id;
    }

    public void setup(JSONObject d) throws DioSdkException {
        data = d;
        try {
            status = data.getString("status");
            if(data.has("viewsLeft")) {
                capViews = true;
                viewsLeft = data.getInt("viewsLeft");
            }
            if(status.equals( "enabled" )){
                processAds(data.getJSONArray("ads"));
                rebuildQueue();
                preloadNextAd();
            }
        } catch (JSONException e) {
            throw new DioSdkException("bad placement data");
        }
    }

    private void processAds(JSONArray adsData) {
        lastAdStack = ads;
        ads = new LinkedHashMap<>();
        for(int i = 0; i < adsData.length(); i++ ) {
            try {
                JSONObject entry = (JSONObject)adsData.get(i);
                String adId = entry.getString("adId");
                Ad ad = Ad.factory(adId, entry.getJSONObject("ad"), entry.optJSONObject("offering"));
                if (ad != null)
                    ad.setPlacementId(id);

                ads.put(adId, ad);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(adsData.length() == 0) {
            Controller.getInstance().triggerPlacementAction("onNoAds", this.id);
        }
    }

    private void rebuildQueue() {
        Iterator it = ads.entrySet().iterator();
        queue.clear();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            queue.add(entry.getKey().toString());
        }
    }

    Ad getAd(String id) {
        Ad ad = null;
        if(ads.containsKey(id)) {
            ad = ads.get(id);
        } else {
            if(lastAdStack.containsKey(id)) {
                ad = lastAdStack.get(id);
            }
         }
        return ad;
    }
    public void refetch() {
        Controller.getInstance().refetchPlacement(this.id);
    }
    public Ad getNextAd() {
        String adid = null;
        Ad ad = null;
        if (ads.size() > 0) {
            if (queue.size() == 0) {
                this.refetch();
                // we rebuild the queue so we have what to serve till we get a new set
                rebuildQueue();
            }
            adid = queue.get(0);
            queue.remove(0);
            ad = ads.get(adid);
            preloadNextAd();
        } else {
            //try to fill the ads
            try {
                setup(this.data);
                if (queue.size() > 0) {
                    adid = queue.get(0);
                    queue.remove(0);
                    ad = ads.get(adid);
                    preloadNextAd();
                }
            } catch (DioSdkException e) {
                e.printStackTrace();
            }
        }

        return ad;
    }

    private void preloadNextAd() {
        if (queue.size() > 0) {
            final String adid = queue.get(0);
            try {
                Ad ad = ads.get(adid);
                if(ad != null) {
                    ad.addPreloadListener(new Ad.OnPreloadListener() {
                        public void onError() {
                            if(ads.size() > 0)
                                ads.remove(adid);
                            if (queue.size() > 0)
                                queue.remove(0);
                            preloadNextAd();
                        }
                        public void onLoaded() {
                            Controller.getInstance().triggerPlacementAction("onAdReady", id);
                        }
                    });
                    ad.preload();
                }
            } catch(DioSdkException E) {
                if(ads.size() > 0)
                    ads.remove(adid);
                if (queue.size() > 0)
                    queue.remove(0);
                preloadNextAd();
            }
        }
    }

    public JSONObject getDebugData() {
       return data;
    }
    public int getQueueSize() {
        return queue.size();
    }
    public Boolean hasAd() {
        return ads.size() > 0;
    }

    public boolean isOperative()  {
        Boolean retval = false;
        retval = (this.status.equals("enabled")) && (!capViews || viewsLeft > 0);
        return retval;
    }

    public String getName() {
        try {
            return this.data.getString("name");
        } catch (JSONException e) {
            return "UNKNOWN";
        }
    }


}
