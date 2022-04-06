package com.example.hw;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    private static final String LOG_TAG = WordListAdapter.class.getSimpleName();
    private final LinkedList<String> mWordList;
    private final LinkedList<String> mContentList;
    private final LayoutInflater mInflater;
    private AppCompatActivity activity;

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView wordItemView;
        final WordListAdapter mAdapter;

        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            String element = mWordList.get(mPosition);
            // Change the word in the mWordList.

            mAdapter.notifyDataSetChanged();
        }
    }

    public WordListAdapter(Context context, LinkedList<String> wordList, LinkedList<String> contentList) {
        mInflater = LayoutInflater.from(context);
        this.mWordList = wordList;
        this.mContentList = contentList;
    }

    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.wordlist_item, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordListAdapter.WordViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        String title = mWordList.get(position);
        String msg = mContentList.get(position);
        // Add the data to the view holder.
        holder.wordItemView.setText(title);
        holder.wordItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity = (AppCompatActivity)v.getContext();
                Log.d(LOG_TAG, title);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_TITLE", title);
                extras.putString("EXTRA_MESSAGE", msg);
                Intent intent  = new Intent(v.getContext(), StatusActivity.class);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }
}
