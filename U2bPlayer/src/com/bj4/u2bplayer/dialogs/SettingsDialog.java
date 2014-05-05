
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingsDialog extends SubDialogs {

    private RadioGroup mQualityGroup, mOptimizeParsingGroup, mShowStatusGroup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.settings_dialog, null);
        mQualityGroup = (RadioGroup)v.findViewById(R.id.music_quality_group);
        mQualityGroup.check(PlayMusicApplication.sUsingHighQuality ? R.id.music_quality_high
                : R.id.music_quality_low);
        mQualityGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PlayMusicApplication.setMusicQuality(context, checkedId == R.id.music_quality_high);
            }
        });
        mOptimizeParsingGroup = (RadioGroup)v.findViewById(R.id.optimize_parsing_group);
        mOptimizeParsingGroup
                .check(PlayMusicApplication.sOptimizeParsing ? R.id.optimize_parsing_true
                        : R.id.optimize_parsing_false);
        mOptimizeParsingGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PlayMusicApplication.setOptimizeParsing(context,
                        checkedId == R.id.optimize_parsing_true);
            }
        });
        mShowStatusGroup = (RadioGroup)v.findViewById(R.id.show_status_bar_group);
        mShowStatusGroup.check(PlayMusicApplication.sShowStatus ? R.id.show_status_bar
                : R.id.hide_status_bar);
        mShowStatusGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PlayMusicApplication.setShowStatus(context,
                        checkedId == R.id.show_status_bar);
            }
        });
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_settings).setCancelable(true).setView(v);
        return builder.create();
    }
}
