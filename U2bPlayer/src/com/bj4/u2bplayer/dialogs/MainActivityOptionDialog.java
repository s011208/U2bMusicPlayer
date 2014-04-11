
package com.bj4.u2bplayer.dialogs;

import java.util.ArrayList;

import com.bj4.u2bplayer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MainActivityOptionDialog extends DialogFragment {
    public static final int ITEM_DOWNLOAD_DATA = 0;

    public interface MainActivityOptionDialogCallback {
        public void onSelected(int option);
    }

    private Context mContext;

    private MainActivityOptionDialogCallback mCallback;

    private int mWidth;

    private int[] mLocation = {
            0, 0
    };

    public MainActivityOptionDialog(Context context, MainActivityOptionDialogCallback callback,
            int[] l) {
        mContext = context;
        mCallback = callback;
        mLocation = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<CharSequence> options = new ArrayList<CharSequence>();
        final String sync = mContext.getString(R.string.option_download_data);
        options.add(sync);
        CharSequence[] optionsContent = new CharSequence[options.size()];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options.toArray(optionsContent), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = (String)((AlertDialog)dialog).getListView()
                        .getItemAtPosition(which);
                if (selectedItem.equals(sync)) {
                    if (mCallback != null) {
                        mCallback.onSelected(ITEM_DOWNLOAD_DATA);
                    }
                }
            }
        });
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupGravityAndPosition();
        return container;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(mWidth, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    public void setupGravityAndPosition() {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        mWidth = getResources().getDimensionPixelSize(R.dimen.main_activity_option_dialog_width);

        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.windowAnimations = android.R.style.Animation_Toast;
        lp.x = mLocation[0];
        lp.y = mLocation[1];
        window.setAttributes(lp);
    }

}
