
package com.yenhsun.u2bplayer.utilities;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class YoutubeDataParser {
    // https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters
    // https://gdata.youtube.com/feeds/api/videos?q=五月天+入陣曲&max-results=5&alt=json&orderby=viewCount&format=6&fields=entry(id,media:group(media:content(@url,@duration)))
    private static final String TAG = "YoutubeIdParser";

    public interface YoutubeIdParserResultCallback {
        public void setResult(ArrayList<PlayListInfo> infoList);
    }

    public static void showYoutubeResult(final String arthur, final String cdTitle,
            final String musicTitle,
            final YoutubeIdParserResultCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<PlayListInfo> infoList = new ArrayList<PlayListInfo>();
                String key = arthur + "+" + cdTitle + "+" + musicTitle;
                String source = "https://gdata.youtube.com/feeds/api/videos?q="
                        + Uri.encode(key)
                        + "&max-results=1&alt=json&format=6&fields=entry(id,media:group(media:content(@url,@duration)))";
                JSONArray jArray = YoutubeDataParser
                        .parse(source);
                if (jArray != null) {
                    try {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jOb = ((JSONObject) jArray.get(i));
                            String videoId = jOb.getJSONObject("id")
                                    .getString("$t");
                            String rtspH = ((JSONObject) jOb.getJSONObject("media$group")
                                    .getJSONArray("media$content").get(2))
                                    .getString("url");
                            String rtspL = ((JSONObject) jOb.getJSONObject("media$group")
                                    .getJSONArray("media$content").get(1))
                                    .getString("url");
                            String httpUri = ((JSONObject) jOb.getJSONObject("media$group")
                                    .getJSONArray("media$content").get(0))
                                    .getString("url");
                            infoList.add(new PlayListInfo(arthur, cdTitle, musicTitle, rtspH,
                                    rtspL,
                                    httpUri, videoId));
                        }
                    } catch (JSONException e) {
                    }
                }
                if (callback != null) {
                    callback.setResult(infoList);
                }
            }
        }).start();
    }

    private static JSONArray parse(String url) {
        return convertFromStringToJson(parseOnInternet(url));
    }

    @SuppressWarnings("deprecation")
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
