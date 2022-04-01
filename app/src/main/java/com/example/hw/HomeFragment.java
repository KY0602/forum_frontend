package com.example.hw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private PagerAdapter adapter;
    private ArrayList<String> title_list = new ArrayList<String>();
    private ArrayList<String> msg_list = new ArrayList<String>();

    public HomeFragment(){
        // require a empty public constructor
    }

    // Save state
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("TITLE_LIST", title_list);
        outState.putStringArrayList("MSG_LIST", msg_list);
        Log.d(LOG_TAG, "Save");
    }

    // Restore saved state
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "Home activity created");
        if (savedInstanceState != null) {
            title_list = savedInstanceState.getStringArrayList("TITLE_LIST");
            msg_list = savedInstanceState.getStringArrayList("MSG_LIST");
            if (title_list.size() != 0) {
                if (adapter != null) {
                    Log.d(LOG_TAG, "Restoring adapter");
                    for (int i = title_list.size() - 1; i >= 0; i--)
                    {
                        adapter.addStatus(title_list.get(i), msg_list.get(i));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(LOG_TAG, "Home created");

        TabLayout tabLayout =(TabLayout)v.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_label1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_label2));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)v.findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });
        return v;
    }

    public void addStatus(String title, String msg) {
        if (adapter != null) {
            title_list.add(0, title);
            msg_list.add(0, msg);
            Log.d(LOG_TAG, "Added");
            adapter.addStatus(title, msg);
            adapter.notifyDataSetChanged();
        }
    }
}