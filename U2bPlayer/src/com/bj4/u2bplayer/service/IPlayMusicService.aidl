package com.bj4.u2bplayer.service;
import com.bj4.u2bplayer.service.IPlayMusicServiceCallback;
import com.bj4.u2bplayer.utilities.PlayListInfo;
interface IPlayMusicService{
void play(int index);
int playFromLastTime();
void next();
void previous();
void pause();
void resume();
boolean isPlaying();
void registerCallback(IPlayMusicServiceCallback cb);
void unRegisterCallback(IPlayMusicServiceCallback cb);
boolean isInitialized();
PlayListInfo getCurrentPlayInfo();
}