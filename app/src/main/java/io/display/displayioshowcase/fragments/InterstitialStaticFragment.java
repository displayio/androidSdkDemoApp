package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import io.display.displayioshowcase.BuildConfig;
import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

/**
 * Created by Nicolae on 04.05.2017.
 */

public class InterstitialStaticFragment extends BaseFragment implements PagerProvider {

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
        initController("111", new JsonStubs().getInterstitialStaticJsonStub());
        Button button = (Button)getActivity().findViewById(R.id.interstitialStaticBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAd("111");
            }
        });
    }
}
