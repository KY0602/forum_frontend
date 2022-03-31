package com.example.hw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabFragment extends Fragment {
    private static final String LOG_TAG = TabFragment.class.getSimpleName();
    private final LinkedList<String> statusTitleAll = new LinkedList<>();
    private final LinkedList<String> statusMsgAll = new LinkedList<>();
    private ArrayList<String> title_list_all, msg_list_all;
    private RecyclerView status_all;
    private WordListAdapter mAdapter;
    private Button load_button;

    public TabFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);

        statusTitleAll.clear();
        statusMsgAll.clear();

        Bundle extras = this.getArguments();
        if (extras != null) {
            title_list_all = extras.getStringArrayList("EXTRA_TITLE");
            msg_list_all = extras.getStringArrayList("EXTRA_MESSAGE");
            for (int i = 0; i < 10; i++)
            {
                statusTitleAll.addLast(title_list_all.get(i));
                statusMsgAll.addLast(msg_list_all.get(i));
            }
        }

        load_button = v.findViewById(R.id.load_button);
        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int len = statusTitleAll.size();
                int count = 0;
                while (len + count < title_list_all.size()) {
                    statusTitleAll.addLast(title_list_all.get(len+count));
                    statusMsgAll.addLast(msg_list_all.get(len+count));
                    count++;
                    if (count > 9) break;
                }
                status_all.getAdapter().notifyItemInserted(len);
                status_all.smoothScrollToPosition(len+count);
            }
        });

        status_all = v.findViewById(R.id.recycle_all);
        mAdapter = new WordListAdapter(getContext(), statusTitleAll, statusMsgAll);
        status_all.setAdapter(mAdapter);
        status_all.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}