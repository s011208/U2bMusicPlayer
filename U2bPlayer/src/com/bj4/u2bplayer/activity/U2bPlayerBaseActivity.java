
package com.bj4.u2bplayer.activity;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.dialogs.MainActivityOptionDialog;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class U2bPlayerBaseActivity extends Activity {

    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private ImageButton mOptionBtn;

    public void onResume() {
        super.onResume();
        initActionBarComponents();
    }

    public void initActionBarComponents() {
        if (mOptionBtn == null) {
            mOptionBtn = (ImageButton)findViewById(R.id.menu);
            if (mOptionBtn == null) {
                if (DEBUG)
                    Log.w(TAG, "cannot find option button");
                return;
            }
            mOptionBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    int[] location1 = {
                            0, 0
                    };
                    Rect r = new Rect();
                    v.getLocationInWindow(location1);
                    v.getLocalVisibleRect(r);
                    display.getSize(size);
                    int[] location = {
                            size.x - location1[0] - r.width(), 2 * location1[1]
                    };
                    new MainActivityOptionDialog(U2bPlayerBaseActivity.this, mOptionCallback,
                            location).show(getFragmentManager(), "");
                }
            });
        }
    }

    private MainActivityOptionDialog.MainActivityOptionDialogCallback mOptionCallback = new MainActivityOptionDialog.MainActivityOptionDialogCallback() {

        @Override
        public void onSelected(int option) {
            switch (option) {
                case MainActivityOptionDialog.ITEM_DOWNLOAD_DATA:
                    if (DEBUG) {
                        Log.d(TAG, "action bar -- sync pressed");
                    }
                    startToScan();
                    break;
            }
        }
    };

    private void startToScan() {
        // scan list
        PlayMusicApplication.getPlayScanner().scan();
    }
}
