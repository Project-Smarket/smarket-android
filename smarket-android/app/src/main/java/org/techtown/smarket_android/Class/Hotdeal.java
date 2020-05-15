package org.techtown.smarket_android.Class;

public class Hotdeal {

    private String category;
    private String title;
    private String url;
    private String replyCount;
    private String hit;
    private String time;

    public Hotdeal( String category, String title, String url, String replyCount, String hit, String time) {
        this.category = category;
        this.title = title;
        this.url = url;
        this.replyCount = replyCount;
        this.hit = hit;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
