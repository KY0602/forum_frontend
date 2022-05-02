package com.example.hw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class StatusActivity extends AppCompatActivity {
    private static final String LOG_TAG = StatusActivity.class.getSimpleName();
    private TextView titleView, msgView, urlText, mapText;
    private ImageButton shareButton, backButton;
    private ImageView imageView;

    // For audio
    private Button startButton, pauseButton;
    boolean isPlay = false;
    boolean isPause = false;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("getTitle", titleView.getText().toString());
        outState.putString("getMessage", msgView.getText().toString());
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "IMAGE-DOWNLOADED".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("IMAGE-DOWNLOADED"));
        super.onResume();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "IMAGE-DOWNLOADED" is broadcast.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Image downloaded");
            File imgFile = new File("/storage/emulated/0/Pictures/download_tmp/tmp.png");
            imageView.setImageURI(Uri.fromFile(imgFile));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String type = extras.getString("EXTRA_TYPE");
        String title = extras.getString("EXTRA_TITLE");
        String msg = extras.getString("EXTRA_MESSAGE");
        if (type.equals("MUSIC")) {
            Log.d(LOG_TAG, "Music");
            setContentView(R.layout.activity_status_music);
            startButton = findViewById(R.id.start);
            startButton.setOnClickListener(this::startPlayer);

            pauseButton = findViewById(R.id.pause);
            pauseButton.setOnClickListener(this::pausePlayer);
        } else if (type.equals("VIDEO")) {
            Log.d(LOG_TAG, "Video");
            setContentView(R.layout.activity_status_video);
            MainActivity.verifyStoragePermissions(this);

            VideoView videoView = findViewById(R.id.videoView);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Movies/nevergonna.mp4");
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
        }
        else {
            setContentView(R.layout.activity_status);
            imageView = findViewById(R.id.image);

            File imgFile = new File(getResources().getString(R.string.image_loc));
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            } else {
                Intent imgIntent = new Intent(getBaseContext(), ImageService.class);
                startService(imgIntent);
            }
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

    public void startPlayer(View view) {
        Log.d(LOG_TAG, "start");
        MainActivity.verifyStoragePermissions(this);
        Intent intent = new Intent(getBaseContext(), MusicService.class);
        if(!isPlay) {
            intent.putExtra("isPlay",true);
            startService(intent);
        }else{
            stopService(intent);
        }
        isPlay = !isPlay;

    }

    public void pausePlayer(View view) {
        Log.d(LOG_TAG, "pause");
        MainActivity.verifyStoragePermissions(this);
        Intent intent = new Intent(getBaseContext(), MusicService.class);
        if(!isPause){
            intent.putExtra("isPlay",true);
            intent.putExtra("isPause",true);
            startService(intent);
        }else{
            intent.putExtra("isPause",false);
            startService(intent);
        }
        isPause = !isPause;
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