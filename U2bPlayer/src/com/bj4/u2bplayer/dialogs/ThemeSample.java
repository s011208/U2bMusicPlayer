
package com.bj4.u2bplayer.dialogs;

import com.bj4.u2bplayer.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ThemeSample extends LinearLayout {

    private Context mContext;

    public ThemeSample(Context context) {
        super(context);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setColors(int header, int light, int dark, int selected, int footer, int background) {
        int viewHeight = (int) mContext.getResources().getDimension(
                R.dimen.theme_sample_view_height);
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

        ll = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);
        ll.setMargins(0, viewHeight, 0, 0);

        v = new View(mContext);
        v.setBackgroundResource(footer);
        addView(v, ll);

    }

}
