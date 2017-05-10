package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import io.display.displayioshowcase.BuildConfig;
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
    public void displayAd() {
        ctrl.showAd(getActivity(), BuildConfig.INTERSTITIAL_PLACEMENT);
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
        initController(new JsonStubs().getInterstitialStaticJsonStub());

        setItemsList(view);
    }

    @Override
    public String getPlacementId() {
        return PLACEMENT_ID;
    }

    public interface ItemClickListener{
        void onCLicked();
    }

    public int getRvItemsList(){
        return R.id.list_static_items;
    }
}
