package io.display.displayioshowcase.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import io.display.sdk.Controller;
import io.display.sdk.DioSdkException;
import io.display.sdk.Placement;

/**
 * Created by Nicolae on 04.05.2017.
 */

public abstract class BaseFragment extends Fragment{

    protected Controller ctrl;

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

    protected void displayAd(String placementId) {
        if(ctrl != null && ctrl.isInitialized())
            ctrl.showAd(getActivity(), placementId);
    }

    protected abstract int layoutId();

    public abstract String getFragmentTitle();

    public abstract void onLoad(View view);
}
