package com.example.upcyclecommunity.database;
import java.util.ArrayList;

public class Post {
    String title;
    ArrayList<String> contents;
    String tags;

    public Post(String title, ArrayList<String> contents, String tags) {
        this.title = title;
        this.contents = contents;
        this.tags = tags;
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
}
