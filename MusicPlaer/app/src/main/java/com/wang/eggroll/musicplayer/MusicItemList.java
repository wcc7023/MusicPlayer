package com.wang.eggroll.musicplayer;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eggroll on 04/03/2017.
 */

public class MusicItemList {

    private static volatile List<MusicItem> musicItemList;
    //volatile确保对于该对象的所有操作都是按顺序执行的，防止new对象的时候出现语句重排，使另一线程访问到没有被初始化，
    // 但是已经被分配空间的对象
    //java初始化一个对象的步骤是，分配对象内存空间-->初始化对象-->设置对象名指向的内存空间
//    private static List<MusicItem> musicItemList = new ArrayList<>();

    public static List<MusicItem> getMusicItemList(){
        if(musicItemList == null){
            //两次判断，防止两个线程同时通过第一次判断后，创建两个对象
            synchronized (MusicItemList.class){
                if (musicItemList == null){
                    musicItemList = new ArrayList<>();
                }
            }
        }
//        if (musicItemList == null){
//            musicItemList = new ArrayList<>();
//        }
        return musicItemList;
    }

    private MusicItemList(){}
}
