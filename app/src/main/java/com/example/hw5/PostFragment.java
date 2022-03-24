package com.example.hw5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import androidx.fragment.app.Fragment;

public class PostFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private EditText postTxt;
    private TextView postOut;
    private ImageButton postButton;

    public PostFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v  = inflater.inflate(R.layout.fragment_post, container, false);

        postTxt = (EditText)v.findViewById(R.id.postTxt);
        postOut = (TextView)v.findViewById(R.id.postOut);
        postButton = (ImageButton)v.findViewById(R.id.postButton);
        postButton.setOnClickListener(this);

        return v;
    }

    public void onClick(final View v) {
        switch(v.getId()) {
            case R.id.postButton:
                Log.d(LOG_TAG, "Post");
                clickPost();
                break;
            default:
                Log.d(LOG_TAG, "No match");
                break;
        }
    }

    private void clickPost() {
        String text = postTxt.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "未输入状态内容", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "发布成功", Toast.LENGTH_LONG).show();
            postTxt.getText().clear();
            postOut.setText(text);
        }
    }
}