package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import io.display.displayioshowcase.BuildConfig;
import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

/**
 * Created by Nicolae on 08.05.2017.
 */

public class InterstitialVideoFragment extends BaseFragment implements PagerProvider  {
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
        return R.layout.fragment_video_interstitial;
    }

    @Override
    public String getFragmentTitle() {
        return "Interstitial video";
    }

    @Override
    public void onLoad(View view) {
        initController("112", new JsonStubs().getInterstitialVideoJsonStub());
        Button button = (Button)getActivity().findViewById(R.id.interstitialVideoBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAd("112");
            }
        });
    }
}
