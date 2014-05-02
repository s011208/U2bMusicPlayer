
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.dialogs.ThemeSelectGridLayout.DismissCallback;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

public class ThemeSample extends LinearLayout {

    private Context mContext;

    private int mTheme;

    private DismissCallback mDismissCallback;

    public void setCallback(DismissCallback c) {
        mDismissCallback = c;
    }

    public ThemeSample(Context context) {
        super(context);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent(
                        U2bPlayerMainFragmentActivity.THEME_CHANGED_INTENT).putExtra(
                        U2bPlayerMainFragmentActivity.THEME_CHANGED_INTENT_EXTRA_THEME, mTheme));
                if (mDismissCallback != null) {
                    mDismissCallback.dismissThemeDialog();
                }
            }
        });
    }

    public void setTheme(int theme) {
        mTheme = theme;
        switch (theme) {
            case U2bPlayerMainFragmentActivity.THEME_BLUE:
                setColors(R.color.theme_blue_action_bar_bg,
                        R.drawable.theme_blue_list_light_oval_bg_unpress,
                        R.drawable.theme_blue_list_dark_oval_bg_unpress,
                        R.drawable.theme_blue_list_selected_item_oval_bg,
                        R.color.theme_blue_action_bar_bg, R.color.theme_blue_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_WHITE:
                setColors(R.color.theme_white_action_bar_bg,
                        R.drawable.theme_white_list_light_oval_bg_unpress,
                        R.drawable.theme_white_list_dark_oval_bg_unpress,
                        R.drawable.theme_white_list_selected_item_oval_bg,
                        R.color.theme_white_action_bar_bg, R.color.theme_white_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_BLACK:
                setColors(R.color.theme_black_action_bar_bg,
                        R.drawable.theme_black_list_light_oval_bg_unpress,
                        R.drawable.theme_black_list_dark_oval_bg_unpress,
                        R.drawable.theme_black_list_selected_item_oval_bg,
                        R.color.theme_black_action_bar_bg, R.color.theme_black_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_ORANGE:
                setColors(R.color.theme_orange_action_bar_bg,
                        R.drawable.theme_orange_list_light_oval_bg_unpress,
                        R.drawable.theme_orange_list_dark_oval_bg_unpress,
                        R.drawable.theme_orange_list_selected_item_oval_bg,
                        R.color.theme_orange_action_bar_bg, R.color.theme_orange_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_YELLOW:
                setColors(R.color.theme_yellow_action_bar_bg,
                        R.drawable.theme_yellow_list_light_oval_bg_unpress,
                        R.drawable.theme_yellow_list_dark_oval_bg_unpress,
                        R.drawable.theme_yellow_list_selected_item_oval_bg,
                        R.color.theme_yellow_action_bar_bg, R.color.theme_yellow_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_GRAY:
                setColors(R.color.theme_gray_action_bar_bg,
                        R.drawable.theme_gray_list_light_oval_bg_unpress,
                        R.drawable.theme_gray_list_dark_oval_bg_unpress,
                        R.drawable.theme_gray_list_selected_item_oval_bg,
                        R.color.theme_gray_action_bar_bg, R.color.theme_gray_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_NAVY:
                setColors(R.color.theme_navy_action_bar_bg,
                        R.drawable.theme_navy_list_light_oval_bg_unpress,
                        R.drawable.theme_navy_list_dark_oval_bg_unpress,
                        R.drawable.theme_navy_list_selected_item_oval_bg,
                        R.color.theme_navy_action_bar_bg, R.color.theme_navy_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_PURPLE:
                setColors(R.color.theme_purple_action_bar_bg,
                        R.drawable.theme_purple_list_light_oval_bg_unpress,
                        R.drawable.theme_purple_list_dark_oval_bg_unpress,
                        R.drawable.theme_purple_list_selected_item_oval_bg,
                        R.color.theme_purple_action_bar_bg, R.color.theme_purple_activity_bg);
                break;
            case U2bPlayerMainFragmentActivity.THEME_SIMPLE_WHITE:
                setColors(R.color.theme_simple_white_action_bar_bg,
                        R.drawable.theme_simple_white_list_light_oval_bg_unpress,
                        R.drawable.theme_simple_white_list_dark_oval_bg_unpress,
                        R.drawable.theme_simple_white_list_selected_item_oval_bg,
                        R.color.theme_simple_white_action_bar_bg,
                        R.color.theme_simple_white_activity_bg);
                break;
        }
    }

    public void setColors(int header, int light, int dark, int selected, int footer, int background) {
        int viewHeight = (int)mContext.getResources()
                .getDimension(R.dimen.theme_sample_view_height);
        setBackgroundResource(background);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);

        View v = new View(mContext);
        v.setBackgroundResource(header);
        addView(v, ll);

        v = new View(mContext);
        v.setBackgroundResource(light);
        addView(v, ll);

        v = new View(mContext);
        v.setBackgroundResource(dark);
        addView(v, ll);

        v = new View(mContext);
        v.setBackgroundResource(selected);
        addView(v, ll);

        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);
        ll.setMargins(0, viewHeight, 0, 0);

        v = new View(mContext);
        v.setBackgroundResource(footer);
        addView(v, ll);
    }

}