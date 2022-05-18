package com.example.hw.Home.Status;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.hw.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageService extends Service{
    private static final String LOG_TAG = ImageService.class.getSimpleName();

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    String downloadUrl, image_name;
    public static boolean serviceState=false;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            downloadFile();
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        serviceState=true;
        HandlerThread thread = new HandlerThread("ServiceStartArguments",1);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG,"onStartCommand");

        String image_type = intent.getStringExtra("image_type");
        image_name = intent.getStringExtra("image_name");
        String image_url;

        if (image_type.equals("profile")) {
            image_url = "http://192.168.1.10:8000/profile-pic/" + image_name;
        } else {
            image_url = "http://192.168.1.10:8000/image/" + image_name;
        }

        this.downloadUrl = image_url;

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"DESTROY");
        serviceState=false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    public void downloadFile(){
        downloadFile(this.downloadUrl, this.image_name);
    }

    public void downloadFile(String fileURL, String fileName) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/download_tmp");
            if (root.exists() && root.isDirectory()) {

            } else {
                Log.d(LOG_TAG, "Directory created");
                root.mkdir();
            }
            Log.d(LOG_TAG, root.getPath());
            Log.d(LOG_TAG, fileURL);
            URL u = new URL(fileURL);
            URLConnection c = u.openConnection();
            c.connect();
            int fileSize = c.getContentLength();
            Log.d(LOG_TAG, String.valueOf(fileSize));

            // Download file
            InputStream input = new BufferedInputStream(u.openStream(), 8192);

            // Output stream
            FileOutputStream output = new FileOutputStream(root + "/" + fileName);

            byte data[] = new byte[1024];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            Toast.makeText(this, "图片下载完成", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("IMAGE-DOWNLOADED");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}
