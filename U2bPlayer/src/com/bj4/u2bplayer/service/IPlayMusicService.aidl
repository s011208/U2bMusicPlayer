package  com.bj4.u2bplayer.service;
interface IPlayMusicService{
void play(int index);
void next();
void previous();
void pause();
void resume();
boolean isPlaying();
}