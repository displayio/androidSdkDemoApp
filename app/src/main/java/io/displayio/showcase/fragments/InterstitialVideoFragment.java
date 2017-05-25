package io.displayio.showcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.displayio.showcase.InterstitialAd;
import io.displayio.showcase.Ads;
import io.displayio.showcase.JsonStubs;
import io.displayio.showcase.R;

/**
 * Created by Nicolae on 08.05.2017.
 */

public class InterstitialVideoFragment extends BaseFragment implements PagerProvider  {
    private static final String PLACEMENT_ID = "112";
    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_video_interstitial;
    }

    @Override
    public String getFragmentTitle() {
        return "IS video";
    }

    @Override
    public void onLoad(View view) {
        setItemsList(view);
    }

    @Override
    public String getPlacementId() {
        return PLACEMENT_ID;
    }

    public int getRvItemsList(){
        return R.id.list_video_items;
    }

    @Override
    public int getTabIcon() {
        return R.drawable.tab_interstitial_video_selector;
    }

    @Override
    protected JSONObject getAdJson(InterstitialAd item) {
        JSONObject json = null;
        try {
            json = new JsonStubs().getInterstitialVideoJsonStub();
            ((JSONObject)json.getJSONArray("ads").get(0)).getJSONObject("ad").getJSONObject("data")
                    .put("video", item.getVideoResName())
                    .put("clk", item.getRedirectLink())
                    .put("landingCard", item.getLandingResName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    protected ArrayList<InterstitialAd> createItemsList() {
        rvListItems = new ArrayList<>();
        return rvListItems = Ads.getInterstitialVideoItems(rvListItems);
    }
}
