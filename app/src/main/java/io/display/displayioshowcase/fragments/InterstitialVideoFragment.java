package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.display.displayioshowcase.InterstitialAd;
import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

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
        return "Interstitial video";
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
        return R.drawable.ic_tab_interstitial_gray;
    }

    @Override
    protected JSONObject getAdJson(InterstitialAd item) {
        JSONObject json = null;
        try {
            json = new JsonStubs().getInterstitialVideoJsonStub();
            ((JSONObject)json.getJSONArray("ads").get(0)).getJSONObject("ad").getJSONObject("data")
                    .put("video", item.getVideoResName())
                    .put("landingCard", item.getLandingResName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    protected ArrayList<InterstitialAd> createItemsList() {
        rvListItems = new ArrayList<>();
        rvListItems.add(InterstitialAd.from("ic_landing_1", "1121", R.drawable.tile_1, "video_1", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_2", "1122", R.drawable.tile_2, "video_2", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_3", "1123", R.drawable.tile_3, "video_3", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_4", "1124", R.drawable.tile_4, "video_4", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_5", "1125", R.drawable.tile_5, "video_5", "480", "320"));
        rvListItems.add(InterstitialAd.from("ic_landing_6", "1126", R.drawable.tile_6, "video_6", "480", "320"));
        return rvListItems;
    }
}
