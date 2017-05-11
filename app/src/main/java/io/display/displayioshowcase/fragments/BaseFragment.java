package io.display.displayioshowcase.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.display.displayioshowcase.EntriesRVAdapter;
import io.display.displayioshowcase.InterstitialAd;
import io.display.displayioshowcase.ListRvAdapter;
import io.display.displayioshowcase.R;
import io.display.sdk.Controller;
import io.display.sdk.DioSdkException;
import io.display.sdk.Placement;

/**
 * Created by Nicolae on 04.05.2017.
 */

public abstract class BaseFragment extends Fragment{

    protected Controller ctrl;
    protected ArrayList<Object[]> mFeedItems;
    protected EntriesRVAdapter mRVAdapter;
    protected RecyclerView rvList;
    protected LinearLayoutManager mLinearLM;
    protected ArrayList<InterstitialAd> rvListItems;
    protected ListRvAdapter rvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onLoad(view);
    }

    protected void initController(String placementId, JSONObject jsonObject) {
        ctrl = Controller.getInstance();
        ctrl.forceInit(getActivity());
        try {
            ctrl.placements.put(placementId, new Placement(placementId));
            Placement plcm = ctrl.placements.get(placementId);
            plcm.setup(jsonObject);
        } catch (DioSdkException e) {
            e.printStackTrace();
        }
    }

    protected void setItemsList(View view) {
        rvList = (RecyclerView) view.findViewById(getRvItemsList());

        rvListItems = createItemsList();
        mLinearLM = new GridLayoutManager(getContext(), 1);

        rvAdapter = new ListRvAdapter(rvListItems);
        rvAdapter.setOnItemClickListener(new InterstitialStaticFragment.ItemClickListener() {
            @Override
            public void onClicked(InterstitialAd item) {
                initController(item.getPlacementId(), getAdJson(item));
                displayAd(item.getPlacementId());
            }
        });

        rvList.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        rvList.setAdapter(rvAdapter);
    }

    protected void setList(View view) {
        rvList = (RecyclerView) view.findViewById(getRvList());

        mFeedItems = createList();
        mFeedItems = insertAds(mFeedItems, getAdPosition());
        mLinearLM = new GridLayoutManager(getContext(), 1);

        mRVAdapter = new EntriesRVAdapter(getActivity(), mFeedItems, getDefaultPlacementMapping());

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvList.setAdapter(mRVAdapter);

        setListDivider();
    }

    protected void displayAd(String placementId) {
        if(ctrl != null && ctrl.isInitialized())
            ctrl.showAd(getActivity(), placementId);
    }

    protected ArrayList<Object[]> insertAds(ArrayList<Object[]> mFeedItems, int position) {
        if (mFeedItems.size() >= position)
            mFeedItems.add(position, new Object[]{"1", "2", R.drawable.infeed_static});
        return mFeedItems;
    }

    protected void setListDivider() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), mLinearLM.getOrientation());
        rvList.addItemDecoration(dividerItemDecoration);
    }

    protected HashMap<Integer, String> getDefaultPlacementMapping() {
        HashMap<Integer, String> mPlcMap = new HashMap<>();
        mPlcMap.put(getAdPosition(), getPlacementId());
        return mPlcMap;
    }

    protected ArrayList<Object[]> createList() {
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

    protected abstract int layoutId();

    public abstract String getFragmentTitle();

    public abstract void onLoad(View view);

    public abstract String getPlacementId();

    public abstract int getTabIcon();

    protected abstract JSONObject getAdJson(InterstitialAd item);

    protected int getAdPosition(){
        return 0;
    };

    protected ArrayList<InterstitialAd> createItemsList(){
        return null;
    };

    protected int getRvList(){
        return 0;
    };

    protected int getRvItemsList() {
        return 0;
    }
}
