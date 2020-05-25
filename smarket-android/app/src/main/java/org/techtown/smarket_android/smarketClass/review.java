package org.techtown.smarket_android.smarketClass;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.gsm.GsmCellLocation;

public class review implements Parcelable {
    String title;
    String content;
    String user;
    String score;
    String mall;
    String date;

    protected review(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(user);
        dest.writeString(score);
        dest.writeString(mall);
        dest.writeString(date);
    }

    public void readFromParcel(Parcel in){
        title = in.readString();
        content = in.readString();
        user = in.readString();
        score = in.readString();
        mall = in.readString();
        date = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public review createFromParcel(Parcel in) {
            return new review(in);
        }

        public review[] newArray(int size) {
            return new review[size];
        }
    };

    public review(String title, String content, String user, String score, String mall, String date){
        this.title = title;
        this.content = content;
        this.user = user;
        this.score = score;
        this.mall = mall;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMall() {
        return mall;
    }

    public void setMall(String mall) {
        this.mall = mall;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
