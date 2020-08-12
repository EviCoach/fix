package com.columnhack.fix.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.columnhack.fix.R;

public class ServiceLoadingDialog extends DialogFragment {

    private Context mContext;
    private AlertDialog mDialog;

    public ServiceLoadingDialog(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(mContext)
                .setView(R.layout.service_dialog)
                .setCancelable(false)
                .create();
    }
}
