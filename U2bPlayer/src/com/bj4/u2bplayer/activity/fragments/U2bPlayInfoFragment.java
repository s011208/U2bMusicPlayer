
package com.bj4.u2bplayer.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class U2bPlayInfoFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO implement view here
        TextView tv = new TextView(getActivity());
        tv.setText("U2bPlayInfoFragment");
        return tv;
    }
}
