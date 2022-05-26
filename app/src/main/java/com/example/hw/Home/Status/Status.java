package com.example.hw.Home.Status;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Status {
    public String status_id, creator_id, creator_username, type, title, text;
    public Date date_created;
    int like;

    public Status(String status_id, String creator_id, String creator_username, String type, String title, String text,
                  Date date_created, int like)
    {
        this.status_id = status_id;
        this.creator_id = creator_id;
        this.creator_username = creator_username;
        this.type = type;
        this.title = title;
        this.text = text;
        this.date_created = date_created;
        this.like = like;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getDate() {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = spf.format(this.date_created);
        return date;
    }
}
