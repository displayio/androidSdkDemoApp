package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

/**
 * Created by Nicolae on 10.05.2017.
 */

public class InfeedStaticFragment extends BaseFragment implements PagerProvider{
    private static int FIRST_INFEED_POSITION = 3;
    private static String PLACEMENT_ID = "113";

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    public void displayAd() {
        ctrl.showAd(getActivity(), PLACEMENT_ID);
    }

    protected int getRvList() {
        return R.id.recycler_static_view;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_static_infeed;
    }

    @Override
    public String getFragmentTitle() {
        return "InFeed Static";
    }

    @Override
    public void onLoad(View view) {
        initController(new JsonStubs().getInfeedStaticJsonStub());
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
