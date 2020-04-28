package org.techtown.smarket_android.searchItemList;

public class Bookmark {
    private String user_id;
    private String folder_name;
    private String bookmark_name;
    private String bookmark_url;
    private String bookmark_image_url;
    private String bookmark_price;
    private Boolean bookmark_check;
    private int alarm_time;
    private Boolean alarm_check;

    public Bookmark(String user_id, String folder_name, String bookmark_name, String bookmark_url, String bookmark_image_url, String bookmark_price, Boolean bookmark_check, int alarm_time, Boolean alarm_check) {
        this.user_id = user_id;
        this.folder_name = folder_name;
        this.bookmark_name = bookmark_name;
        this.bookmark_url = bookmark_url;
        this.bookmark_image_url = bookmark_image_url;
        this.bookmark_price = bookmark_price;
        this.bookmark_check = bookmark_check;
        this.alarm_time = alarm_time;
        this.alarm_check = alarm_check;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public String getBookmark_name() {
        return bookmark_name;
    }

    public void setBookmark_name(String bookmark_name) {
        this.bookmark_name = bookmark_name;
    }

    public String getBookmark_url() {
        return bookmark_url;
    }

    public void setBookmark_url(String bookmark_url) {
        this.bookmark_url = bookmark_url;
    }

    public String getBookmark_image_url() {
        return bookmark_image_url;
    }

    public void setBookmark_image_url(String bookmark_image_url) {
        this.bookmark_image_url = bookmark_image_url;
    }

    public String getBookmark_price() {
        return bookmark_price;
    }

    public void setBookmark_price(String bookmark_price) {
        this.bookmark_price = bookmark_price;
    }

    public Boolean getBookmark_check() {
        return bookmark_check;
    }

    public void setBookmark_check(Boolean bookmark_check) {
        this.bookmark_check = bookmark_check;
    }

    public int getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(int alarm_time) {
        this.alarm_time = alarm_time;
    }

    public Boolean getAlarm_check() {
        return alarm_check;
    }

    public void setAlarm_check(Boolean alarm_check) {
        this.alarm_check = alarm_check;
    }

}
