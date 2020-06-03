package org.techtown.smarket_android.DTO_Class;

public class Fluctuation {
    String date;
    String lprice;
    int lprice_diff;

    public Fluctuation(String date, String lprice) {
        this.date = date;
        this.lprice = lprice;
        this.lprice_diff = 0;
    }

    public Fluctuation(String date, String lprice, int lprice_diff) {
        this.date = date;
        this.lprice = lprice;
        this.lprice_diff = lprice_diff;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLprice() {
        return lprice;
    }

    public void setLprice(String lprice) {
        this.lprice = lprice;
    }

    public int getLprice_diff() {
        return lprice_diff;
    }

    public void setLprice_diff(int lprice_diff) {
        this.lprice_diff = lprice_diff;
    }
}
