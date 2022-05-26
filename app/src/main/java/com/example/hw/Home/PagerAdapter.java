package com.example.hw.Home;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = PagerAdapter.class.getSimpleName();
    private ArrayList<String> type_list_all = new ArrayList<String>();
    private ArrayList<String> title_list_all = new ArrayList<String>();
    private ArrayList<String> msg_list_all = new ArrayList<String>();
    private ArrayList<String> type_list_followed = new ArrayList<String>();
    private ArrayList<String> title_list_followed = new ArrayList<String>();
    private ArrayList<String> msg_list_followed = new ArrayList<String>();
    int mNumOfTabs;

    // 初始化时创建动态列表，连后端的话可以考虑在这里从后端获取动态列表，再保存到ArrayList当中
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        Log.d(LOG_TAG, "Pager created");
        this.mNumOfTabs = NumOfTabs;
        for (int i = 1; i < 51; i++) {
            if (i < 26) {
                type_list_followed.add("TEXT");
                title_list_followed.add("Message " + i);
                msg_list_followed.add("This is Message " + i);
            }
            type_list_all.add("TEXT");
            title_list_all.add("Message " + i);
            msg_list_all.add("This is Message " + i);
        }
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