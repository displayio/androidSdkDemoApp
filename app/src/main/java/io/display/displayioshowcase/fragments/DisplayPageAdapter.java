package io.display.displayioshowcase.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import java.util.List;

import io.display.displayioshowcase.R;

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
