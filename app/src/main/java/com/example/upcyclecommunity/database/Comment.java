package com.example.upcyclecommunity.database;

public class Comment {
    String text;
    String WriterUid;
    String key;

    public Comment(String text, String writerUid, String key) {
        this.text = text;
        WriterUid = writerUid;
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWriterUid() {
        return WriterUid;
    }

    public void setWriterUid(String writerUid) {
        WriterUid = writerUid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
