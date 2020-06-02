package org.techtown.smarket_android.DTO_Class;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotdeal implements Parcelable {

    private String category;
    private String title;
    private String url;
    private String replyCount;
    private String hit;
    private String time;

    public Hotdeal( String category, String title, String url, String replyCount, String hit, String time) {
        this.category = category;
        this.title = title;
        this.url = url;
        this.replyCount = replyCount;
        this.hit = hit;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    protected Hotdeal(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(category);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(replyCount);
        dest.writeString(hit);
        dest.writeString(time);

    }

    public void readFromParcel(Parcel in){
        category = in.readString();
        title = in.readString();
        url = in.readString();
        replyCount = in.readString();
        hit = in.readString();
        time = in.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hotdeal createFromParcel(Parcel in) {
            return new Hotdeal(in);
        }

        public Hotdeal[] newArray(int size) {
            return new Hotdeal[size];
        }
    };
}
