
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
    // https://gdata.youtube.com/feeds/api/videos?q=五月天+入陣曲&max-results=5&alt=json&format=6&fields=entry(id,media:group(media:content(@url,@duration)))
    private static final String TAG = "YoutubeIdParser";

    public interface YoutubeIdParserResultCallback {
        public void setResult(ArrayList<String> idList, ArrayList<String> rtspList);
    }

    public static void showYoutubeResult(final String[] searchKey,
            final YoutubeIdParserResultCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<String> idList = new ArrayList<String>();
                final ArrayList<String> rtspList = new ArrayList<String>();
                String key = "";
                for (String k : searchKey) {
                    key += k + "+";
                }
                if ("".equals(key))
                    return;
                else
                    key = key.substring(0, key.length() - 1);
                JSONArray jArray = YoutubeIdParser
                        .parse("https://gdata.youtube.com/feeds/api/videos?q="
                                + Uri.encode(key)
                                + "&max-results=5&alt=json&format=6&fields=entry(id,media:group(media:content(@url,@duration)))");
                if (jArray != null) {
                    try {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jOb = ((JSONObject) jArray.get(i));
                            String id = jOb.getJSONObject("id")
                                    .getString("$t");
                            idList.add(id.substring(id.lastIndexOf("/") + 1));
                            String rtsp = ((JSONObject) jOb.getJSONObject("media$group")
                                    .getJSONArray("media$content").get(2))
                                    .getString("url");
                            rtspList.add(rtsp);
                        }
                    } catch (JSONException e) {
                    }
                }
                if (callback != null) {
                    callback.setResult(idList, rtspList);
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
