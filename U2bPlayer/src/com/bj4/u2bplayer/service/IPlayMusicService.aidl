package com.bj4.u2bplayer.service;
import com.bj4.u2bplayer.service.IPlayMusicServiceCallback;
interface IPlayMusicService{
void play(int index);
void next();
void previous();
void pause();
void resume();
boolean isPlaying();
void registerCallback(IPlayMusicServiceCallback cb);
void unRegisterCallback(IPlayMusicServiceCallback cb);
}