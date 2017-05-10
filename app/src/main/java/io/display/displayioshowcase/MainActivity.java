package io.display.displayioshowcase;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import io.display.displayioshowcase.fragments.DisplayPageAdapter;
import io.display.displayioshowcase.fragments.FragmentList;
import io.display.displayioshowcase.fragments.InfoDialog;
import io.display.displayioshowcase.fragments.PagerProvider;

public class MainActivity extends AppCompatActivity {
    private DisplayPageAdapter homePageAdapter;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.entries_pager);

        configureToolbar((Toolbar)findViewById(R.id.display_toolbar), "Display.io");

        configurePager();
    }

    protected void configureToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_ic_displayio);
            supportActionBar.setTitle("");
        }
    }

    private void configurePager() {
        List<PagerProvider> mProviderList = new FragmentList().getFragments();

        homePageAdapter = new DisplayPageAdapter(getSupportFragmentManager(), mProviderList, this);
        mPager.setOffscreenPageLimit(mProviderList.size());
        mPager.setAdapter(homePageAdapter);
    }

    private void setUpTabIcons() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        setUpTabIcons();
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
        new InfoDialog().show(getFragmentManager(), "dlg1");
    }
}
