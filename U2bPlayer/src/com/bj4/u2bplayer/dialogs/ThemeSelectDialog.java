
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class ThemeSelectDialog extends SubDialogs implements
        ThemeSelectGridView.DismissCallback {

    public void dismissThemeDialog() {
        dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.theme_select_dialog, null);
        ThemeSelectGridView gl = (ThemeSelectGridView) v
                .findViewById(R.id.dialog_theme_select_grid_layout);
        gl.setCallback(this);
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_theme).setCancelable(true).setView(v);
        return builder.create();
    }
}
