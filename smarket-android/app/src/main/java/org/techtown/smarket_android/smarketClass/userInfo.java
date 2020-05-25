package org.techtown.smarket_android.smarketClass;

import java.util.ArrayList;
import java.util.List;

public class userInfo {
    private String user_id;
    private Boolean alarm_check;
    private List<String> bookmarkFolderList;

    // 회원가입 시
    // 사용자 아이디, 사용자 알람 설정, 사용자 북마크 폴더 리스트 디폴트 생성
    public userInfo(String user_id) {
        this.user_id = user_id;
        this.alarm_check = true;
        this.bookmarkFolderList = new ArrayList<>();
        bookmarkFolderList.add("내 북마크 폴더");
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Boolean getAlarm_check() {
        return alarm_check;
    }

    public void setAlarm_check(Boolean alarm_check) {
        this.alarm_check = alarm_check;
    }

    public List<String> getBookmarkFolderList() {
        return bookmarkFolderList;
    }

    public void setBookmarkFolderList(List<String> bookmarkFolderList) {
        this.bookmarkFolderList = bookmarkFolderList;
    }
}
