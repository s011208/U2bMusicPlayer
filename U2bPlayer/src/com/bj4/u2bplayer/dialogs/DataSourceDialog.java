
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DataSourceDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView v = (ListView) inflater.inflate(R.layout.data_source_dialog, null);
        DataSourceListAdapter dsList = new DataSourceListAdapter(context);
        v.setAdapter(dsList);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.option_source).setCancelable(true).setView(v);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupGravityAndPosition();
        return container;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    public void setupGravityAndPosition() {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        lp.alpha = 0.95f;
        window.setAttributes(lp);
    }

    private class DataSourceListAdapter extends BaseAdapter {

        private Context mContext;

        private String[] mSource;

        private LayoutInflater myInflater;

        public DataSourceListAdapter(Context context) {
            super();
            mContext = context;
            mSource = mContext.getResources().getStringArray(R.array.data_source_list);
            myInflater = LayoutInflater.from(mContext);
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
        public View getView(int position, View convertView, ViewGroup parent) {
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

            return convertView;
        }

        private class ViewHolder {
            CheckBox mCb;
        }
    }
}
