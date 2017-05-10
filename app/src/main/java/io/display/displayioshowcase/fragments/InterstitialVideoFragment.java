package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

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
    public void displayAd() {
        ctrl.showAd(getActivity(), PLACEMENT_ID);
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
        initController(new JsonStubs().getInterstitialVideoJsonStub());
        Button button = (Button)getActivity().findViewById(R.id.interstitialVideoBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAd(PLACEMENT_ID);
            }
        });
    }

    @Override
    public String getPlacementId() {
        return PLACEMENT_ID;
    }
}
