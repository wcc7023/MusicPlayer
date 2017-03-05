package com.wang.eggroll.musicplayer;

import android.media.MediaPlayer;

/**
 * Created by eggroll on 04/03/2017.
 */

public class MyMediaPlayer {

    private static MediaPlayer mediaPlayer;
    public static MediaPlayer getMediaPlayer(){
        if(mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
        }
        return mediaPlayer;
    }
    private MyMediaPlayer(){}
}
