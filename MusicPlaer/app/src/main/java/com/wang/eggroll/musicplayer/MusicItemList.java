package com.wang.eggroll.musicplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eggroll on 04/03/2017.
 */

public class MusicItemList {

    private static List<MusicItem> musicItemList = new ArrayList<>();

    public static List<MusicItem> getMusicItemList(){
//        if (musicItemList == null){
//            musicItemList = new ArrayList<>();
//        }
        return musicItemList;
    }

    private MusicItemList(){}
}
