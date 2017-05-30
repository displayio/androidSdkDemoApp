package io.displayio.showcase;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import io.displayio.showcase.fragments.BaseFragment;
import io.displayio.showcase.fragments.DisplayPageAdapter;
import io.displayio.showcase.fragments.FragmentList;
import io.displayio.showcase.fragments.InfoDialog;
import io.displayio.showcase.fragments.PagerProvider;

public class MainActivity extends AppCompatActivity{
    private DisplayPageAdapter homePageAdapter;
    private ViewPager mPager;
    private List<PagerProvider> mProviderList;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.entries_pager);

        configureToolbar((Toolbar)findViewById(R.id.display_toolbar));

        configurePager();
    }

    protected void configureToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_display_toolbar_logo);
            supportActionBar.setTitle("");
        }
    }

    private void configurePager() {
        mProviderList = new FragmentList().getFragments();

        homePageAdapter = new DisplayPageAdapter(getSupportFragmentManager(), mProviderList, this);
        mPager.setOffscreenPageLimit(mProviderList.size());
        mPager.setAdapter(homePageAdapter);
    }

    private void setUpTabIcons() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.pager_tab);
        if(tabLayout.getTabCount() > 0) {
            for (int i = 0; i < mProviderList.size(); i++) {
                BaseFragment frag = homePageAdapter.getItem(i);
                tabLayout.getTabAt(i).setIcon(frag.getTabIcon());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        setUpTabIcons();
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_info :
                displayInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayInfoDialog() {
        InfoDialog info = new InfoDialog();
        info.setOnClickListener(new InfoDialog.OnCloseListener() {
            @Override
            public void onClosed() {
                showArrowDown();
            }
        });
        info.show(getFragmentManager(), "dlg1");

        showArrowUp();
    }

    public void showArrowDown() {
        if(menu != null)
            menu.findItem(R.id.menu_info).setIcon(R.drawable.ic_action_arrow_down);
    }

    public void showArrowUp() {
        if(menu != null)
            menu.findItem(R.id.menu_info).setIcon(R.drawable.ic_action_arrow_up);
    }
}
