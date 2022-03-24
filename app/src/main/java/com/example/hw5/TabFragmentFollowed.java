package com.example.hw5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.*;
import androidx.fragment.app.Fragment;

public class TabFragmentFollowed extends Fragment {
    private static final String LOG_TAG = TabFragmentFollowed.class.getSimpleName();

    public TabFragmentFollowed(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_followed, container, false);

        ListView listView = (ListView)v.findViewById(R.id.msgList_followed);

        String[] titles = getResources().getStringArray(R.array.titles);
        String[] messages = getResources().getStringArray(R.array.messages);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.activity_listview, titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                String title = (String)(listView.getItemAtPosition(position));
                Bundle extras = new Bundle();
                extras.putString("EXTRA_TITLE", title);
                extras.putString("EXTRA_MESSAGE", messages[position]);
                Log.d(LOG_TAG, title);

                StatusFragment statusFragment = new StatusFragment();
                statusFragment.setArguments(extras);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, statusFragment)
                        .addToBackStack("status")
                        .commit();
            }
        });

        return v;
    }
}