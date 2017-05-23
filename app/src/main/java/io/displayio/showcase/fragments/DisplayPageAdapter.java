package io.displayio.showcase.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Nicolae on 04.05.2017.
 */

public class DisplayPageAdapter extends FragmentPagerAdapter{
    private final List<PagerProvider> fragmentProviderList;
    private Context context;

    public DisplayPageAdapter(FragmentManager fm, List<PagerProvider> fragmentProviderList, Context context) {
        super(fm);
        this.fragmentProviderList = fragmentProviderList;
        this.context = context;
    }

    @Override
    public BaseFragment getItem(int position) {
        return (BaseFragment)fragmentProviderList.get(position).getInstance();
    }

    @Override
    public int getCount() {
        return fragmentProviderList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).getFragmentTitle();
    }

    public List<PagerProvider> getFragmentProviderList(){
        return fragmentProviderList;
    }
}
