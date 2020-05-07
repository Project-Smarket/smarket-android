package org.techtown.smarket_android.Class;

public class BookmarkAlarm {

    private String folder_name;
    private String bookmark_id;
    private int alarm_time;
    private Boolean alarm_check;

    public BookmarkAlarm(String folder_name, String bookmark_id, int alarm_time, Boolean alarm_check) {
        this.folder_name = folder_name;
        this.bookmark_id = bookmark_id;
        this.alarm_time = alarm_time;
        this.alarm_check = alarm_check;
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

    @Override
    public String toString() {
        return "BookmarkAlarm{" +
                "folder_name='" + folder_name + '\'' +
                ", bookmark_id='" + bookmark_id + '\'' +
                ", alarm_time=" + alarm_time +
                ", alarm_check=" + alarm_check +
                '}';
    }
}

