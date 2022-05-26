package com.example.hw.Home.Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hw.MainActivity;
import com.example.hw.Profile.OtherUserProfileActivity;
import com.example.hw.Profile.PersonalPageActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StatusActivity extends AppCompatActivity {
    private static final String LOG_TAG = StatusActivity.class.getSimpleName();
    private TextView titleView, msgView, urlText, mapText;
    private ImageButton shareButton, backButton;
    private ImageView imageView;
    private Button startButton, pauseButton;
    private String user_id, media_image, status_id;

    // For audio
    boolean isPlay_audio = false;
    boolean isPause_audio = false;

    // For video
    public SurfaceView surfaceView;
    public SurfaceHolder holder;
    private VideoService videoService;
    private boolean bounded = false;
    boolean isPlay_video = false;
    boolean isPause_video = false;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    // 保存状态，实际不需要
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("getTitle", titleView.getText().toString());
        outState.putString("getMessage", msgView.getText().toString());
    }

    // Pause时unregister receiver，用以图片下载完成时通知
    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    // Resume时register receiver，会捕捉到"IMAGE-DOWNLOADED"的broadcast，用以图片下载完成时通知
    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "IMAGE-DOWNLOADED".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("IMAGE-DOWNLOADED"));
        super.onResume();
    }

    // 图片下载完成时，将本地存储的图片添加到imageView中
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "IMAGE-DOWNLOADED" is broadcast.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Image downloaded");
            File imgFile = new File(getResources().getString(R.string.image_loc) + media_image);
            imageView.setImageURI(Uri.fromFile(imgFile));
        }
    };

    // 用以视频播放的service
    // To get an instance of VideoService
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VideoService.VideoBinder videoBinder = (VideoService.VideoBinder) iBinder;
            videoService = videoBinder.getService();
            bounded = true;
            Log.d(LOG_TAG, "Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(LOG_TAG, "Service disconnected");
            bounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        status_id = extras.getString("status_id");
        user_id = extras.getString("user_id");

        String type = extras.getString("EXTRA_TYPE");
        String title = extras.getString("EXTRA_TITLE");
        String msg = extras.getString("EXTRA_TEXT");

        // 通过intent传入的type判断需要执行什么
        // 3 Types of contents
        if (type.equals("AUDIO")) {
            Log.d(LOG_TAG, "Music");
            setContentView(R.layout.activity_status_music);
            startButton = findViewById(R.id.start);
            startButton.setOnClickListener(this::startMusicPlayer);

            pauseButton = findViewById(R.id.pause);
            pauseButton.setOnClickListener(this::pauseMusicPlayer);
        } else if (type.equals("VIDEO")) {
            Log.d(LOG_TAG, "Video");
            setContentView(R.layout.activity_status_video);
            MainActivity.verifyStoragePermissions(this);

            surfaceView = findViewById(R.id.surfaceView);
            holder = surfaceView.getHolder();

            Intent videoIntent = new Intent(this, VideoService.class);
            bindService(videoIntent, serviceConnection, Context.BIND_AUTO_CREATE);

            startButton = findViewById(R.id.start);
            startButton.setOnClickListener(this::startVideoPlayer);

            pauseButton = findViewById(R.id.pause);
            pauseButton.setOnClickListener(this::pauseVideoPlayer);
        } else {
            setContentView(R.layout.activity_status);
            imageView = findViewById(R.id.image);
            Log.d(LOG_TAG, status_id);

            getStatusInfoImage();
        }
        titleView = findViewById(R.id.titleView2);
        titleView.setText(title);

        msgView = findViewById(R.id.msgView);
        msgView.setText(msg);

        urlText = findViewById(R.id.url);
        urlText.setText(getResources().getString(R.string.text_url));
        urlText.setOnClickListener(this::clickURL);

        mapText = findViewById(R.id.map);
        mapText.setText(getResources().getText(R.string.text_map));
        mapText.setOnClickListener(this::clickMap);

        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(this::shareText);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::goBack);

        if (savedInstanceState != null) {
            titleView.setText(savedInstanceState.getString("getTitle"));
            msgView.setText(savedInstanceState.getString("getMessage"));
        }
    }

    private void getStatusInfoImage() {
        String jsonStr = "{\"status_id\":\""+ status_id + "\"}";
        String requestUrl = getResources().getString(R.string.backend_url) + "query-status";

        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，

            @SuppressWarnings("deprecation") RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {}
                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String responseStr = response.body().string();
                    try {
                        JSONObject jObject = new JSONObject(responseStr);
                        boolean query_status = jObject.getBoolean("status");
                        if (query_status) {
                            media_image = jObject.getString("media");
                            StatusActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    File imgFile = new File(getResources().getString(R.string.image_loc) + media_image);
                                    // If image does not exist, start a service to download
                                    if (imgFile.exists()) {
                                        imageView.setImageURI(Uri.fromFile(imgFile));
                                    } else {
                                        Intent imgIntent = new Intent(getBaseContext(), ImageService.class);
                                        imgIntent.putExtra("image_type", "status");
                                        imgIntent.putExtra("image_name", media_image);
                                        startService(imgIntent);
                                    }
                                }
                            });
                        } else {
                            StatusActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startMusicPlayer(View view) {
        MainActivity.verifyStoragePermissions(this);
        Intent intent = new Intent(getBaseContext(), MusicService.class);
        if (!isPlay_audio) {
            intent.putExtra("isPlay",true);
            startService(intent);
        } else {
            stopService(intent);
        }
        isPlay_audio = !isPlay_audio;
    }

    public void pauseMusicPlayer(View view) {
        MainActivity.verifyStoragePermissions(this);
        Intent intent = new Intent(getBaseContext(), MusicService.class);
        if (!isPause_audio) {
            intent.putExtra("isPlay",true);
            intent.putExtra("isPause",true);
            startService(intent);
        } else {
            intent.putExtra("isPause",false);
            startService(intent);
        }
        isPause_audio = !isPause_audio;
    }

    public void startVideoPlayer(View view) {
        Intent videoIntent = new Intent(this, VideoService.class);
        if (bounded) {
            videoService.mediaPlayer.setDisplay(holder);
            if (!isPlay_video) {
                videoIntent.putExtra("isPlay",true);
                videoIntent.putExtra("isRestart", true);
                startService(videoIntent);
            } else {
                videoIntent.putExtra("isPlay",true);
                videoIntent.putExtra("isPause",true);
                startService(videoIntent);
            }
            isPlay_video = !isPlay_video;
        }
    }

    public void pauseVideoPlayer(View view) {
        Intent videoIntent = new Intent(this, VideoService.class);
        if (bounded) {
            videoService.mediaPlayer.setDisplay(holder);
            if (!isPause_video) {
                videoIntent.putExtra("isPlay",true);
                videoIntent.putExtra("isPause",true);
                startService(videoIntent);
            } else {
                videoIntent.putExtra("isPause",false);
                startService(videoIntent);
            }
            isPause_video = !isPause_video;
        }
    }

    public void goBack(View view) {
        finish();
        super.onBackPressed();
    }

    public void clickURL(View view) {
        // Get the URL text.
        String url = urlText.getText().toString();
        Log.d(LOG_TAG, url);

        // Parse the URI and create the intent.
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this!");
        }
    }

    public void clickMap(View view) {
        // Get the string indicating a location. Input is not validated; it is
        // passed to the location handler intact.
        String loc = mapText.getText().toString();
        Log.d(LOG_TAG, loc);

        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);

        startActivity(intent);
    }

    public void shareText(View view) {
        String txt = msgView.getText().toString();
        Log.d(LOG_TAG, txt);
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(R.string.share_text_with)
                .setText(txt)
                .startChooser();
    }
}