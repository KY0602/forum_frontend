package com.example.hw.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.hw.Home.Status.Status;
import com.example.hw.MainActivity;
import com.example.hw.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private Context _context;
    private AppCompatActivity activity;
    private static final String LOG_TAG = PagerAdapter.class.getSimpleName();
    private ArrayList<String> type_list_all = new ArrayList<String>();
    private ArrayList<String> title_list_all = new ArrayList<String>();
    private ArrayList<String> msg_list_all = new ArrayList<String>();
    private ArrayList<String> statusid_list_all = new ArrayList<String>();
    private ArrayList<String> userid_list_all = new ArrayList<String>();
    private ArrayList<String> type_list_followed = new ArrayList<String>();
    private ArrayList<String> title_list_followed = new ArrayList<String>();
    private ArrayList<String> msg_list_followed = new ArrayList<String>();
    private ArrayList<String> statusid_list_followed = new ArrayList<String>();
    private ArrayList<String> userid_list_followed = new ArrayList<String>();
    ArrayList<Status> status_list_all = new ArrayList<Status>();
    int mNumOfTabs;


    // 初始化时创建动态列表，连后端的话可以考虑在这里从后端获取动态列表，再保存到ArrayList当中
    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context c) {
        super(fm);
        this._context = c;
        Log.d(LOG_TAG, "Pager created");
        this.mNumOfTabs = NumOfTabs;
        ArrayList<Status> status_list = new ArrayList<Status>();
//        for (int i = 1; i < 3; i++) {
//            if (i < 26) {
//                type_list_followed.add("TEXT");
//                title_list_followed.add("Message " + i);
//                msg_list_followed.add("This is Message " + i);
//            }
//            type_list_all.add("TEXT");
//            title_list_all.add("Message " + i);
//            msg_list_all.add("This is Message " + i);
//        }
        activity = (AppCompatActivity)_context;
        MainActivity mainActivity = (MainActivity) activity;
        String user_id = mainActivity.user_id;
        Log.d("",user_id);
        String jsonStr = "{\"user_id\":\"" + user_id + "\"}";
        String requestUrl = _context.getResources().getString(R.string.backend_url) + "query-all-status";
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，

            @SuppressWarnings("deprecation") RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("querystatus","fail");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String responseStr = response.body().string();
                    try {
                        Log.d("querystatus","respone");
                        JSONObject jObject = new JSONObject(responseStr);
                        boolean query_status = jObject.getBoolean("status");
                        Log.d("querystatus",jObject.toString());
                        if (query_status) {
                            Log.d("querystatus","true");
                            JSONArray statusArray = jObject.getJSONArray("status_list");
                            int count = 0;
                            for (int i = 0; i < statusArray.length(); i++) {
                                count++;
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
                                type_list_all.add(type);
                                title_list_all.add(title);
                                msg_list_all.add(text);
                                statusid_list_all.add(status_id);
                                userid_list_all.add(creator_id);
                                status_list_all.add(i, status);
                            }
                            Intent intent = new Intent("LIST-OBTAINED");
                            intent.putExtra("user_count", count);
                            LocalBroadcastManager.getInstance(_context.getApplicationContext()).sendBroadcast(intent);
                            notifyDataSetChanged();
                        } else {
//                            PagerAdapter.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(_context.getApplicationContext(), "获取失败", Toast.LENGTH_LONG).show();
//                                }
//                            });
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //query followed
//        String requestUrlf = _context.getResources().getString(R.string.backend_url) + "query-followed-status";
//        try {
//            OkHttpClient client = new OkHttpClient();
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
//
//            @SuppressWarnings("deprecation") RequestBody body = RequestBody.create(JSON, jsonStr);
//            Request request = new Request.Builder()
//                    .url(requestUrlf)
//                    .post(body)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    Log.d("queryfollowstatus","fail");
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
//                    final String responseStr = response.body().string();
//                    try {
//                        Log.d("queryfollowstatus","respone");
//                        JSONObject jObject = new JSONObject(responseStr);
//                        boolean query_status = jObject.getBoolean("status");
//                        Log.d("queryfollowstatus",jObject.toString());
//                        if (query_status) {
//                            Log.d("queryfollowstatus","true");
//                            JSONArray statusArray = jObject.getJSONArray("status_list");
//                            int count = 0;
//                            for (int i = 0; i < statusArray.length(); i++) {
//                                count++;
//                                JSONObject status_tmp = statusArray.getJSONObject(i);
//
//                                String status_id = status_tmp.getString("status_id");
//                                String creator_id = status_tmp.getString("creator_id");
//                                String creator_username = status_tmp.getString("creator_username");
//                                String type = status_tmp.getString("type");
//                                String title = status_tmp.getString("title");
//                                String text = status_tmp.getString("text");
//                                String date_tmp = status_tmp.getString("date_created");
//
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                                Date created_date = sdf.parse(date_tmp);
//
//                                int like = status_tmp.getInt("like");
//
//                                Status status = new Status(status_id, creator_id, creator_username, type, title, text, created_date
//                                        , like);
//                                type_list_followed.add(type);
//                                title_list_followed.add(title);
//                                msg_list_followed.add(text);
////                                statusid_list_followed.add(status_id);
////                                userid_list_followed.add(creator_id);
//                            }
//                            Intent intent = new Intent("LIST-OBTAINED");
//                            intent.putExtra("user_count", count);
//                            LocalBroadcastManager.getInstance(_context.getApplicationContext()).sendBroadcast(intent);
//                            notifyDataSetChanged();
//                        } else {
////                            PagerAdapter.this.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(_context.getApplicationContext(), "获取失败", Toast.LENGTH_LONG).show();
////                                }
////                            });
//                        }
//                    } catch (JSONException | ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // HomeFragment中调用addStatus来添加动态
    public void addStatus(String title, String msg) {
        type_list_all.add(0, "TEXT");
        title_list_all.add(0, title);
        msg_list_all.add(0, msg);
    }

    // 通过setArguments的方法，将之前获取的动态列表传给两个tab(all, followed)
    @Override
    public Fragment getItem(int position) {
        Bundle extras_all = new Bundle();
        Bundle extras_followed = new Bundle();
        extras_all.putStringArrayList("EXTRA_TYPE", type_list_all);
        extras_all.putStringArrayList("EXTRA_TITLE", title_list_all);
        extras_all.putStringArrayList("EXTRA_TEXT", msg_list_all);
        extras_all.putStringArrayList("EXTRA_STATUS_ID", statusid_list_all);
        extras_all.putStringArrayList("EXTRA_USER_ID", userid_list_all);
        extras_all.<Status>putParcelableArrayList("EXTRA_STATUS",status_list_all);
        extras_followed.putStringArrayList("EXTRA_TYPE", type_list_all);
        extras_followed.putStringArrayList("EXTRA_TITLE", title_list_followed);
        extras_followed.putStringArrayList("EXTRA_TEXT", msg_list_followed);
        switch (position) {
            case 0:
                TabFragment tabFragment1 = new TabFragment();
                tabFragment1.setArguments(extras_all);
                return tabFragment1;
            case 1:
                TabFragment tabFragment2 = new TabFragment();
                tabFragment2.setArguments(extras_followed);
                return tabFragment2;
            default: return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}