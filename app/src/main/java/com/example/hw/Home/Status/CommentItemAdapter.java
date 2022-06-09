package com.example.hw.Home.Status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hw.R;

import java.util.ArrayList;

public class CommentItemAdapter extends BaseAdapter {
    ArrayList<Comment> comment_list = new ArrayList<Comment>();
    private LayoutInflater layoutInflater;
    private Context context;

    public CommentItemAdapter(Context aContext, ArrayList<Comment> comment_list ) {
        this.context = aContext;
        this.comment_list =  comment_list;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return  comment_list.size();
    }

    @Override
    public Object getItem(int i) {
        return  comment_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.comment_list_item_layout, null);
            holder = new ViewHolder();
            holder.username = (TextView) view.findViewById(R.id.comment_item_creatorname);
            holder.content = (TextView) view.findViewById(R.id.comment_item_content);
            holder.date_created = (TextView) view.findViewById(R.id.comment_item_date);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        Comment comment = this.comment_list.get(i);
        holder.username.setText(comment.creator_username);
        holder.content.setText(comment.content);
        holder.date_created.setText(comment.getDate());
        return view;
    }

    static class ViewHolder {
        TextView username,content, date_created;
    }
}
