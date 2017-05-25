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
                    .put("clk", item.getRedirectLink())
                    .put("w", item.getWidth())
                    .put("h", item.getHeight());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    protected ArrayList<InterstitialAd> createItemsList() {
        rvListItems = new ArrayList<>();
        return rvListItems = Ads.getInterstitialStaticItems(rvListItems);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_static_interstital;
    }

    @Override
    public String getFragmentTitle() {
        return "IS static";
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
        return R.drawable.tab_interstitial_static_selector;
    }

    public interface ItemClickListener{
        void onClicked(InterstitialAd interstitialAd);
    }

    public int getRvItemsList(){
        return R.id.list_static_items;
    }

}
