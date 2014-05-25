
package com.bj4.u2bplayer.dialogs;

import java.util.ArrayList;
import java.util.HashSet;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SettingsDialog extends SubDialogs {
    private ToggleButton mQualityGroup, mOptimizeParsingGroup, mShowStatusGroup, mAllow3GUpdate,
            mShowNotificationWhenHeasetOn;

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

        mAllow3GUpdate = (ToggleButton)v.findViewById(R.id.allow_3g_update);
        mAllow3GUpdate.setChecked(PlayMusicApplication.sAllow3GUpdate);
        mAllow3GUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PlayMusicApplication.setAllow3gUpdate(context, isChecked);
            }
        });
        mShowNotificationWhenHeasetOn = (ToggleButton)v
                .findViewById(R.id.show_notification_when_headset_on_toggle);
        mShowNotificationWhenHeasetOn
                .setChecked(PlayMusicApplication.sShowNotificationWhenHeadsetOn);
        mShowNotificationWhenHeasetOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PlayMusicApplication.setShowNotificationWhenHeadsetOn(context, isChecked);
            }
        });
        initDataSourceList(v);
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_settings).setCancelable(true).setView(v);
        return builder.create();
    }

    private void initDataSourceList(View parent) {
        // data source
        LinearLayout sourceList = (LinearLayout)parent.findViewById(R.id.data_soruce_list);
        String[] sources = getResources().getStringArray(R.array.data_source_list);
        final SharedPreferences pref = PlayMusicApplication.getPref(getActivity());
        final HashSet<String> dataSet = new HashSet<String>((HashSet<String>)pref.getStringSet(
                U2bPlayerMainFragmentActivity.SHARE_PREF_KEY_SOURCE_LIST, new HashSet<String>()));
        final boolean[] checkedList = new boolean[sources.length];
        for (int i = 0; i < checkedList.length; i++) {
            if (dataSet.contains(String.valueOf(i))) {
                checkedList[i] = true;
            } else {
                checkedList[i] = false;
            }
        }
        final ArrayList<CheckBox> cbList = new ArrayList<CheckBox>();
        for (int i = 0; i < sources.length; i++) {
            final int position = i;
            CheckBox cb = new CheckBox(getActivity());
            cbList.add(cb);
            cb.setText(sources[position]);
            cb.setTextColor(Color.BLACK);
            cb.setChecked(checkedList[position]);
            if (dataSet.size() <= 1 && checkedList[position]) {
                cb.setEnabled(false);
            } else {
                cb.setEnabled(true);
            }
            cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkedList[position] = isChecked;
                    if (isChecked) {
                        dataSet.add(String.valueOf(position));
                    } else {
                        dataSet.remove(String.valueOf(position));
                    }
                    for (int j = 0; j < cbList.size(); j++) {
                        CheckBox check = cbList.get(j);
                        if (dataSet.size() <= 1 && checkedList[j]) {
                            check.setEnabled(false);
                        } else {
                            check.setEnabled(true);
                        }
                    }
                    pref.edit()
                            .putStringSet(U2bPlayerMainFragmentActivity.SHARE_PREF_KEY_SOURCE_LIST,
                                    dataSet).commit();
                    getActivity().sendBroadcast(
                            new Intent(
                                    U2bPlayerMainFragmentActivity.DATA_SOURCE_LIST_CHANGED_INTENT));
                }
            });
            sourceList.addView(cb);
        }

    }
}
