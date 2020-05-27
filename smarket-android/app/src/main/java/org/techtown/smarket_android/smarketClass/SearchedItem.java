package org.techtown.smarket_android.smarketClass;

public class SearchedItem {

    private String id;
    private String item_title;
    private String item_id;
    private String item_type;
    private String item_price;
    private String item_image;
    private String item_mallName;
    private String item_link;
    private String item_brand;
    private String item_maker;
    private String item_category1;
    private String item_category2;
    private String item_category3;
    private String item_category4;



    // alarm 에서 사용하는 데이터
    private String user_id;
    private String alarm_type;
    private String updated_price;
    private String alarm_message;
    private String alarm_date;



    // search에서 사용하는 클래스 생성자
    public SearchedItem(String item_title, String item_id, String item_type, String item_price, String item_image, String item_mallName
    , String item_link, String item_brand, String item_maker, String item_category1, String item_category2, String item_category3, String item_category4) {
        this.item_title = item_title;
        this.item_id = item_id;
        this.item_type = item_type;
        this.item_price = item_price;
        this.item_image = item_image;
        this.item_mallName = item_mallName;
        this.item_link = item_link;
        this.item_brand = item_brand;
        this.item_maker = item_maker;
        this.item_category1 = item_category1;
        this.item_category2 = item_category2;
        this.item_category3 = item_category3;
        this.item_category4 = item_category4;
    }

    // alarm에서 사용하는 클래스 생성자
    public SearchedItem(String user_id, String id, String item_title, String item_id, String item_type, String item_price, String item_image, String item_mallName, String alarm_type, String updated_price, String alarm_date) {
        this.user_id = user_id;
        this.id = id;
        this.item_title = item_title;
        this.item_id = item_id;
        this.item_type = item_type;
        this.item_price = item_price;
        this.item_image = item_image;
        this.item_mallName = item_mallName;
        this.alarm_type = alarm_type;
        this.updated_price = updated_price;
        alarm_message = updated_price + "원";
        this.alarm_date = alarm_date;
    }

    public String getAlarm_date() {
        return alarm_date;
    }

    public void setAlarm_date(String alarm_date) {
        this.alarm_date = alarm_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
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

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_mallName() {
        return item_mallName;
    }

    public void setItem_mallName(String item_mallName) {
        this.item_mallName = item_mallName;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public String getUpdated_price() {
        return updated_price;
    }

    public void setUpdated_price(String updated_price) {
        this.updated_price = updated_price;
    }

    public String getAlarm_message() {
        return alarm_message;
    }

    public void setAlarm_message(String alarm_message) {
        this.alarm_message = alarm_message;
    }

    public String getItem_link() {
        return item_link;
    }

    public void setItem_link(String item_link) {
        this.item_link = item_link;
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

    @Override
    public String toString() {
        return "SearchedItem{" +
                "item_title='" + item_title + '\'' +
                ", item_price='" + item_price + '\'' +
                ", updated_price='" + updated_price + '\'' +
                '}';
    }
}
