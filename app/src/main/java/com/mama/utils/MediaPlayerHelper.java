package com.mama.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerHelper {
    private static MediaPlayerHelper instance;
    private MediaPlayer mediaPlayer;

    private MediaPlayerHelper() {
        // 私有构造函数
    }

    public static synchronized MediaPlayerHelper getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHelper();
        }
        return instance;
    }

    public void playRawAudio(Context context, int rawResourceId) {
        if (isPlaying()) {
            // 如果正在播放音乐，则不执行播放操作
            return;
        }

        stopAudio();

        mediaPlayer = MediaPlayer.create(context, rawResourceId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        mediaPlayer.start();
    }

    public void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
