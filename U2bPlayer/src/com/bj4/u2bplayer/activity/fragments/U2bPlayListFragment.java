
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class U2bPlayListFragment extends Fragment {

    private View mContentView;

    private RelativeLayout mControllPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponents() {
        if (mContentView != null) {
            mControllPanel = (RelativeLayout)mContentView
                    .findViewById(R.id.play_list_controll_panel);
            initTheme();
        }
    }

    private void initTheme() {
        U2bPlayerMainFragmentActivity activity = (U2bPlayerMainFragmentActivity)getActivity();
        int theme = activity.getApplicationTheme();
        if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
            mControllPanel.setBackgroundResource(R.color.theme_blue_action_bar_bg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.play_list_fragment, container, false);
        initComponents();
        return mContentView;
    }
}
