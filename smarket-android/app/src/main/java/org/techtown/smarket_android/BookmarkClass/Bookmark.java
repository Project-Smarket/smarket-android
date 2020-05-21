package org.techtown.smarket_android.BookmarkClass;

// bookmark_item_list_adapter에서 적용될 Bookmark_client 클래스 입니다.
public class Bookmark {
    private String folder_name;
    private String bookmark_id;
    private String bookmark_title;
    private String bookmark_itemId;
    private String bookmark_type;
    private Boolean bookmark_selling;
    private String bookmark_lprice;
    private String bookmark_link;
    private String bookmark_image_url;
    private BookmarkAlarm bookmarkAlarm ;

    public Bookmark(String bookmark_id, String folder_name, String bookmark_title, String bookmark_itemId, String bookmark_type, Boolean bookmark_selling, String bookmark_lprice, String bookmark_link, String bookmark_image_url, BookmarkAlarm bookmarkAlarm) {
        this.bookmark_id = bookmark_id;
        this.folder_name = folder_name;
        this.bookmark_title = bookmark_title;
        this.bookmark_itemId = bookmark_itemId;
        this.bookmark_type = bookmark_type;
        this.bookmark_selling = bookmark_selling;
        this.bookmark_lprice = bookmark_lprice;
        this.bookmark_link = bookmark_link;
        this.bookmark_image_url = bookmark_image_url;
        this.bookmarkAlarm = bookmarkAlarm;
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

    public String getBookmark_title() {
        return bookmark_title;
    }

    public void setBookmark_title(String bookmark_title) {
        this.bookmark_title = bookmark_title;
    }

    public String getBookmark_itemId() {
        return bookmark_itemId;
    }

    public void setBookmark_itemId(String bookmark_itemId) {
        this.bookmark_itemId = bookmark_itemId;
    }

    public String getBookmark_type() {
        return bookmark_type;
    }

    public void setBookmark_type(String bookmark_type) {
        this.bookmark_type = bookmark_type;
    }

    public Boolean getBookmark_selling() {
        return bookmark_selling;
    }

    public void setBookmark_selling(Boolean bookmark_selling) {
        this.bookmark_selling = bookmark_selling;
    }

    public String getBookmark_lprice() {
        return bookmark_lprice;
    }

    public void setBookmark_lprice(String bookmark_lprice) {
        this.bookmark_lprice = bookmark_lprice;
    }

    public String getBookmark_link() {
        return bookmark_link;
    }

    public void setBookmark_link(String bookmark_link) {
        this.bookmark_link = bookmark_link;
    }

    public String getBookmark_image_url() {
        return bookmark_image_url;
    }

    public void setBookmark_image_url(String bookmark_image_url) {
        this.bookmark_image_url = bookmark_image_url;
    }

    public BookmarkAlarm getBookmarkAlarm() {
        return bookmarkAlarm;
    }

    public void setBookmarkAlarm(BookmarkAlarm bookmarkAlarm) {
        this.bookmarkAlarm = bookmarkAlarm;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "folder_name='" + folder_name + '\'' +
                ", bookmark_id='" + bookmark_id + '\'' +
                ", bookmark_title='" + bookmark_title + '\'' +
                ", bookmark_itemId='" + bookmark_itemId + '\'' +
                '}';
    }
}

