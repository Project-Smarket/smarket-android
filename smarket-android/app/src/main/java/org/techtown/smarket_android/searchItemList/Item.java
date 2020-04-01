package org.techtown.smarket_android.searchItemList;

import android.widget.ImageButton;

public class Item {

    private int item_image;
    private String item_name;
    private String item_value;

//    public Item(Drawable drawable, String item_name, String item_value) {
//        this.item_image = drawable;
//        this.item_name = item_name;
//        this.item_value = item_value;
//    }

    public int getItem_image() {
        return item_image;
    }

    public void setItem_image(int item_image) {
        this.item_image = item_image;
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

}
