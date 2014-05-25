
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

public class ThemeSelectDialog extends SubDialogs implements ThemeSelectGridView.DismissCallback {

    public void dismissThemeDialog() {
        dismiss();
    }

    public AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_Translucent);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.theme_select_dialog, null);
        v.setBackgroundColor(0xffffff);
        ThemeSelectGridView gl = (ThemeSelectGridView)v
                .findViewById(R.id.dialog_theme_select_grid_layout);
        gl.setCallback(this);
        Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(v);
        mDialog.setCancelable(true);
        return mDialog;
    }

    public void setupGravityAndPosition() {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        int marginV = (int)getActivity().getResources().getDimension(
                R.dimen.theme_selector_dialog_margin_v);
        int marginH = (int)getActivity().getResources().getDimension(
                R.dimen.theme_selector_dialog_margin_h);
        int windowHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        int windowWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = windowWidth - marginH * 2;
        lp.height = windowHeight - marginV * 2;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }
}
