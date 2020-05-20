package org.techtown.smarket_android.Class;

public class news {
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
}
