
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class ThemeSelectGridLayout extends GridLayout {
    private Context mContext;

    public interface DismissCallback {
        public void dismissThemeDialog();
    }

    private DismissCallback mDismissCallback;

    public void setCallback(DismissCallback d) {
        mDismissCallback = d;
        initThemes(mContext);
    }

    private int mElementWidth;

    public ThemeSelectGridLayout(Context context) {
        this(context, null);
    }

    public ThemeSelectGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeSelectGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

    }

    private void initThemes(Context context) {
        setBackgroundColor(0xccffffff);
        int margin = (int)context.getResources().getDimension(R.dimen.theme_sample_view_margin);
        mElementWidth = (context.getResources().getDisplayMetrics().widthPixels - 12 * margin)
                / context.getResources().getInteger(R.integer.theme_selector_dialog_column);
        GridLayout.LayoutParams gl;

        // blue
        gl = getGridLayoutParams(context, margin);
        ThemeSample v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_BLUE);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // white
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_WHITE);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // black
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_BLACK);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // orange
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_ORANGE);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // yellow
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_YELLOW);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // gray
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_GRAY);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // navy
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_NAVY);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // purple
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_PURPLE);
        v.setCallback(mDismissCallback);
        addView(v, gl);

        // simple_white
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setTheme(U2bPlayerMainFragmentActivity.THEME_SIMPLE_WHITE);
        v.setCallback(mDismissCallback);
        addView(v, gl);
    }

    private GridLayout.LayoutParams getGridLayoutParams(Context context, int margin) {
        GridLayout.LayoutParams gl = new GridLayout.LayoutParams();
        gl.height = (int)context.getResources().getDimension(R.dimen.theme_sample_view_height) * 6;
        gl.width = mElementWidth;
        gl.setMargins(margin, margin, margin, margin);
        return gl;
    }
}
