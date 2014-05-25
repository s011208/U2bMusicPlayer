
package com.bj4.u2bplayer.dialogs;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MainActivityOptionDialog extends DialogFragment {

    private static final boolean DEBUG = false;

    public static final int ITEM_DOWNLOAD_DATA = 0;

    public static final int ITEM_NON_ADS = 1;

    public static final int ITEM_SLEEP_MODE = 2;

    public interface MainActivityOptionDialogCallback {
        public void onSelected(int option);
    }

    private Context mContext;

    private MainActivityOptionDialogCallback mCallback;

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
        final String theme = mContext.getString(R.string.option_theme);
        options.add(theme);
        final String settings = mContext.getString(R.string.option_settings);
        options.add(settings);
        final String share = mContext.getString(R.string.option_share);
        options.add(share);
        final String sleepMode = mContext.getString(R.string.option_sleep_mode);
        options.add(sleepMode);
        final String noAds = mContext.getString(R.string.option_no_ads);
        if (PlayMusicApplication.sAdAvailable == true)
            options.add(noAds);

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
                } else if (selectedItem.equals(theme)) {
                    ThemeSelectDialog ts = new ThemeSelectDialog();
                    ts.show(getActivity().getFragmentManager(), "");
                } else if (selectedItem.equals(settings)) {
                    SettingsDialog sd = new SettingsDialog();
                    sd.show(getActivity().getFragmentManager(), "");
                } else if (selectedItem.equals(share)) {
                    PlayList pl = PlayList.getInstance(mContext);
                    PlayListInfo info = pl.getCurrentPlayingListInfo();
                    if (info != null) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                                mContext.getString(R.string.app_name));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, createShareText(mContext, info));
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent,
                                getResources().getText(R.string.share_chooser_title)));
                    }
                } else if (selectedItem.equals(noAds)) {
                    if (mCallback != null) {
                        mCallback.onSelected(ITEM_NON_ADS);
                    }
                } else if (selectedItem.equals(sleepMode)) {
                    if (mCallback != null) {
                        mCallback.onSelected(ITEM_SLEEP_MODE);
                    }
                }
            }
        });
        return builder.create();
    }

    private static final String createShareText(Context context, PlayListInfo info) {
        String former = context.getString(R.string.share_string_former);
        String rear = context.getString(R.string.share_string_rear);
        return former + info.mMusicTitle + rear;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupGravityAndPosition();
        return container;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    public void setupGravityAndPosition() {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.x = mLocation[0];
        lp.y = mLocation[1];
        window.setAttributes(lp);
    }

}
