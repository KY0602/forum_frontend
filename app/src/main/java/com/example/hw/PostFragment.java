package com.example.hw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PostFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private EditText postTitle, postMsg;
    private ImageButton postButton;
    private AppCompatActivity activity;

    public PostFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v  = inflater.inflate(R.layout.fragment_post, container, false);

        postTitle = (EditText)v.findViewById(R.id.postTitle);
        postMsg = (EditText)v.findViewById(R.id.postMsg);
        postButton = (ImageButton)v.findViewById(R.id.postButton);
        postButton.setOnClickListener(this);

        activity = (AppCompatActivity)v.getContext();

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
        String title = postTitle.getText().toString();
        String msg = postMsg.getText().toString();
        if (title.isEmpty() || msg.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "动态标题或内容不能为空", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "发布成功", Toast.LENGTH_LONG).show();
            postTitle.getText().clear();
            postMsg.getText().clear();

            switchContent(title, msg);
        }
    }

    private void switchContent(String title, String msg) {
        if (activity == null) {
            return;
        }
        else if (activity instanceof MainActivity) {
            Log.d(LOG_TAG, "switch");
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.switchHome(title, msg);
        }
    }
}