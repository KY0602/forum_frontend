package com.example.hw;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;


public class MusicService extends Service {

    private static final String LOG_TAG = MusicService.class.getSimpleName();
    //media player
    private MediaPlayer mediaPlayer;
    //Used to pause/resume MediaPlayer
    private int resumePosition;

    private MusicBinder mBinder = new MusicBinder();

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    //TODO
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isPlay = intent.getBooleanExtra("isPlay",true);
        if(isPlay) {
            boolean isPause = intent.getBooleanExtra("isPause", false);
            if (isPause) {
                pauseMedia();
            } else {
                playMedia();
            }
        }else {
            resumeMedia();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    // TODO
    public void onCreate(){
        super.onCreate();
        //create player
        mediaPlayer = new MediaPlayer();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/nevergonna.mp3";
        // mediaPlayer = MediaPlayer.create(this,R.raw.wonderwall);

        try {
            mediaPlayer.setDataSource(this, Uri.parse(path));
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    // TODO
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

}