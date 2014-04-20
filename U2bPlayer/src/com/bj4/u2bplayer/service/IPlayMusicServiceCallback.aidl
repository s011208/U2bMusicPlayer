package  com.bj4.u2bplayer.service;
import com.bj4.u2bplayer.utilities.PlayListInfo;
interface IPlayMusicServiceCallback{
void notifyPlayIndexChanged();
void notifyPlayStateChanged(boolean isPlaying);
void notifyPlayInfoChanged(out PlayListInfo info);
void updateBufferingPercentage(out PlayListInfo info, int percentage);
void updatePlayingTime(int time);
}