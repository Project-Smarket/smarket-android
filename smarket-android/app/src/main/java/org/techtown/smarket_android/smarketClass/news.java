package org.techtown.smarket_android.smarketClass;

import android.os.Parcel;
import android.os.Parcelable;

public class news implements Parcelable {
    private String newsImg;
    private String newsTitle;
    private String newsUrl;
    private String newsUser;
    private String newsHit;
    private String newsDate;

    public news(){

    }

    public news(String newsImg, String newsTitle, String newsUrl, String newsUser, String newsHit, String newsDate){
        this.newsImg = newsImg;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.newsUser = newsUser;
        this.newsHit = newsHit;
        this.newsDate = newsDate;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(String newsImg) {
        this.newsImg = newsImg;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsUser() {
        return newsUser;
    }

    public void setNewsUser(String newsUser) {
        this.newsUser = newsUser;
    }

    public String getNewsHit() {
        return newsHit;
    }

    public void setNewsHit(String newsHit) {
        this.newsHit = newsHit;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected news(Parcel in){
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsImg);
        dest.writeString(newsTitle);
        dest.writeString(newsUrl);
        dest.writeString(newsUser);
        dest.writeString(newsHit);
        dest.writeString(newsDate);
    }

    public void readFromParcel(Parcel in){
        newsImg = in.readString();
        newsTitle = in.readString();
        newsUrl= in.readString();
        newsUser = in.readString();
        newsHit = in.readString();
        newsDate = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public news createFromParcel(Parcel in) {
            return new news(in);
        }

        public news[] newArray(int size) {
            return new news[size];
        }
    };
}
