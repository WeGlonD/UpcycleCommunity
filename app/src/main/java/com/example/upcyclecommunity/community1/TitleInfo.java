package com.example.upcyclecommunity.community1;

public class TitleInfo {
    private String title;
    private Long commentcnt;

    public TitleInfo(String title, Long commentcnt) {
        this.title = title;
        this.commentcnt = commentcnt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCommentcnt() {
        return commentcnt;
    }

    public void setCommentcnt(Long commentcnt) {
        this.commentcnt = commentcnt;
    }
}
