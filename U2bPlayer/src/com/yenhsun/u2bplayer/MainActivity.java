
package com.yenhsun.u2bplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity {

    TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResult = (TextView) findViewById(R.id.parse_result);

    }

    private void showYoutubeResult() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String result = "";
                JSONArray jArray = YoutubeIdParser
                        .parse("https://gdata.youtube.com/feeds/api/videos?q="
                                + Uri.encode("五月天+入陣曲")
                                + "&max-results=5&alt=json");
                if (jArray != null) {
                    try {
                        for (int i = 0; i < jArray.length(); i++) {
                            String raw = ((JSONObject) jArray.get(i)).getJSONObject("id")
                                    .getString("$t");
                            result += raw.substring(raw.lastIndexOf("/") + 1) + "\n";
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.e("QQQQ", "failed", e);
                    }

                    final String r = result;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mResult.setText(r);
                            Log.e("QQQQ", "DONE");
                        }
                    });
                } else
                    Log.e("QQQQ", "null");

            }
        }).start();
    }

}
