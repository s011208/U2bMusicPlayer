
package com.yenhsun.u2bplayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class YoutubeIdParser {
    // https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters
    private static final String TAG = "YoutubeIdParser";

    public interface YoutubeIdParserResultCallback {
        public void setResult(ArrayList<String> idList);
    }

    public static void showYoutubeResult(final String[] searchKey,
            final YoutubeIdParserResultCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<String> playList = new ArrayList<String>();
                String key = "";
                for (String k : searchKey) {
                    key += k + "+";
                }
                if ("".equals(key))
                    return;
                else
                    key = key.substring(0, key.length() - 1);
                Log.e("QQQQ", "key: " + key);
                JSONArray jArray = YoutubeIdParser
                        .parse("https://gdata.youtube.com/feeds/api/videos?q="
                                + Uri.encode(key)
                                + "&max-results=5&alt=json");
                if (jArray != null) {
                    try {
                        for (int i = 0; i < jArray.length(); i++) {
                            String raw = ((JSONObject) jArray.get(i)).getJSONObject("id")
                                    .getString("$t");
                            playList.add(raw.substring(raw.lastIndexOf("/") + 1));
                        }
                        if (callback != null) {
                            callback.setResult(playList);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
        }).start();
    }

    public static JSONArray parse(String url) {
        return convertFromStringToJson(parseOnInternet(url));
    }

    private static String parseOnInternet(String url) {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;
        StringBuilder sb = new StringBuilder();
        try {
            u = new URL(url);
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception e) {
            Log.e(TAG, "parse failed", e);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
            }
        }
        return sb.toString();
    }

    private static JSONArray convertFromStringToJson(String data) {
        try {
            JSONArray jArray = new JSONObject(data).getJSONObject("feed").getJSONArray("entry");
            return jArray;
        } catch (JSONException e) {
            Log.e(TAG, "convertFromStringToJson error", e);
        }
        return null;
    }
}
