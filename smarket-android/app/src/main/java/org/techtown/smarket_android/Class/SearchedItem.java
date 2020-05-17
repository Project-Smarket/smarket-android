package org.techtown.smarket_android.Class;

import android.graphics.Bitmap;
import android.widget.ImageButton;

import java.net.URL;

public class SearchedItem {


    private String item_title;
    private String item_id;
    private String item_type;
    private String item_price;
    private String item_image;
    private String item_mall;

    // alarm 에서 사용하는 데이터
    private String alarm_type;
    private String updated_price;
    private String alarm_message;



    public SearchedItem(String item_title, String item_id, String item_type, String item_price, String item_image, String item_mall) {
        this.item_title = item_title;
        this.item_id = item_id;
        this.item_type = item_type;
        this.item_price = item_price;
        this.item_image = item_image;
        this.item_mall = item_mall;
    }

    public SearchedItem(String item_title, String item_id, String item_type, String item_price, String item_image, String item_mall, String alarm_type, String updated_price) {
        this.item_title = item_title;
        this.item_id = item_id;
        this.item_type = item_type;
        this.item_price = item_price;
        this.item_image = item_image;
        this.item_mall = item_mall;
        this.alarm_type = alarm_type;
        this.updated_price = updated_price;
        alarm_message = updated_price + "원" + alarm_type + "했습니다";
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

    public String getItem_mall() {
        return item_mall;
    }

    public void setItem_mall(String item_mall) {
        this.item_mall = item_mall;
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

    @Override
    public String toString() {
        return "SearchedItem{" +
                "item_title='" + item_title + '\'' +
                ", item_price='" + item_price + '\'' +
                ", updated_price='" + updated_price + '\'' +
                '}';
    }
}
