package com.marcoslopez7.stormy.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.marcoslopez7.stormy.R;

/**
 * Created by user on 28/10/2015.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_mensaje)
                .setPositiveButton(R.string.error_ok_button, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
