package com.example.upcyclecommunity.database;
import java.util.ArrayList;

public class Post {
    String title;
    ArrayList<String> contents;
    String tags;
    String user_id;

    public Post(String title, ArrayList<String> contents, String tags, String user_id) {
        this.title = title;
        this.contents = contents;
        this.tags = tags;
        this.user_id = user_id;
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
}
