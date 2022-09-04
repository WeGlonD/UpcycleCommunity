package com.example.upcyclecommunity.database;

import java.util.ArrayList;
//regrdfvsfdsvbgndnhdnhnnr
public class Post {
    String title;
    ArrayList<String> contents;
    ArrayList<String> tags;

    public Post(String title, ArrayList<String> contents) {
        this.title = title;
        this.contents = contents;
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
}
