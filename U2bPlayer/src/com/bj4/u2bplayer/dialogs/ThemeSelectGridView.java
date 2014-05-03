
package com.bj4.u2bplayer.dialogs;

import java.util.ArrayList;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;

public class ThemeSelectGridView extends GridView {
    private Context mContext;
    private static final ArrayList<Integer> THEME_LIST = new ArrayList<Integer>();
    static {
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_BLUE);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_WHITE);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_BLACK);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_ORANGE);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_YELLOW);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_GRAY);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_NAVY);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_PURPLE);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_SIMPLE_WHITE);
        THEME_LIST.add(U2bPlayerMainFragmentActivity.THEME_RED);
    }

    public interface DismissCallback {
        public void dismissThemeDialog();
    }

    private DismissCallback mDismissCallback;

    private class ThemeAdapter extends ArrayAdapter<Integer> {

        private Context mContext;
        private LayoutInflater mInflater;

        public ThemeAdapter(Context context) {
            super(context, R.layout.theme_sample, THEME_LIST);
            mContext = context;
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.theme_sample, parent, false);
            }
            ((ThemeSample) row).setTheme(getItem(position));
            return row;
        }
    }

    public void setCallback(DismissCallback d) {
        mDismissCallback = d;
        this.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mContext.sendBroadcast(new Intent(
                        U2bPlayerMainFragmentActivity.THEME_CHANGED_INTENT).putExtra(
                        U2bPlayerMainFragmentActivity.THEME_CHANGED_INTENT_EXTRA_THEME, position));
                if (mDismissCallback != null) {
                    mDismissCallback.dismissThemeDialog();
                }
            }
        });
    }

    public ThemeSelectGridView(Context context) {
        this(context, null);
    }

    public ThemeSelectGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeSelectGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setNumColumns(mContext.getResources().getInteger(R.integer.theme_selector_dialog_column));

        ThemeAdapter ta = new ThemeAdapter(mContext);
        setAdapter(ta);
    }
}
