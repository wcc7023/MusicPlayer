package com.wang.eggroll.musicplayer;

import android.net.Uri;

/**
 * Created by eggroll on 17/02/2017.
 */

public class MusicItem {

    String musicName;
    String musicArtist;
    String musicAlbum;
    String duration;
    Uri musicUri;

    public MusicItem(String musicName, String musicArtist, String musicAlbum, String duration, Uri musicUri) {
        this.musicName = musicName;
        this.musicArtist = musicArtist;
        this.musicAlbum = musicAlbum;
        this.duration = duration;
        this.musicUri = musicUri;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public String getDuration() {
        return duration;
    }

    public Uri getMusicUri() {
        return musicUri;
    }
}
