
package com.yenhsun.u2bplayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class YoutubeIdParser {
    // https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters
    private static final String TAG = "YoutubeIdParser";

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
