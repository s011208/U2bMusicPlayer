
package com.yenhsun.u2bplayer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends YouTubeBaseActivity implements
        YoutubeIdParser.YoutubeIdParserResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YoutubeIdParser.showYoutubeResult(new String[] {
                "五月天", "入陣曲"
        }, this);
    }

    @Override
    public void setResult(ArrayList<String> idList) {
        for (String id : idList) {
            Log.e("QQQQ", id);
        }
    }

}
