package com.uca.upcyclecommunity.database;

public class Brand {

    private String name;
    private String url;
    private String tags;
    private String pic;

    public Brand(){
    }

    public Brand(String name, String url, String tags, String pic){
        this.name = name;
        this.url = url;
        this.tags = tags;
        this.pic = pic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
