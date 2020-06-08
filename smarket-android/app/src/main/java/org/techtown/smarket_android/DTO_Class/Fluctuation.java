package org.techtown.smarket_android.DTO_Class;

import java.util.Date;

public class Fluctuation {
    Date date;
    String lprice;
    int lprice_diff;

    public Fluctuation(Date date, String lprice) {
        this.date = date;
        this.lprice = lprice;
    }

    public Fluctuation(Date date, String lprice, int lprice_diff) {
        this.date = date;
        this.lprice = lprice;
        this.lprice_diff = lprice_diff;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
