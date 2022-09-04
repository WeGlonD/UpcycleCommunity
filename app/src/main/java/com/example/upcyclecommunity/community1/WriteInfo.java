package com.example.upcyclecommunity.community1;

public class WriteInfo {
    private String title;
    private String Contents;
    private String pic;
    private String video;

    public WriteInfo(String title, String contents) {
        this.title = title;
        this.Contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return Contents;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        Contents = contents;
    }
}
