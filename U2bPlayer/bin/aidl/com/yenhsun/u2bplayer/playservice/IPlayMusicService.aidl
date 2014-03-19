package  com.yenhsun.u2bplayer.playservice;
interface IPlayMusicService{
void play(int index);
void next();
void previous();
void pause();
void resume();
boolean isPlaying();
}