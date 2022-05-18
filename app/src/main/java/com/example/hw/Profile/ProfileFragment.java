package com.example.hw.Profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.hw.Home.Status.ImageService;
import com.example.hw.LoginActivity;
import com.example.hw.MainActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = ProfileFragment.class.getSimpleName();
    private AppCompatActivity activity;
    TextView username, email, description;
    ImageView profile_pic;
    String user_id, profile_pic_user;

    public ProfileFragment(){
        // require a empty public constructor
    }

    // Resume时register receiver，会捕捉到"IMAGE-DOWNLOADED"的broadcast，用以图片下载完成时通知
    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "IMAGE-DOWNLOADED".
        LocalBroadcastManager.getInstance(activity).registerReceiver(
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
            File imgFile = new File(getResources().getString(R.string.image_loc) + profile_pic_user);
            profile_pic.setImageURI(Uri.fromFile(imgFile));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get user_id from MainActivity
        activity = (AppCompatActivity)v.getContext();
        MainActivity mainActivity = (MainActivity) activity;
        user_id = mainActivity.user_id;

        username = v.findViewById(R.id.username);
        email = v.findViewById(R.id.email);
        description = v.findViewById(R.id.profileDesc);
        profile_pic = v.findViewById(R.id.profilePic);

        String jsonStr = "{\"user_id\":\""+ user_id + "\"}";
        String requestUrl = "http://192.168.1.10:8000/query-userinfo";

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
                        boolean status = jObject.getBoolean("status");
                        if (status) {
                            String username_user = jObject.getString("username");
                            String email_user = jObject.getString("email");
                            String desc_user = jObject.getString("description");
                            profile_pic_user = jObject.getString("profile_photo");
                            Log.d(LOG_TAG, profile_pic_user);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    username.setText(username_user);
                                    email.setText(email_user);
                                    description.setText(desc_user);

                                    // Download image, start ImageService to download pic if image does not already exist
                                    if (!profile_pic_user.equals("null")) {
                                        File imgFile = new File(getResources().getString(R.string.image_loc) + profile_pic_user);
                                        if (imgFile.exists()) {
                                            profile_pic.setImageURI(Uri.fromFile(imgFile));
                                        } else {
                                            Intent imgIntent = new Intent(activity, ImageService.class);
                                            imgIntent.putExtra("image_type", "profile");
                                            imgIntent.putExtra("image_name", profile_pic_user);
                                            activity.startService(imgIntent);
                                        }
                                    }

                                    Toast.makeText(v.getContext(), "获取成功", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(v.getContext(), "获取失败", Toast.LENGTH_LONG).show();
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

        return v;
    }

    @Override
    public void onClick(View view) {
        return;
    }
}