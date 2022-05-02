package com.example.hw.Post;

import android.content.SharedPreferences;
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

import com.example.hw.MainActivity;
import com.example.hw.R;

public class PostFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private EditText postTitle, postMsg;
    private ImageButton postButton;
    private AppCompatActivity activity;
    private SharedPreferences pref;

    public PostFragment(){
        // require a empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Destroy");
        saveDraft();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Pause");
        saveDraft();
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
        pref = v.getContext().getSharedPreferences("Draft", 0);
        loadDraft();

        return v;
    }

    public void saveDraft() {
        SharedPreferences.Editor editor = pref.edit();
        String title = postTitle.getText().toString();
        String msg = postMsg.getText().toString();
        editor.putString("TITLE", title);
        editor.putString("MESSAGE", msg);
        editor.commit();
//        Toast.makeText(getActivity().getApplicationContext(), "草稿保存成功", Toast.LENGTH_SHORT).show();
    }

    public void loadDraft() {
        if (pref.contains("TITLE") || pref.contains("MESSAGE")) {
//            Toast.makeText(getActivity().getApplicationContext(), "草稿加载成功", Toast.LENGTH_SHORT).show();
            if (pref.contains("TITLE")) {
                postTitle.setText(pref.getString("TITLE", ""));
            }
            if (pref.contains("MESSAGE")) {
                postMsg.setText(pref.getString("MESSAGE", ""));
            }
        }
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

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();

            switchContent(title, msg);
        }
    }

    private void switchContent(String title, String msg) {
        if (activity == null) {
            return;
        }
        else if (activity instanceof MainActivity) {
            Log.d(LOG_TAG, "Switch");
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.switchHome(title, msg);
        }
    }
}