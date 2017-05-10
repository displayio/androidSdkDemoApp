package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import io.display.displayioshowcase.BuildConfig;
import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

/**
 * Created by Nicolae on 05.05.2017.
 */

public class InfeedVideoFragment extends BaseFragment implements PagerProvider {

    private static int FIRST_INFEED_POSITION = 3;
    private static String PLACEMENT_ID = "114";

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    public void displayAd() {
        ctrl.showAd(getActivity(), BuildConfig.INTERSTITIAL_PLACEMENT);
    }

    protected int getRvList() {
        return R.id.recycler_view;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_video_infeed;
    }

    @Override
    public String getFragmentTitle() {
        return "InFeed Video";
    }

    @Override
    public void onLoad(View view) {
        initController(new JsonStubs().getInfeedVideoJsonStub());
        setList(view);
    }

    @Override
    public int getAdPosition() {
        return FIRST_INFEED_POSITION;
    }

    @Override
    public String getPlacementId() {
        return PLACEMENT_ID;
    }
}
