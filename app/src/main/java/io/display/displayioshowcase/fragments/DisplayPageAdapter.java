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
        Drawable myDrawable = ContextCompat.getDrawable(context, R.drawable.ic_action_ic_close);
        String text = getItem(position).getFragmentTitle();
        SpannableStringBuilder sb = new SpannableStringBuilder("   " + text); // space added before text for convenience
        try {
            myDrawable.setBounds(5, 5, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
//            // TODO: handle exception
        }

        return sb;
    }

    public List<PagerProvider> getFragmentProviderList(){
        return fragmentProviderList;
    }
}
