package com.example.hw.Home.Status;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


public class MusicService extends Service {

    private static final String LOG_TAG = MusicService.class.getSimpleName();
    // Media player
    private MediaPlayer mediaPlayer;
    // Used to pause/resume MediaPlayer
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
    public void onCreate(){
        super.onCreate();
        // Create player
        mediaPlayer = new MediaPlayer();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/nevergonna.mp3";

        try {
            mediaPlayer.setDataSource(this, Uri.parse(path));
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
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