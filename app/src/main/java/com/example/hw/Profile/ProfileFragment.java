package com.example.hw.Profile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.hw.Home.Status.ImageService;
import com.example.hw.MainActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

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
    TextView username, email, description, following_list;
    ImageView profile_pic;
    Button editProfileButton, changePwButton;
    String user_id, profile_pic_user, username_user, desc_user;
    static final int EDIT_USER_INFO = 400, CHANGE_USER_PW=500;

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

        following_list = v.findViewById(R.id.followingListTxt);
        following_list.setClickable(true);
        following_list.setOnClickListener(this);

        editProfileButton = v.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        changePwButton = v.findViewById(R.id.changePwButton);
        changePwButton.setOnClickListener(this);

        String jsonStr = "{\"user_id\":\""+ user_id + "\"}";
        String requestUrl = getResources().getString(R.string.backend_url) + "query-userinfo";

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
                            username_user = jObject.getString("username");
                            String email_user = jObject.getString("email");
                            desc_user = jObject.getString("description");
                            profile_pic_user = jObject.getString("profile_photo");
                            Log.d(LOG_TAG, profile_pic_user);


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    username.setText(username_user);
                                    email.setText(email_user);

                                    if (!desc_user.equals("null")) description.setText(desc_user);

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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.editProfileButton:
                Log.d(LOG_TAG, "Edit");

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);

                Bundle extras = new Bundle();
                extras.putString("user_id", this.user_id);
                extras.putString("username", this.username_user);
                extras.putString("description", this.desc_user);
                extras.putString("profile_pic", this.profile_pic_user);

                intent.putExtras(extras);
                startActivityForResult(intent, EDIT_USER_INFO);
                break;
            case R.id.changePwButton:
                Log.d(LOG_TAG, "Change PW");

                Intent intent_change_pw = new Intent(getActivity(), ChangePasswordActivity.class);
                intent_change_pw.putExtra("user_id", this.user_id);
                startActivityForResult(intent_change_pw, CHANGE_USER_PW);
                break;
            case R.id.followingListTxt:
                Log.d(LOG_TAG, "Following list");

                Intent intent_following = new Intent(getActivity(), FollowingListActivity.class);
                intent_following.putExtra("user_id_self", this.user_id);
                intent_following.putExtra("user_id_other", this.user_id);
                startActivity(intent_following);
            default:
                Log.d(LOG_TAG, "No match");
                break;
        }
    }

    // After receiving updated user info from EditProfileActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case EDIT_USER_INFO:
                    Bundle extras = data.getExtras();

                    username_user = extras.getString("username");
                    desc_user = extras.getString("description");
                    profile_pic_user = extras.getString("profile_pic");
                    Log.d(LOG_TAG, profile_pic_user);

                    username.setText(username_user);
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
                    break;
                case CHANGE_USER_PW:
                    Log.d(LOG_TAG, "changed successfully");
                    break;
                default:
                    return;
            }
        }
    }
}