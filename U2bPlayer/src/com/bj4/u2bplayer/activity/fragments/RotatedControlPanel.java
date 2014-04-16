
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class RotatedControlPanel extends RelativeLayout {
    private U2bPlayInfoFragment mParent;

    public RotatedControlPanel(Context context) {
        this(context, null);
    }

    public RotatedControlPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatedControlPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void setTheme(final int theme) {
        if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
            setBackgroundResource(R.drawable.theme_blue_play_info_triangle_bg_right);
        }
    }

    public void setParent(U2bPlayInfoFragment p) {
        mParent = p;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
