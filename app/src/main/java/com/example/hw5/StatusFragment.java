package com.example.hw5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

public class StatusFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = StatusFragment.class.getSimpleName();
    private TextView titleView, msgView, urlText, mapText;
    private ImageButton shareButton;

    public StatusFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        Bundle extras = this.getArguments();
        if (extras != null) {
            String title = extras.getString("EXTRA_TITLE");
            String msg = extras.getString("EXTRA_MESSAGE");
            Log.d(LOG_TAG, title);
            Log.d(LOG_TAG, msg);

            titleView = (TextView)v.findViewById(R.id.titleView2);
            titleView.setText(title);

            msgView = (TextView)v.findViewById(R.id.msgView);
            msgView.setText(msg);

            urlText = (TextView)v.findViewById(R.id.url);
            urlText.setText(getResources().getText(R.string.text_url));
            urlText.setOnClickListener(this);

            mapText = (TextView)v.findViewById(R.id.map);
            mapText.setText(getResources().getText(R.string.text_map));
            mapText.setOnClickListener(this);

            shareButton = (ImageButton)v.findViewById(R.id.share_button);
            shareButton.setOnClickListener(this);
        }

        return v;
    }
    
    public void onClick(final View v) {
        switch(v.getId()) {
            case R.id.url:
                Log.d(LOG_TAG, "URL");
                clickUrl();
                break;
            case R.id.map:
                Log.d(LOG_TAG, "Map");
                clickMap();
                break;
            case R.id.share_button:
                Log.d(LOG_TAG, "Share");
                shareText();
                break;
            default:
                Log.d(LOG_TAG, "Mo match");
                break;
        }
    }

    private void clickUrl() {
        // Get the URL text.
        String url = urlText.getText().toString();
        Log.d(LOG_TAG, url);

        // Parse the URI and create the intent.
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this!");
        }
    }

    private void clickMap() {
        // Get the string indicating a location. Input is not validated; it is
        // passed to the location handler intact.
        String loc = mapText.getText().toString();
        Log.d(LOG_TAG, loc);

        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);

        startActivity(intent);
    }

    public void shareText() {
        String txt = msgView.getText().toString();
        Log.d(LOG_TAG, txt);
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(getActivity())
                .setType(mimeType)
                .setChooserTitle(R.string.share_text_with)
                .setText(txt)
                .startChooser();
    }
}