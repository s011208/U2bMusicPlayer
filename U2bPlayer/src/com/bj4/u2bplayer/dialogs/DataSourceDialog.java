
package com.bj4.u2bplayer.dialogs;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class DataSourceDialog extends SubDialogs {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout parent = (RelativeLayout) inflater
                .inflate(R.layout.data_source_dialog, null);
        ListView list = (ListView) parent.findViewById(R.id.data_source_list_view);
        final DataSourceListAdapter dsList = new DataSourceListAdapter(context);
        list.setAdapter(dsList);
        Button ok = (Button) parent.findViewById(R.id.data_source_dialog_ok);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dsList.confirm();
                dismiss();
            }
        });
        Button cancel = (Button) parent.findViewById(R.id.data_source_dialog_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_source).setCancelable(true).setView(parent);
        return builder.create();
    }

    private class DataSourceListAdapter extends BaseAdapter {
        private SharedPreferences mPref;
        private Context mContext;

        private String[] mSource;

        private LayoutInflater myInflater;

        private boolean[] mCheckedList;

        private HashSet<String> mSet;

        public void confirm() {
            mPref.edit()
                    .putStringSet(U2bPlayerMainFragmentActivity.SHARE_PREF_KEY_SOURCE_LIST,
                            mSet).commit();
            mContext.sendBroadcast(new Intent(
                    U2bPlayerMainFragmentActivity.DATA_SOURCE_LIST_CHANGED_INTENT));
        }

        public DataSourceListAdapter(Context context) {
            super();
            mContext = context;
            mSource = mContext.getResources().getStringArray(R.array.data_source_list);
            mCheckedList = new boolean[mSource.length];
            myInflater = LayoutInflater.from(mContext);
            mPref = PlayMusicApplication.getPref(mContext);
            mSet = (HashSet<String>) mPref
                    .getStringSet(U2bPlayerMainFragmentActivity.SHARE_PREF_KEY_SOURCE_LIST,
                            new HashSet<String>());
            for (int i = 0; i < mCheckedList.length; i++) {
                if (mSet.contains(String.valueOf(i))) {
                    mCheckedList[i] = true;
                } else {
                    mCheckedList[i] = false;
                }
            }
        }

        @Override
        public int getCount() {
            return mSource.length;
        }

        @Override
        public String getItem(int position) {
            return mSource[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = myInflater.inflate(R.layout.data_source_list_view, null);
                holder = new ViewHolder();
                holder.mCb = (CheckBox) convertView.findViewById(R.id.data_source_list_item_cb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mCb.setText(getItem(position));
            holder.mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCheckedList[position] = isChecked;
                    if (isChecked) {
                        mSet.add(String.valueOf(position));
                    } else {
                        mSet.remove(String.valueOf(position));
                    }
                    notifyDataSetChanged();
                }
            });
            holder.mCb.setChecked(mCheckedList[position]);
            if (mSet.size() <= 1 && mCheckedList[position]) {
                holder.mCb.setEnabled(false);
            } else {
                holder.mCb.setEnabled(true);
            }
            return convertView;
        }

        private class ViewHolder {
            CheckBox mCb;
        }
    }
}
