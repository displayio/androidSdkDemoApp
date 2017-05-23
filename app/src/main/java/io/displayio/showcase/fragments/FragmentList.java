package io.displayio.showcase.fragments;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolae on 04.05.2017.
 */

public class FragmentList {
    public List<PagerProvider> getFragments() {
        List<PagerProvider> mProviderList = new ArrayList<>();
        mProviderList.add(new InterstitialStaticFragment());
        mProviderList.add(new InterstitialVideoFragment());
        mProviderList.add(new InfeedVideoFragment());
        mProviderList.add(new InfeedStaticFragment());
        return mProviderList;
    }
}
