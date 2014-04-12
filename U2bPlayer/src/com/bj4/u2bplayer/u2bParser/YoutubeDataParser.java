
package com.bj4.u2bplayer.u2bParser;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.*;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class YoutubeDataParser implements U2bDatabaseHelper.DatabaseHelperCallback {
    // https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters
    // https://gdata.youtube.com/feeds/api/videos?q=五月天+入陣曲&max-results=5&alt=json&orderby=viewCount&format=6&fields=entry(id,media:group(media:content(@url,@duration)))
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private static final String SOURCE_PREVIOUS = "https://gdata.youtube.com/feeds/api/videos?q=";

    private static final String SOURCE_LAST = "&max-results=1&alt=json&format=6&fields=entry(id,media:group(media:content(@url,@duration)))";

    public interface YoutubeIdParserResultCallback {
        public void setResult(ArrayList<PlayListInfo> infoList);
    }

    public static void parseYoutubeData(final ArrayList<PlayListInfo> infoList,
            final YoutubeIdParserResultCallback callback) {
        if (infoList.isEmpty())
            return;
        Thread parseWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<PlayListInfo> taskIter = infoList.iterator();
                while (taskIter.hasNext()) {
                    PlayListInfo info = taskIter.next();
                    try {
                        if (DEBUG)
                            Log.i(TAG, "key: " + info.mArtist + "+" + info.mMusicTitle);
                        String source = SOURCE_PREVIOUS
                                + Uri.encode(info.mArtist + "+" + info.mMusicTitle) + SOURCE_LAST;

                        String rawData = parseOnInternet(source);
                        JSONArray entry = new JSONObject(rawData).getJSONObject("feed")
                                .getJSONArray("entry");
                        if (entry != null) {
                            for (int i = 0; i < entry.length(); i++) {
                                JSONObject jOb = ((JSONObject)entry.get(i));
                                info.mVideoId = jOb.getJSONObject("id").getString("$t");
                                info.mHttpUri = ((JSONObject)jOb.getJSONObject("media$group")
                                        .getJSONArray("media$content").get(0)).getString("url");
                                info.mRtspHighQuility = ((JSONObject)jOb
                                        .getJSONObject("media$group").getJSONArray("media$content")
                                        .get(2)).getString("url");
                                info.mRtspLowQuility = ((JSONObject)jOb
                                        .getJSONObject("media$group").getJSONArray("media$content")
                                        .get(1)).getString("url");
                                if (DEBUG)
                                    Log.d(TAG, "url: " + info.mHttpUri);
                            }
                        }
                    } catch (NullPointerException npe) {
                        taskIter.remove();
                        Log.w(TAG, "failed", npe);
                    } catch (JSONException jse) {
                        Log.w(TAG, "failed", jse);
                    }
                }
                if (callback != null) {
                    callback.setResult(infoList);
                    final U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication
                            .getDataBaseHelper();
                    if (DEBUG) {
                        mDatabaseHelper.getPlayListByArtist("韋禮安");
                        mDatabaseHelper.getPlayListByMusic("為愛而活");
                    }
                }
            }
        });
        parseWorker.setPriority(Thread.MIN_PRIORITY);
        parseWorker.start();
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

    @Override
    public void notifyDataSetChanged() {
        final U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        ArrayList<PlayListInfo> infoList = new ArrayList<PlayListInfo>();
        Cursor data = databaseHelper.queryDataForU2bParser();
        if (DEBUG)
            Log.d(TAG, "data == null: " + (data == null));
        if (data != null) {
            int columnArtist = data.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST);
            int columnAlbum = data.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM);
            int columnMusic = data.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC);
            int columnRank = data.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK);
            if (DEBUG)
                Log.d(TAG, "data size: " + data.getCount());
            while (data.moveToNext()) {
                String artist = data.getString(columnArtist);
                String album = data.getString(columnAlbum);
                String music = data.getString(columnMusic);
                int rank = data.getInt(columnRank);
                infoList.add(new PlayListInfo(artist, album, music, "", "", "", "", rank));
            }
            data.close();
            YoutubeDataParser.parseYoutubeData(infoList,
                    new YoutubeDataParser.YoutubeIdParserResultCallback() {

                        @Override
                        public void setResult(ArrayList<PlayListInfo> infoList) {
                            databaseHelper.insert(infoList);
                        }
                    });
        }
    }

}
