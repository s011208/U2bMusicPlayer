
package com.bj4.u2bplayer.activity;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UnCaughtExceptionHandlerActivity extends Activity {
    private static final String TAG = "UnCaughtExceptionHandlerActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String crashLog = this.getIntent()
                    .getStringExtra(PlayMusicApplication.INTENT_CRASH_LOG);
            final Dialog dialog = new Dialog(UnCaughtExceptionHandlerActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.uncaught_exception_dialog);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            TextView tv = (TextView) dialog.findViewById(R.id.exception_log);
            tv.setText(crashLog);
            Button btn = (Button) dialog.findViewById(R.id.uncaught_exit);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    System.exit(1);
                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "failed to create error handling dialog", e);
            System.exit(1);
        }
    }
}
