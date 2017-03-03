package com.wang.eggroll.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<MusicItem> musicItemList = new ArrayList<>();
    private String id;
    private String musicName;
    private String musicAlbum;
    private String musicArtist;
    private String musicDuration;
    private long musicDurationBefore;
    private Uri musicUri;
    private Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public MediaPlayer mediaPlayer = new MediaPlayer();
//    private boolean isPaused = false;
    private MusicItem currentMusicItem;
    private SeekBar seekBar;
    private TextView totalTime;
    private TextView currentTime;
    private TextView musicTitle;

    private Button playBtn;
    private Button playNextBtn;
    private Button playPreBtn;

    private boolean isPause = false;
    private boolean reload = true;

    private Handler handler = new Handler();
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            currentTime.setText(convertMSecendToTime(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(updateThread, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        totalTime = (TextView) findViewById(R.id.total_time);
        currentTime = (TextView) findViewById(R.id.current_time);

        musicTitle = (TextView) findViewById(R.id.music_title);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusicItem(musicItemList.get(position), true);
            }
        });

        loadMusic();

        Adapter adapter = new Adapter(musicItemList);
        listView.setAdapter(adapter);

        playBtn = (Button) findViewById(R.id.play_btn);
        playNextBtn = (Button) findViewById(R.id.next_btn);
        playPreBtn = (Button) findViewById(R.id.previous_btn);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        playNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        playPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPre();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser == true){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Code about playing
    private void prepareToPlay(MusicItem item){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(MainActivity.this, item.musicUri);
            mediaPlayer.prepare();
            isPause = false;
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
            playBtn.setText("pause");
        }
        mediaPlayer.start();
        totalTime.setText(currentMusicItem.getDuration());
        seekBar.setMax(mediaPlayer.getDuration());
        musicTitle.setText(currentMusicItem.getMusicName());
        handler.post(updateThread);
//        isPause = true;
//        isPaused = false;
    }

    private void play(){
        if(currentMusicItem == null && musicItemList.size() > 0 && isPause == false){
            currentMusicItem = musicItemList.get(0);
            playMusicItem(currentMusicItem, true);
        }else if (currentMusicItem != null && isPause == false){
            pause();
        }else{
            playFromPause();
        }
    }

    private void pause(){
        mediaPlayer.pause();
        isPause = true;
        playBtn.setText("play");
    }

    private void playFromPause(){
        playMusicItem(currentMusicItem, false);
        isPause = false;
        playBtn.setText("pause");
    }

    private void playNext(){
        int currentIndex = musicItemList.indexOf(currentMusicItem);

        if(currentIndex == musicItemList.size() - 1){
            currentIndex = 0;
        }else {
            currentIndex++;
        }
        currentMusicItem = musicItemList.get(currentIndex);
        playMusicItem(currentMusicItem, true);
    }

    private void playPre(){
        int currentIndex = musicItemList.indexOf(currentMusicItem);

        if(currentIndex == 0){
            currentIndex = musicItemList.size() - 1;
        }else {
            currentIndex--;
        }
        currentMusicItem = musicItemList.get(currentIndex);
        playMusicItem(currentMusicItem, true);
    }

    private String convertMSecendToTime(long time) {

        SimpleDateFormat mSDF = new SimpleDateFormat("mm:ss");

        Date date = new Date(time);
        String times= mSDF.format(date);

        return times;
    }


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
                musicItemList.add(musicItem);
            }
            cursor.close();
        }
    }

}
