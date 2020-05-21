package org.techtown.smarket_android.Class;

public class review {
    String title;
    String content;
    String user;
    String score;
    String mall;
    String date;

    public review(){

    }

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
