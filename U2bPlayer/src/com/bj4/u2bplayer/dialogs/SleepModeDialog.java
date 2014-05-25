
package com.bj4.u2bplayer.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bj4.u2bplayer.R;

public class SleepModeDialog extends SubDialogs {

    public interface SleepModeDialogCallback {
        public void onItemSelected(int type);
    }

    private SleepModeDialogCallback mCallback;

    public static final int DISABLE = 0;

    public static final int MINS_15 = 1;

    public static final int MINS_30 = 2;

    public static final int HOUR_1 = 3;

    public static final int HOUR_2 = 4;

    public static final int HOUR_3 = 5;

    public SleepModeDialog(SleepModeDialogCallback callback) {
        super();
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        Spinner spinner = new Spinner(context);
        final String[] items = context.getResources().getStringArray(R.array.sleep_mode_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.sleep_mode_spinner, items);
        adapter.setDropDownViewResource(R.layout.sleep_mode_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (mCallback != null) {
                    mCallback.onItemSelected(arg2);
                    Toast.makeText(context, items[arg2], Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setTitle(R.string.option_sleep_mode).setCancelable(true).setView(spinner);
        return builder.create();
    }
}
