package io.display.displayioshowcase.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import io.display.displayioshowcase.BuildConfig;
import io.display.displayioshowcase.EntriesRVAdapter;
import io.display.displayioshowcase.JsonStubs;
import io.display.displayioshowcase.R;

/**
 * Created by Nicolae on 10.05.2017.
 */

public class InfeedStaticFragment extends BaseFragment implements PagerProvider{
    private static int FIRST_INFEED_POSITION = 3;

    protected ArrayList<Object[]> mFeedItems;
    protected EntriesRVAdapter mRVAdapter;

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
        return R.layout.fragment_static_infeed;
    }

    @Override
    public String getFragmentTitle() {
        return "InFeed Static";
    }

    @Override
    public void onLoad(View view) {
        initController("113", new JsonStubs().getInfeedStaticJsonStub());
        RecyclerView rvList = (RecyclerView) view.findViewById(R.id.recycler_static_view);

        mFeedItems = createList();
        mFeedItems = insertAds(mFeedItems);

        mRVAdapter = new EntriesRVAdapter(getActivity(), mFeedItems, getDefaultPlacementMapping());

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvList.setAdapter(mRVAdapter);
    }

    public HashMap<Integer, String> getDefaultPlacementMapping() {
        HashMap<Integer, String> mPlcMap = new HashMap<>();
        mPlcMap.put(FIRST_INFEED_POSITION, "113");
        return mPlcMap;
    }

    private ArrayList<Object[]> createList() {
        mFeedItems = new ArrayList<>();
        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        mFeedItems.add(new Object[]{"US Unemployment Falls to Pre-Crisis Low", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_1});
        mFeedItems.add(new Object[]{"Tories Set for Biggest Local Election Win in Decades", "Tories Set for Biggest Local Election Win in Decades", R.drawable.img_2});
        mFeedItems.add(new Object[]{"Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", "Traders Vote Macron as Le Pen Vows to Wipe the Smiles Off Their Faces", R.drawable.img_3});
        mFeedItems.add(new Object[]{"Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", "Brexit bickering: EU’s Juncker says he’ll stop ‘speaking English’ because it’s losing importance", R.drawable.img_4});
        mFeedItems.add(new Object[]{"1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", "1st large Chinese-made passenger jet C919 takes flight, seeks to rival Boeing & Airbus", R.drawable.img_5});

        return mFeedItems;
    }

    public ArrayList<Object[]> insertAds(ArrayList<Object[]> mFeedItems) {
        if (mFeedItems.size() >= FIRST_INFEED_POSITION)
            mFeedItems.add(FIRST_INFEED_POSITION, new Object[]{"1", "2", R.drawable.infeed_static_1});
        return mFeedItems;
    }
}
