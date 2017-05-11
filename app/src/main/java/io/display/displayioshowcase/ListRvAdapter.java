package io.display.displayioshowcase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import io.display.displayioshowcase.fragments.InterstitialStaticFragment;

/**
 * Created by Nicolae on 10.05.2017.
 */

public class ListRvAdapter extends RecyclerView.Adapter<ListRvAdapter.BaseHolder>{
    private ArrayList<InterstitialAd> items;
    private InterstitialStaticFragment.ItemClickListener itemClickListener;

    public ListRvAdapter(ArrayList<InterstitialAd> items){
        this.items = items;
    }

    public void setOnItemClickListener(InterstitialStaticFragment.ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.mainImgView.setImageResource(items.get(position).getTileRes());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class BaseHolder extends RecyclerView.ViewHolder {
        public ImageView mainImgView;
        public BaseHolder(View itemView) {
            super(itemView);
            mainImgView = (ImageView) itemView.findViewById(R.id.list_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null)
                        itemClickListener.onClicked(getItem(getAdapterPosition()));
                }
            });
        }

        private InterstitialAd getItem(int adapterPosition) {
            return items.get(adapterPosition);
        }
    }
}
