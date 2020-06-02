package org.techtown.smarket_android.DTO_Class;

public class Alarm {

    // 상품 상세
    private String id;
    private String user_id;
    private String folder_name;
    private boolean item_selling;
    private String item_alarm;

    private String item_title;
    private String item_link;
    private String item_image;
    private String item_lprice;
    private String item_mallName;
    private String item_id;
    private String item_type;
    private String item_brand;
    private String item_maker;
    private String item_category1;
    private String item_category2;
    private String item_category3;
    private String item_category4;

    private int lprice_diff;

    // 알람 리스트
    private String alarm_date;

    public Alarm(String id, String user_id, String folder_name, boolean item_selling, String item_alarm, String item_title, String item_link, String item_image, String item_lprice, String item_mallName, String item_id, String item_type, String item_brand, String item_maker, String item_category1, String item_category2, String item_category3, String item_category4, int lprice_diff, String alarm_date) {
        this.id = id;
        this.user_id = user_id;
        this.folder_name = folder_name;
        this.item_selling = item_selling;
        this.item_alarm = item_alarm;
        this.item_title = item_title;
        this.item_link = item_link;
        this.item_image = item_image;
        this.item_lprice = item_lprice;
        this.item_mallName = item_mallName;
        this.item_id = item_id;
        this.item_type = item_type;
        this.item_brand = item_brand;
        this.item_maker = item_maker;
        this.item_category1 = item_category1;
        this.item_category2 = item_category2;
        this.item_category3 = item_category3;
        this.item_category4 = item_category4;
        this.lprice_diff = lprice_diff;
        this.alarm_date = alarm_date;
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

    public String getItem_alarm() {
        return item_alarm;
    }

    public void setItem_alarm(String item_alarm) {
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

    public String getItem_mallName() {
        return item_mallName;
    }

    public void setItem_mallName(String item_mallName) {
        this.item_mallName = item_mallName;
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

    public String getItem_brand() {
        return item_brand;
    }

    public void setItem_brand(String item_brand) {
        this.item_brand = item_brand;
    }

    public String getItem_maker() {
        return item_maker;
    }

    public void setItem_maker(String item_maker) {
        this.item_maker = item_maker;
    }

    public String getItem_category1() {
        return item_category1;
    }

    public void setItem_category1(String item_category1) {
        this.item_category1 = item_category1;
    }

    public String getItem_category2() {
        return item_category2;
    }

    public void setItem_category2(String item_category2) {
        this.item_category2 = item_category2;
    }

    public String getItem_category3() {
        return item_category3;
    }

    public void setItem_category3(String item_category3) {
        this.item_category3 = item_category3;
    }

    public String getItem_category4() {
        return item_category4;
    }

    public void setItem_category4(String item_category4) {
        this.item_category4 = item_category4;
    }

    public int getLprice_diff() {
        return lprice_diff;
    }

    public void setLprice_diff(int lprice_diff) {
        this.lprice_diff = lprice_diff;
    }

    public String getAlarm_date() {
        return alarm_date;
    }

    public void setAlarm_date(String alarm_date) {
        this.alarm_date = alarm_date;
    }
}