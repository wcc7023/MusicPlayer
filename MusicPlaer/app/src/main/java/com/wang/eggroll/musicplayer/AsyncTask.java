//package com.wang.eggroll.musicplayer;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.MediaStore;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by eggroll on 26/02/2017.
// */
//
//public class AsyncTask extends android.os.AsyncTask<String, Float, Float> {
//    private Context context;
//    private List<MusicItem> musicItemList;
//    private String id;
//    private String musicName;
//    private String musicAlbum;
//    private String musicArtist;
//    private String musicDuration;
//    private long musicDurationBefore;
//    private Uri musicUri;
//    private Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//
//    public AsyncTask(Context context, List<MusicItem> musicItemList) {
//        this.context = context;
//        this.musicItemList = musicItemList;
//    }
//
//    @Override
//    protected Float doInBackground(String... params) {
//        loadMusic();
//        return null;
//    }
//
//    private void loadMusic(){
//
//        ContentResolver contentResolver = context.getContentResolver();
//
//        String[] projection = {
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.DURATION
//        };
//
//        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//
//        if(null != cursor){
//            while (cursor.moveToNext()){
//                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
//                musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                musicAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                musicArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                musicDurationBefore = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//
//                musicDuration = convertMSecendToTime(musicDurationBefore);
//
//                musicUri = Uri.withAppendedPath(uri, id);
//
//
//                MusicItem musicItem = new MusicItem(musicName, musicArtist, musicAlbum, musicDuration, musicUri);
//                musicItemList.add(musicItem);
//            }
//            cursor.close();
//        }
//    }
//
//    private String convertMSecendToTime(long time) {
//
//        SimpleDateFormat mSDF = new SimpleDateFormat("mm:ss");
//
//        Date date = new Date(time);
//        String times= mSDF.format(date);
//
//        return times;
//    }
//}
