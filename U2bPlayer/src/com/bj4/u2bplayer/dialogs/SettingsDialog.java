
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SettingsDialog extends SubDialogs {
    private ToggleButton mQualityGroup, mOptimizeParsingGroup, mShowStatusGroup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.settings_dialog, null);
        mQualityGroup = (ToggleButton)v.findViewById(R.id.music_quality_toggle);
        mQualityGroup.setChecked(PlayMusicApplication.sUsingHighQuality);
        mQualityGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PlayMusicApplication.setMusicQuality(context, isChecked);
            }
        });

        mOptimizeParsingGroup = (ToggleButton)v.findViewById(R.id.optimize_parsing_toggle);
        mOptimizeParsingGroup.setChecked(PlayMusicApplication.sOptimizeParsing);
        mOptimizeParsingGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PlayMusicApplication.setOptimizeParsing(context, isChecked);
            }
        });

        mShowStatusGroup = (ToggleButton)v.findViewById(R.id.show_status_bar_toggle);
        mShowStatusGroup.setChecked(PlayMusicApplication.sShowStatus);
        mShowStatusGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PlayMusicApplication.setShowStatus(context, isChecked);
            }
        });

        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_settings).setCancelable(true).setView(v);
        return builder.create();
    }
}
