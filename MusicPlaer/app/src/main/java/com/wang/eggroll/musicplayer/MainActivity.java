package com.wang.eggroll.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private SeekBar seekBar;


    private Button playBtn;
    private Button playNextBtn;
    private Button playPreBtn;

    private TextView totalTime;
    private TextView currentTime;
    private TextView musicTitle;


//    private Messenger messenger;

    private MusicServiceBroadcastReceiver musicServiceBroadcastReceiver;

    boolean isPaused = false;
    boolean firstPlay = true;

    private Handler handler = new Handler();
    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(MyMediaPlayer.getMediaPlayer().getCurrentPosition());
            currentTime.setText(convertMSecendToTime(MyMediaPlayer.getMediaPlayer().getCurrentPosition()));
            handler.postDelayed(updateSeekbar, 100);
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = (Button) findViewById(R.id.play_btn);
        playNextBtn = (Button) findViewById(R.id.next_btn);
        playPreBtn = (Button) findViewById(R.id.previous_btn);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser == true){
                    MyMediaPlayer.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("play");
        intentFilter.addAction("init");
        musicServiceBroadcastReceiver = new MusicServiceBroadcastReceiver();
        registerReceiver(musicServiceBroadcastReceiver, intentFilter);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        Adapter adapter = new Adapter(MusicItemList.getMusicItemList());
        listView.setAdapter(adapter);


        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                final MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firstPlay == true && isPaused ==false){
                        myBinder.playMusic();
                            firstPlay = false;
                            playBtn.setText("pause");
                            initSeekbar();
                        }else if(firstPlay == false && isPaused ==false){
                            myBinder.pause();
                            isPaused = true;
                            playBtn.setText("play");
                        }else if(firstPlay == false && isPaused == true){
                            myBinder.playFromPause();
                            isPaused = false;
                            playBtn.setText("pause");
                        }
                    }
                });

                playNextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myBinder.playNextMusic();
                        firstPlay = false;
                        playBtn.setText("pause");
                        initSeekbar();
                    }
                });

                playPreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myBinder.playPreMusic();
                        firstPlay = false;
                        playBtn.setText("pause");
                        initSeekbar();
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myBinder.playSelected(position);
                        firstPlay = false;
                        playBtn.setText("pause");
                        initSeekbar();
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        final Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE | BIND_ABOVE_CLIENT);



//        playBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                play();
//            }
//        });

        totalTime = (TextView) findViewById(R.id.total_time);
        currentTime = (TextView) findViewById(R.id.current_time);
        musicTitle = (TextView) findViewById(R.id.music_title);





//        playNextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNext();
//            }
//        });
//
//        playPreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPre();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicServiceBroadcastReceiver);
    }

    private void initSeekbar(){
        seekBar.setMax(MyMediaPlayer.getMediaPlayer().getDuration());
        handler.post(updateSeekbar);
    }


    private String convertMSecendToTime(long time) {

        SimpleDateFormat mSDF = new SimpleDateFormat("mm:ss");

        Date date = new Date(time);
        String times= mSDF.format(date);

        return times;
    }

    public class MusicServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String musicTittleString;
            String totalTimeString;
            if(TextUtils.equals(action, "play")){
                musicTittleString =  intent.getStringExtra("musicTitle");
                totalTimeString = intent.getStringExtra("musicDuration");
                musicTitle.setText(musicTittleString);
                totalTime.setText(totalTimeString);
            }else if(TextUtils.equals(action, "init")){
                initSeekbar();
            }
        }
    }

}
