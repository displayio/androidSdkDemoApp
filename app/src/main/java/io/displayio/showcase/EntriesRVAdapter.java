package io.displayio.showcase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import io.displayio.sdk.Controller;
import io.displayio.sdk.DioSdkException;
import io.displayio.sdk.ads.InfeedAdContainer;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Nicolae on 05.05.2017.
 */

public class EntriesRVAdapter extends RecyclerView.Adapter<EntriesRVAdapter.BaseHolder> {
    private Context context;
    private static final int AD_ITEM = 0;
    private static final int FIRST_ITEM = 1;
    private static final int SIMPLE_ITEM = 2;
    HashMap<Integer, String> mPlacementPositions = new HashMap<>();
    private HashMap<Integer, Object[]> mAdsMap = new HashMap<>();
    private ArrayList<Object[]> itemsList;

    public EntriesRVAdapter(Context context, ArrayList<Object[]> itemsList, HashMap<Integer, String> defaultPlacementMapping) {
        this.context = context;
        this.itemsList = itemsList;
        this.mPlacementPositions = defaultPlacementMapping;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout adView;
        BaseHolder holder;

        switch (viewType) {
            case FIRST_ITEM:
                adView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_list_first, null);
                holder = new FirstItemViewHolder(adView);
                break;
            case AD_ITEM:
                adView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_list_item, null);
                holder = new AdHolder(adView);
                break;
            case SIMPLE_ITEM:
            default:
                adView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_list, null);
                holder = new AdHolder(adView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case AD_ITEM:
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkAndAttachAdToRow((AdHolder)holder, holder.getAdapterPosition());
                        } catch (DioSdkException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case SIMPLE_ITEM:
            case FIRST_ITEM:
                holder.titleTextView.setText((String)itemsList.get(position)[0]);
                holder.descriptionTextView.setText((String)itemsList.get(position)[1]);
                holder.mainImgView.setImageResource((int)itemsList.get(position)[2]);
        }
    }

    private void checkAndAttachAdToRow(final AdHolder holder, final int position) throws DioSdkException {
        if (!isViewObtained(position)) {
            try {
                getInFeedAd(holder, position);
            } catch (DioSdkException e) {
                setViewHeight(holder, 0);
                e.printStackTrace();
            }
        } else {
            displayAd(holder, (View) mAdsMap.get(position)[0], (Integer) mAdsMap.get(position)[1]);
        }
    }

    private boolean isViewObtained(int rowPosition) {
        return mAdsMap != null && mAdsMap.containsKey(rowPosition);
    }

    private void getInFeedAd(AdHolder holder, int position) throws DioSdkException {
        String mPlacementId = mPlacementPositions.get(position);
        InfeedAdContainer infeedAdContainer = Controller.getInstance().getInfeedAdContainer(context, mPlacementId);
        View mAdView = infeedAdContainer.getContainerView();
        int mAdHeight = infeedAdContainer.getFeedAdHeight();
        if (mAdHeight > 0) {
            mAdsMap.put(position, new Object[]{mAdView, mAdHeight});
            displayAd(holder, mAdView, mAdHeight);
        } else {
            setViewHeight(holder, 0);
        }
    }

    private void setViewHeight(AdHolder holder, int height) {
        RecyclerView.LayoutParams rlParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        rlParams.height = height;
        holder.itemView.setLayoutParams(rlParams);
    }

    private void displayAd(AdHolder holder, View adView, Integer adHeight) {
        if (adView != null) {
            if (adView.getParent() != null)
                ((RelativeLayout) adView.getParent()).removeAllViews();
            holder.mAdViewContainer.addView(adView, MATCH_PARENT, adHeight);
        } else {
            setViewHeight(holder, 0);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return FIRST_ITEM;
        else if (mPlacementPositions.containsKey(position))
            return AD_ITEM;
        else
            return SIMPLE_ITEM;
    }

    private static class AdHolder extends BaseHolder {
        AdHolder(View itemView) {
            super(itemView);
        }
    }

    private static class FirstItemViewHolder extends BaseHolder {
        FirstItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class BaseHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public ImageView mainImgView;
        RelativeLayout mAdViewContainer;
        public BaseHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(android.R.id.text1);
            mainImgView = (ImageView) itemView.findViewById(R.id.main_icon);
            descriptionTextView = (TextView) itemView.findViewById(R.id.tv_description);
            mAdViewContainer = (RelativeLayout) itemView.findViewById(R.id.ad_item_container);
        }
    }
}
