package com.example.hw.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabFragment extends Fragment {
    private static final String LOG_TAG = TabFragment.class.getSimpleName();
    private final LinkedList<String> statusTypeAll = new LinkedList<>();
    private final LinkedList<String> statusTitleAll = new LinkedList<>();
    private final LinkedList<String> statusMsgAll = new LinkedList<>();
    private ArrayList<String> type_list_all, title_list_all, msg_list_all;
    private RecyclerView status_all;
    private WordListAdapter mAdapter;
    private Button load_button;

    public TabFragment(){
        // require a empty public constructor
    }

    // 通过getArguments从PagerAdapter获取动态列表，此处MUSIC和VIDEO只是为了测试音频和视频而写死两个动态
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);

        statusTypeAll.clear();
        statusTitleAll.clear();
        statusMsgAll.clear();

        Bundle extras = this.getArguments();
        if (extras != null) {
            type_list_all = extras.getStringArrayList("EXTRA_TYPE");
            title_list_all = extras.getStringArrayList("EXTRA_TITLE");
            msg_list_all = extras.getStringArrayList("EXTRA_MESSAGE");
            for (int i = 0; i < 10; i++)
            {
                statusTypeAll.addLast(type_list_all.get(i));
                statusTitleAll.addLast(title_list_all.get(i));
                statusMsgAll.addLast(msg_list_all.get(i));
            }
        }
        statusTypeAll.addFirst("MUSIC");
        statusTitleAll.addFirst("Music");
        statusMsgAll.addFirst("This is a music.");

        statusTypeAll.addFirst("VIDEO");
        statusTitleAll.addFirst("Video");
        statusMsgAll.addFirst("This is a video.");

        load_button = v.findViewById(R.id.load_button);
        load_button.setOnClickListener(this::loadMore);

        status_all = v.findViewById(R.id.recycle_all);
        mAdapter = new WordListAdapter(getContext(), statusTypeAll, statusTitleAll, statusMsgAll);
        status_all.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager((getContext()));
        status_all.setLayoutManager(llm);

        // 划到最底时会调用loadMore显示更多动态
        status_all.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (llm.findLastCompletelyVisibleItemPosition() >= statusTitleAll.size()-3) {
                    loadMore(v);
                }
            }
        });
        return v;
    }

    // 当点击“加载更多”按钮或划到最底时调用loadMore显示更多动态
    private void loadMore(View view) {
        int len = statusTitleAll.size();
        int count = 0;
        while (len + count < title_list_all.size()) {
            statusTypeAll.addLast(type_list_all.get(len+count));
            statusTitleAll.addLast(title_list_all.get(len+count));
            statusMsgAll.addLast(msg_list_all.get(len+count));
            count++;
            if (count > 9) break;
        }
        status_all.getAdapter().notifyItemInserted(len);
        if (count == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "已没有更多动态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "已加载更多动态", Toast.LENGTH_SHORT).show();
        }
    }
}