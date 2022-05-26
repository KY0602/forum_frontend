package com.example.hw.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hw.Home.Status.Status;
import com.example.hw.Home.Status.StatusActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalPageActivity extends AppCompatActivity {
    private static final String LOG_TAG = PersonalPageActivity.class.getSimpleName();
    private String user_id_self, user_id_other;
    ListView statusListView;
    ArrayList<Status> status_list = new ArrayList<Status>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        user_id_self = intent.getStringExtra("user_id_self");
        user_id_other = intent.getStringExtra("user_id_other");

        getStatusList();

        setContentView(R.layout.activity_personal_page);
        statusListView = findViewById(R.id.status_list_view);

        statusListView.setAdapter(new StatusItemAdapter(this, status_list));
        statusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                Bundle extras = new Bundle();
                String status_id = status_list.get(i).status_id;
                String type = status_list.get(i).type;
                String title = status_list.get(i).title;
                String text = status_list.get(i).text;
                extras.putString("status_id", status_id);
                extras.putString("user_id", user_id_self);
                extras.putString("EXTRA_TYPE", type);
                extras.putString("EXTRA_TITLE", title);
                extras.putString("EXTRA_TEXT", text);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    private void getStatusList() {
        String jsonStr = "{\"user_id\":\""+ user_id_other + "\"}";
        String requestUrl = getResources().getString(R.string.backend_url) + "query-user-status";

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
                            JSONArray statusArray = jObject.getJSONArray("status_list");
                            for (int i = 0; i < statusArray.length(); i++)
                            {
                                JSONObject status_tmp = statusArray.getJSONObject(i);

                                String status_id = status_tmp.getString("status_id");
                                String creator_id = status_tmp.getString("creator_id");
                                String creator_username = status_tmp.getString("creator_username");
                                String type = status_tmp.getString("type");
                                String title = status_tmp.getString("title");
                                String text = status_tmp.getString("text");
                                String date_tmp = status_tmp.getString("date_created");

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date created_date = sdf.parse(date_tmp);

                                int like = status_tmp.getInt("like");

                                Status status = new Status(status_id, creator_id, creator_username, type, title, text, created_date
                                , like);
                                status_list.add(i, status);
                            }
                        } else {
                            PersonalPageActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (JSONException | ParseException e) {
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