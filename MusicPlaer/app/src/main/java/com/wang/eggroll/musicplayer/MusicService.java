package com.wang.eggroll.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eggroll on 04/03/2017.
 */

public class MusicService extends Service {

//    private List<MusicItem> musicItemList = new ArrayList<>();
//    private MusicItemList musicItemList;
    private MusicItem currentMusicItem;
    private String id;
    private String musicName;
    private String musicAlbum;
    private String musicArtist;
    private String musicDuration;
    private long musicDurationBefore;
    private Uri musicUri;
    private Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//    public MediaPlayer mediaPlayer = new MediaPlayer();
//    private boolean isPaused = false;
//    private MyMediaPlayer myMediaPlayer;
//    private boolean isPause = false;
//    private boolean reload = true;
//    private SeekBar seekBar;

    private MusicActivityBroadcastReceiver musicActivityBroadcastReceiver;

//    private Handler handler = new Handler();
//    private Runnable updateThread = new Runnable() {
//        @Override
//        public void run() {
//            seekBar.setProgress(myMediaPlayer.getMediaPlayer().getCurrentPosition());
//            currentTime.setText(convertMSecendToTime(myMediaPlayer.getMediaPlayer().getCurrentPosition()));
//            handler.postDelayed(updateThread, 100);
//        }
//    };
//    private Messenger messenger = new Messenger(handler);
//    private MessengerHandler messengerHandler;
//
//    public MusicService(){
//        messengerHandler = new MessengerHandler();
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        loadMusic();

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser == true){
//                    myMediaPlayer.getMediaPlayer().seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });



        MyMediaPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("itemClick");
        musicActivityBroadcastReceiver = new MusicActivityBroadcastReceiver();
        registerReceiver(musicActivityBroadcastReceiver, intentFilter);

        return new MyBinder();
    }

    public class MyBinder extends Binder{
        public void playMusic(){
            play();
            sendInfo();
        }

        public void playNextMusic(){
            playNext();
            sendInfo();
        }

        public void playPreMusic(){
            playPre();
            sendInfo();
        }

        public void pause(){
            MyMediaPlayer.getMediaPlayer().pause();
//        isPause = true;
//        playBtn.setText("play");
        }

        public void playFromPause(){
            playMusicItem(currentMusicItem, false);
//        isPause = false;
//        playBtn.setText("pause");
        }
    }

    private void sendInfo(){
        Intent intent = new Intent("play");
        intent.putExtra("musicTitle", currentMusicItem.getMusicName());
        intent.putExtra("musicDuration", currentMusicItem.getDuration());
        sendBroadcast(intent);
    }

    private void play() {
        if (currentMusicItem == null && MusicItemList.getMusicItemList().size() > 0) {
            currentMusicItem = MusicItemList.getMusicItemList().get(0);
            playMusicItem(currentMusicItem, true);
//        }else if (currentMusicItem != null && isPause == false){
//            pause();
//        }else{
//            playFromPause();
//        }
        }
    }
//    private class MessengerHandler extends Handler{
//        @Override
//        public void handleMessage(Message msg) {
//
//            if (msg.what == 1){
//                Log.d("Service", "playAction");
//                play();
//            }
//
//            super.handleMessage(msg);
//        }
//    }




    private void loadMusic(){

        ContentResolver contentResolver = getContentResolver();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        if(null != cursor){
            while (cursor.moveToNext()){
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                musicAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                musicArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                musicDurationBefore = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                musicDuration = convertMSecendToTime(musicDurationBefore);

                musicUri = Uri.withAppendedPath(uri, id);


                MusicItem musicItem = new MusicItem(musicName, musicArtist, musicAlbum, musicDuration, musicUri);
                MusicItemList.getMusicItemList().add(musicItem);
            }
            cursor.close();
        }
    }
    private String convertMSecendToTime(long time) {

        SimpleDateFormat mSDF = new SimpleDateFormat("mm:ss");

        Date date = new Date(time);
        String times= mSDF.format(date);

        return times;
    }


    //Code about playing
    private void prepareToPlay(MusicItem item){
        try {
            MyMediaPlayer.getMediaPlayer().reset();
            MyMediaPlayer.getMediaPlayer().setDataSource(MusicService.this, item.musicUri);
            MyMediaPlayer.getMediaPlayer().prepare();
//            isPause = false;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void playMusicItem(MusicItem item, boolean reload){
        if(item == null){
            return;
        }
        if (reload){
            prepareToPlay(item);
            currentMusicItem = item;
//            playBtn.setText("pause");
        }
        MyMediaPlayer.getMediaPlayer().start();
//        totalTime.setText(currentMusicItem.getDuration());
//        seekBar.setMax(myMediaPlayer.getMediaPlayer().getDuration());
//        musicTitle.setText(currentMusicItem.getMusicName());
//        handler.post(updateThread);
    }

    private void playNext(){
        int currentIndex = MusicItemList.getMusicItemList().indexOf(currentMusicItem);

        if(currentIndex == MusicItemList.getMusicItemList().size() - 1){
            currentIndex = 0;
        }else {
            currentIndex++;
        }
        currentMusicItem = MusicItemList.getMusicItemList().get(currentIndex);
        playMusicItem(currentMusicItem, true);
    }

    private void playPre(){
        int currentIndex = MusicItemList.getMusicItemList().indexOf(currentMusicItem);

        if(currentIndex == 0){
            currentIndex = MusicItemList.getMusicItemList().size() - 1;
        }else {
            currentIndex--;
        }
        currentMusicItem = MusicItemList.getMusicItemList().get(currentIndex);
        playMusicItem(currentMusicItem, true);
    }

    private class MusicActivityBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.equals(action, "itemClick")){
                int position = intent.getIntExtra("position", 0);
                currentMusicItem = MusicItemList.getMusicItemList().get(position);
                playMusicItem(currentMusicItem, true);
                sendInfo();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicActivityBroadcastReceiver);
    }
}
