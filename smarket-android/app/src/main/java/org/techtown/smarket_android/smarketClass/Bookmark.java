package org.techtown.smarket_android.smarketClass;

// bookmark_item_list_adapter에서 적용될 Bookmark_client 클래스 입니다.
public class Bookmark {
    private String id;
    private String user_id;
    private String folder_name;
    private boolean item_selling;
    private boolean item_alarm;
    private String item_title;
    private String item_link;
    private String item_image;
    private String item_lprice;
    private String item_id;
    private String item_type;

    public Bookmark(String id, String user_id, String folder_name, boolean item_selling, boolean item_alarm, String item_title, String item_link, String item_image, String item_lprice, String item_id, String item_type) {
        this.id = id;
        this.user_id = user_id;
        this.folder_name = folder_name;
        this.item_selling = item_selling;
        this.item_alarm = item_alarm;
        this.item_title = item_title;
        this.item_link = item_link;
        this.item_image = item_image;
        this.item_lprice = item_lprice;
        this.item_id = item_id;
        this.item_type = item_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isItem_selling() {
        return item_selling;
    }

    public void setItem_selling(boolean item_selling) {
        this.item_selling = item_selling;
    }

    public boolean isItem_alarm() {
        return item_alarm;
    }

    public void setItem_alarm(boolean item_alarm) {
        this.item_alarm = item_alarm;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getItem_link() {
        return item_link;
    }

    public void setItem_link(String item_link) {
        this.item_link = item_link;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_lprice() {
        return item_lprice;
    }

    public void setItem_lprice(String item_lprice) {
        this.item_lprice = item_lprice;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }
}