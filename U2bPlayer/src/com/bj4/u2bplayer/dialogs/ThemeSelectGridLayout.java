
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class ThemeSelectGridLayout extends GridLayout {
    public ThemeSelectGridLayout(Context context) {
        this(context, null);
    }

    public ThemeSelectGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeSelectGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThemes(context);
    }

    private void initThemes(Context context) {
        setBackgroundColor(0xccffffff);
        int margin = (int) context.getResources().getDimension(R.dimen.theme_sample_view_margin);
        GridLayout.LayoutParams gl = getGridLayoutParams(context, margin);

        // blue
        ThemeSample v = new ThemeSample(context);
        v.setColors(R.color.theme_blue_action_bar_bg, R.drawable.theme_blue_list_light_oval_bg_unpress,
                R.drawable.theme_blue_list_dark_oval_bg_unpress,
                R.drawable.theme_blue_list_selected_item_oval_bg, R.color.theme_blue_action_bar_bg,
                R.color.theme_blue_activity_bg);
        addView(v, gl);

        // white
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_white_action_bar_bg, R.drawable.theme_white_list_light_oval_bg_unpress,
                R.drawable.theme_white_list_dark_oval_bg_unpress,
                R.drawable.theme_white_list_selected_item_oval_bg,
                R.color.theme_white_action_bar_bg,
                R.color.theme_white_activity_bg);
        addView(v, gl);

        // black
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_black_action_bar_bg, R.drawable.theme_black_list_light_oval_bg_unpress,
                R.drawable.theme_black_list_dark_oval_bg_unpress,
                R.drawable.theme_black_list_selected_item_oval_bg,
                R.color.theme_black_action_bar_bg,
                R.color.theme_black_activity_bg);
        addView(v, gl);

        // orange
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_orange_action_bar_bg, R.drawable.theme_orange_list_light_oval_bg_unpress,
                R.drawable.theme_orange_list_dark_oval_bg_unpress,
                R.drawable.theme_orange_list_selected_item_oval_bg,
                R.color.theme_orange_action_bar_bg,
                R.color.theme_orange_activity_bg);
        addView(v, gl);

        // yellow
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_yellow_action_bar_bg, R.drawable.theme_yellow_list_light_oval_bg_unpress,
                R.drawable.theme_yellow_list_dark_oval_bg_unpress,
                R.drawable.theme_yellow_list_selected_item_oval_bg,
                R.color.theme_yellow_action_bar_bg,
                R.color.theme_yellow_activity_bg);
        addView(v, gl);

        // gray
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_gray_action_bar_bg, R.drawable.theme_gray_list_light_oval_bg_unpress,
                R.drawable.theme_gray_list_dark_oval_bg_unpress,
                R.drawable.theme_gray_list_selected_item_oval_bg, R.color.theme_gray_action_bar_bg,
                R.color.theme_gray_activity_bg);
        addView(v, gl);

        // navy
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_navy_action_bar_bg, R.drawable.theme_navy_list_light_oval_bg_unpress,
                R.drawable.theme_navy_list_dark_oval_bg_unpress,
                R.drawable.theme_navy_list_selected_item_oval_bg, R.color.theme_navy_action_bar_bg,
                R.color.theme_navy_activity_bg);
        addView(v, gl);

        // purple
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_purple_action_bar_bg, R.drawable.theme_purple_list_light_oval_bg_unpress,
                R.drawable.theme_purple_list_dark_oval_bg_unpress,
                R.drawable.theme_purple_list_selected_item_oval_bg,
                R.color.theme_purple_action_bar_bg,
                R.color.theme_purple_activity_bg);
        addView(v, gl);

        // simple_white
        gl = getGridLayoutParams(context, margin);
        v = new ThemeSample(context);
        v.setColors(R.color.theme_simple_white_action_bar_bg,
                R.drawable.theme_simple_white_list_light_oval_bg_unpress,
                R.drawable.theme_simple_white_list_dark_oval_bg_unpress,
                R.drawable.theme_simple_white_list_selected_item_oval_bg,
                R.color.theme_simple_white_action_bar_bg,
                R.color.theme_simple_white_activity_bg);
        addView(v, gl);
    }

    private GridLayout.LayoutParams getGridLayoutParams(Context context, int margin) {
        GridLayout.LayoutParams gl = new GridLayout.LayoutParams();
        gl.height = (int) context.getResources().getDimension(R.dimen.theme_sample_view_height) * 6;
        gl.width = (int) context.getResources().getDimension(R.dimen.theme_sample_view_Width);
        gl.setMargins(margin, margin, margin, margin);
        return gl;
    }
}
