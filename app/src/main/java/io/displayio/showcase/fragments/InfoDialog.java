package io.displayio.showcase.fragments;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.displayio.showcase.R;

/**
 * Created by Nicolae on 08.05.2017.
 */

public class InfoDialog extends DialogFragment {
    private OnCloseListener onCloseListener;

    public void setOnClickListener(OnCloseListener onCloseListener){
        this.onCloseListener = onCloseListener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment, null);
        v.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onCloseListener != null)
            onCloseListener.onClosed();
    }

    public interface OnCloseListener{
        void onClosed();
    }
}
