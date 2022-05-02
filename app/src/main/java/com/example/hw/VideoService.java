package com.example.hw;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;


public class VideoService extends Service {

    private static final String LOG_TAG = VideoService.class.getSimpleName();
    //media player
    private MediaPlayer mediaPlayer;
    //Used to pause/resume MediaPlayer
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
    //TODO
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //create player
        mediaPlayer = new MediaPlayer();
    }

}