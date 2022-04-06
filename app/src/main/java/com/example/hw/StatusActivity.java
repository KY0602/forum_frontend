package com.example.hw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {
    private static final String LOG_TAG = StatusActivity.class.getSimpleName();
    private TextView titleView, msgView, urlText, mapText;
    private ImageButton shareButton, backButton;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String title = extras.getString("EXTRA_TITLE");
        String msg = extras.getString("EXTRA_MESSAGE");
        titleView = findViewById(R.id.titleView2);
        titleView.setText(title);

        msgView = findViewById(R.id.msgView);
        msgView.setText(msg);

        urlText = findViewById(R.id.url);
        urlText.setText(getResources().getText(R.string.text_url));
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