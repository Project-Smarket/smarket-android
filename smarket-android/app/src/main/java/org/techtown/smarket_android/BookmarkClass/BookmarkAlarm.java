package org.techtown.smarket_android.BookmarkClass;

public class BookmarkAlarm {

    private String user_id;
    private String folder_name;
    private String bookmark_id;
    private String bookmark_price;
    private int alarm_time;
    private Boolean alarm_check;

    public BookmarkAlarm(String user_id, String folder_name, String bookmark_id, String bookmark_price, int alarm_time, Boolean alarm_check) {
        this.user_id = user_id;
        this.folder_name = folder_name;
        this.bookmark_id = bookmark_id;
        this.bookmark_price = bookmark_price;
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

    public String getBookmark_id() {
        return bookmark_id;
    }

    public void setBookmark_id(String bookmark_id) {
        this.bookmark_id = bookmark_id;
    }

    public String getBookmark_price() {
        return bookmark_price;
    }

    public void setBookmark_price(String bookmark_price) {
        this.bookmark_price = bookmark_price;
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

