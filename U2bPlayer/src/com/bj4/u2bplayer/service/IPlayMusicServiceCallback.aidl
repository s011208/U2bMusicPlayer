package  com.bj4.u2bplayer.service;
interface IPlayMusicServiceCallback{
void notifyPlayIndexChanged();
void notifyPlayStateChanged(boolean isPlaying);
}