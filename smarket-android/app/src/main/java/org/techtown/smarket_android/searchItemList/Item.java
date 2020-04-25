package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.widget.ImageButton;

import java.net.URL;

public class Item {


    private String item_name;
    private String item_value;
    private String item_image;
    private String item_mall;

    public Item(String item_name, String item_value, String item_image, String item_mall) {
        this.item_name = item_name;
        this.item_value = item_value;
        this.item_image = item_image;
        this.item_mall = item_mall;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_value() {
        return item_value;
    }

    public void setItem_value(String item_value) {
        this.item_value = item_value;
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
}
