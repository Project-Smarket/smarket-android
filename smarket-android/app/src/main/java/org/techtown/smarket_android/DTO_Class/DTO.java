package org.techtown.smarket_android.DTO_Class;

import android.os.Parcel;
import android.os.Parcelable;

public class DTO implements Parcelable {

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

    // SearchedItem
    public DTO(String item_title, String item_link, String item_image, String item_lprice, String item_mallName, String item_id, String item_type, String item_brand, String item_maker, String item_category1, String item_category2, String item_category3, String item_category4) {
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
    }


    // Bookmark
    public DTO(String id, String user_id, String folder_name, boolean item_selling, String item_alarm, String item_title, String item_link, String item_image, String item_lprice, String item_mallName, String item_id, String item_type, String item_brand, String item_maker, String item_category1, String item_category2, String item_category3, String item_category4) {
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

    protected DTO(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item_title);
        dest.writeString(item_link);
        dest.writeString(item_image);
        dest.writeString(item_lprice);
        dest.writeString(item_mallName);
        dest.writeString(item_id);
        dest.writeString(item_type);
        dest.writeString(item_brand);
        dest.writeString(item_maker);
        dest.writeString(item_category1);
        dest.writeString(item_category2);
        dest.writeString(item_category3);
        dest.writeString(item_category4);
    }

    public void readFromParcel(Parcel in){
        item_title = in.readString();
        item_link = in.readString();
        item_image = in.readString();
        item_lprice = in.readString();
        item_mallName = in.readString();
        item_id = in.readString();
        item_type = in.readString();
        item_brand = in.readString();
        item_maker = in.readString();
        item_category1 = in.readString();
        item_category2 = in.readString();
        item_category3 = in.readString();
        item_category4 = in.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DTO createFromParcel(Parcel in) {
            return new DTO(in);
        }

        public DTO[] newArray(int size) {
            return new DTO[size];
        }
    };
}