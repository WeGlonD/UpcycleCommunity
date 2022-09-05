package com.example.upcyclecommunity.database;

public class Comment {
    String text;
    String WriterUid;

    public Comment(String text, String writerUid) {
        this.text = text;
        WriterUid = writerUid;
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
}
