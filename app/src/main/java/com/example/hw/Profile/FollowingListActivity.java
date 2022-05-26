package com.example.hw.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hw.Home.Status.ImageService;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class User {
    private String user_id, username;

    public User(String user_id, String username) {
        this.user_id = user_id;
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }

    public String getUser_id() { return user_id; }
}

public class FollowingListActivity extends AppCompatActivity {
    private static final String LOG_TAG = FollowingListActivity.class.getSimpleName();
    private String user_id_self, user_id_other;
    ListView followingList;
    ArrayList<User> following = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        user_id_self = intent.getStringExtra("user_id_self");
        user_id_other = intent.getStringExtra("user_id_other");

        getFollowingList();

        setContentView(R.layout.activity_following_list);
        followingList = findViewById(R.id.following_list_view);
        ArrayAdapter<User> arr;
        arr = new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, following);
        followingList.setAdapter(arr);
        followingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), OtherUserProfileActivity.class);
                String user_id_other_2 = following.get(i).getUser_id();
                intent.putExtra("user_id_self", user_id_self);
                intent.putExtra("user_id_other", user_id_other_2);
                startActivity(intent);
            }
        });
    }

    private void getFollowingList() {
        String jsonStr = "{\"user_id\":\""+ user_id_other + "\"}";
        String requestUrl = getResources().getString(R.string.backend_url) + "query-following";

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
                            JSONArray userArray = jObject.getJSONArray("following");
                            for (int i = 0; i < userArray.length(); i++)
                            {
                                JSONObject user_tmp = userArray.getJSONObject(i);

                                String user_id_tmp = user_tmp.getString("user_id");
                                String username_tmp = user_tmp.getString("username");

                                User user = new User(user_id_tmp, username_tmp);
                                following.add(i, user);
                            }
                        } else {
                            FollowingListActivity.this.runOnUiThread(new Runnable() {
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
}