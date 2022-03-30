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

public class TabFragmentAll extends Fragment {
    private static final String LOG_TAG = TabFragmentAll.class.getSimpleName();
    private final LinkedList<String> statusTitleAll = new LinkedList<>();
    private final LinkedList<String> statusMsgAll = new LinkedList<>();
    private RecyclerView status_all;
    private WordListAdapter mAdapter;

    public TabFragmentAll(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_all, container, false);
        String[] titles = getResources().getStringArray(R.array.titles_all);
        String[] messages = getResources().getStringArray(R.array.messages_all);
        statusTitleAll.clear();
        statusMsgAll.clear();

        for (int i = 0; i < titles.length; i++)
        {
            statusTitleAll.addLast(titles[i]);
            statusMsgAll.addLast(messages[i]);
        }
        status_all = v.findViewById(R.id.recycle_all);
        mAdapter = new WordListAdapter(getContext(), statusTitleAll, statusMsgAll);
        status_all.setAdapter(mAdapter);
        status_all.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}