package com.uca.upcyclecommunity.database;
import java.util.ArrayList;

public class Post {
    String title;
    ArrayList<String> contents;
    String tags;
    String user_id;
    String timeStamp;
    Long clickCnt;

    public Post(String title, ArrayList<String> contents, String tags, String user_id, String timeStamp, Long clickCnt) {
        this.title = title;
        this.contents = contents;
        this.tags = tags;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
        this.clickCnt = clickCnt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Long getClickCnt() {
        return clickCnt;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setClickCnt(Long clickCnt) {
        this.clickCnt = clickCnt;
    }
}
