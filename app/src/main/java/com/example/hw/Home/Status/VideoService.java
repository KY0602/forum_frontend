package com.example.hw.Home.Status;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


public class VideoService extends Service {

    private static final String LOG_TAG = VideoService.class.getSimpleName();
    // Media player
    public MediaPlayer mediaPlayer;
    // Used to pause/resume MediaPlayer
    private int resumePosition;

    private VideoBinder mBinder = new VideoBinder();

    public class VideoBinder extends Binder {
        VideoService getService() {
            return VideoService.this;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isPlay = intent.getBooleanExtra("isPlay",true);
        if(isPlay) {
            boolean isPause = intent.getBooleanExtra("isPause", false);
            boolean isRestart = intent.getBooleanExtra("isRestart", false);
            if (isRestart) {
                resumePosition = 0;
                resumeMedia();
            }
            if (isPause) {
                pauseMedia();
            } else {
                playMedia();
            }
        } else {
            resumeMedia();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        // Create player
        Log.d(LOG_TAG, "Media Player created");
        mediaPlayer = new MediaPlayer();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Movies/nevergonna.mp4";
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
        Log.d(LOG_TAG, "Destroying...");
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