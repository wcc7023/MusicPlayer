package com.wang.eggroll.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list_view);
        loadMusic();

        Adapter adapter = new Adapter(musicItemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicItem musicItem = musicItemList.get(position);
                MediaPlayer mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(MainActivity.this, musicItem.musicUri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
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

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

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

    private String convertMSecendToTime(long time) {

        SimpleDateFormat mSDF = new SimpleDateFormat("mm:ss");

        Date date = new Date(time);
        String times= mSDF.format(date);

        return times;
    }
}
