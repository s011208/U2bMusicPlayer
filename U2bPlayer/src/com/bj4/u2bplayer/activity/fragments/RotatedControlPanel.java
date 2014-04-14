
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RotatedControlPanel extends RelativeLayout {
    public RotatedControlPanel(Context context) {
        this(context, null);
    }

    public RotatedControlPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatedControlPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundResource(R.color.theme_blue_play_info_bottom_controller_bg);
    }

}
