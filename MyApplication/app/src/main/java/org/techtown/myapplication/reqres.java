package org.techtown.myapplication;

import java.util.List;

public class reqres {
    String page;
    String per_page;
    String total;
    String total_pages;
    List<data> reqres_data;

    @Override
    public String toString() {
        return "reqres{" +
                "page='" + page + '\'' +
                ", per_page='" + per_page + '\'' +
                ", total='" + total + '\'' +
                ", total_pages='" + total_pages + '\'' +
                ", reqres_data=" + reqres_data +
                '}';
    }
}

class data{

    int id;
    String name;
    int year;
    String color;
    String pantone_value;

}
