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
 * Created by Nicolae on 04.05.2017.
 */

public class InterstitialStaticFragment extends BaseFragment implements PagerProvider {
    private static final String PLACEMENT_ID = "111";

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    protected JSONObject getAdJson(InterstitialAd item) {
        JSONObject json = null;
        try {
            json = new JsonStubs().getInterstitialStaticJsonStub();
            ((JSONObject)json.getJSONArray("ads").get(0)).getJSONObject("ad").getJSONObject("data")
                    .put("ctv", item.getVideoResName())
                    .put("w", item.getWidth())
                    .put("h", item.getHeight());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    protected ArrayList<InterstitialAd> createItemsList() {
        rvListItems = new ArrayList<>();
        rvListItems.add(InterstitialAd.from("ic_inter_1", "1111", R.drawable.ic_inter_1, "ic_inter_1", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_2", "1112", R.drawable.ic_inter_2, "ic_inter_2", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_3", "1113", R.drawable.ic_inter_3, "ic_inter_3", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_4", "1114", R.drawable.ic_inter_4, "ic_inter_4", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_5", "1115", R.drawable.ic_inter_5, "ic_inter_5", "320", "480"));
        rvListItems.add(InterstitialAd.from("ic_inter_6", "1116", R.drawable.ic_inter_6, "ic_inter_6", "320", "480"));
        return rvListItems;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_static_interstital;
    }

    @Override
    public String getFragmentTitle() {
        return "Interstitial static";
    }

    @Override
    public void onLoad(View view) {
        setItemsList(view);
    }

    @Override
    public String getPlacementId() {
        return PLACEMENT_ID;
    }

    @Override
    public int getTabIcon() {
        return R.drawable.ic_tab_interstitial_gray;
    }

    public interface ItemClickListener{
        void onClicked(InterstitialAd interstitialAd);
    }

    public int getRvItemsList(){
        return R.id.list_static_items;
    }

}
