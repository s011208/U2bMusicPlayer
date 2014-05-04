
package com.bj4.u2bplayer.u2bParser;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.*;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class YoutubeDataParser implements U2bDatabaseHelper.DatabaseHelperCallback {
    // https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters
    // https://gdata.youtube.com/feeds/api/videos?q=五月天+入陣曲&max-results=5&alt=json&orderby=viewCount&format=6&fields=entry(id,media:group(media:content(@url,@duration)))
    // http://img.youtube.com/vi/<video id>/0.jpg
    private static final String TAG = "YoutubeDataParser";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private static final int IGNORE_DURATION = 90;

    private static final String SOURCE_PREVIOUS = "https://gdata.youtube.com/feeds/api/videos?q=";

    private static final int QUICK_NOTIFY_LIMIT = 20;

    private static final HandlerThread sWorkerThread = new HandlerThread(
            "YoutubeDataParser handler");
    static {
        sWorkerThread.setPriority(Thread.MAX_PRIORITY);
        sWorkerThread.start();
    }

    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private Context mContext;

    public YoutubeDataParser(Context context) {
        mContext = context.getApplicationContext();
    }

    public interface YoutubeIdParserResultCallback {
        public void setResult(ArrayList<PlayListInfo> infoList);
    }

    private static final String getSource(PlayListInfo info) {
        return SOURCE_PREVIOUS
                + Uri.encode(info.mArtist + "+" + info.mMusicTitle) + "&max-results="
                + (PlayMusicApplication.sOptimizeParsing ? 5 : 1)
                + "&alt=json&format=6&fields=entry(id,media:group(media:content(@url,@duration)))";
    }

    public static void parseYoutubeData(final ArrayList<PlayListInfo> infoList,
            final YoutubeIdParserResultCallback callback) {
        if (infoList.isEmpty())
            return;
        sWorker.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<PlayListInfo> listTobeInserted = new ArrayList<PlayListInfo>();
                Iterator<PlayListInfo> taskIter = infoList.iterator();
                while (taskIter.hasNext()) {
                    PlayListInfo info = taskIter.next();
                    try {
                        if (DEBUG)
                            Log.i(TAG, "key: " + info.mArtist + "+" + info.mMusicTitle);
                        String source = getSource(info);

                        String rawData = parseOnInternet(source);
                        JSONArray entry = new JSONObject(rawData).getJSONObject("feed")
                                .getJSONArray("entry");
                        if (entry != null) {
                            for (int i = 0; i < entry.length(); i++) {
                                JSONObject jOb = ((JSONObject) entry.get(i));
                                info.mVideoId = jOb.getJSONObject("id").getString("$t");
                                if (info.mVideoId != null) {
                                    info.mVideoId = info.mVideoId.substring(
                                            info.mVideoId.lastIndexOf("/") + 1,
                                            info.mVideoId.length());
                                }
                                info.mHttpUri = ((JSONObject) jOb.getJSONObject("media$group")
                                        .getJSONArray("media$content").get(0)).getString("url");
                                int duration = ((JSONObject) jOb.getJSONObject("media$group")
                                        .getJSONArray("media$content").get(0)).getInt("duration");
                                if (PlayMusicApplication.sOptimizeParsing
                                        && duration < IGNORE_DURATION) {
                                    if (DEBUG)
                                        Log.v(TAG, "duration: " + duration + ", abort");
                                    continue;
                                }
                                info.mRtspHighQuility = ((JSONObject) jOb
                                        .getJSONObject("media$group").getJSONArray("media$content")
                                        .get(2)).getString("url");
                                info.mRtspLowQuility = ((JSONObject) jOb
                                        .getJSONObject("media$group").getJSONArray("media$content")
                                        .get(1)).getString("url");
                                if (DEBUG)
                                    Log.d(TAG, "url: " + info.mHttpUri);
                                break;
                            }
                        }
                        listTobeInserted.add(info);
                    } catch (NullPointerException npe) {
                        taskIter.remove();
                        Log.w(TAG, "failed", npe);
                    } catch (JSONException jse) {
                        Log.w(TAG, "failed", jse);
                    } finally {
                        if (listTobeInserted.size() % QUICK_NOTIFY_LIMIT == 0 && callback != null) {
                            callback.setResult(listTobeInserted);
                            listTobeInserted.clear();
                        }
                    }
                }
                if (callback != null && listTobeInserted.isEmpty() == false) {
                    callback.setResult(listTobeInserted);
                }
            }
        });
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
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    PlayList.getInstance(mContext).notifyScanDone();
                                }
                            }).start();
                        }
                    });
        }
    }

    public static Bitmap getYoutubeThumbnail(String vId) {
        return getYoutubeThumbnail(vId, 0);
    }

    /**
     * @param vId video id
     * @param num 0 is the largest one, 1&2&3 are smaller
     * @return Bitmap if retrieve something, else null
     */
    public static Bitmap getYoutubeThumbnail(String vId, int num) {
        try {
            URL imageUrl = new URL("http://img.youtube.com/vi/" + vId + "/" + num + ".jpg");
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            byte[] imageRaw = baf.toByteArray();
            Bitmap rtn = BitmapFactory.decodeByteArray(imageRaw, 0, imageRaw.length);
            return rtn;
        } catch (Exception e) {
            Log.d(TAG, "Error: " + e.toString());
        }
        return null;
    }
}
