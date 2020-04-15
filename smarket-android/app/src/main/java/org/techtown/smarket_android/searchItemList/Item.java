package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.widget.ImageButton;

import java.net.URL;

public class Item {

    private String list_item_image;
    private String list_item_name;
    private String list_item_value;
    private String list_item_mall;
    private int item_image;

    public String getList_item_mall() {
        return list_item_mall;
    }

    public void setList_item_mall(String list_item_mall) {
        this.list_item_mall = list_item_mall;
    }

    public int getItem_image() {
        return item_image;
    }

    public void setItem_image(int item_image) {
        this.item_image = item_image;
    }

    public String getList_item_image() {
        return list_item_image;
    }

    public void setList_item_image(String list_item_image) {
        this.list_item_image = list_item_image;
    }

    public String getList_item_name() {
        return list_item_name;
    }

    public void setList_item_name(String list_item_name) {
        this.list_item_name = list_item_name;
    }

    public String getList_item_value() {
        return list_item_value;
    }

    public void setList_item_value(String list_item_value) {
        this.list_item_value = list_item_value;
    }
}
