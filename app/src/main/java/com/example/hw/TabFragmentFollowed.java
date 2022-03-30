package com.example.hw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class TabFragmentFollowed extends Fragment {
    private static final String LOG_TAG = TabFragmentFollowed.class.getSimpleName();
    private final LinkedList<String> statusTitleFollowed = new LinkedList<>();
    private final LinkedList<String> statusMsgFollowed = new LinkedList<>();
    private RecyclerView status_followed;
    private WordListAdapter mAdapter;

    public TabFragmentFollowed(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_followed, container, false);
        String[] titles = getResources().getStringArray(R.array.titles);
        String[] messages = getResources().getStringArray(R.array.messages);
        statusTitleFollowed.clear();
        statusMsgFollowed.clear();

        for (int i = 0; i < titles.length; i++)
        {
            statusTitleFollowed.addLast(titles[i]);
            statusMsgFollowed.addLast(messages[i]);
        }
        status_followed = v.findViewById(R.id.recycle_followed);
        mAdapter = new WordListAdapter(getContext(), statusTitleFollowed, statusMsgFollowed);
        status_followed.setAdapter(mAdapter);
        status_followed.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}