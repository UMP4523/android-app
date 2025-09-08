// com.example.catchclone.data.Restaurant.java
package com.example.catchclone.data;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    public String name = "";
    public String intro = "";
    public double rating = 4.8;
    public int reviewCount = 0;
    public boolean lunchOpen = true;
    public boolean dinnerOpen = true;
    public String lunchPrice = "점심 11만원";
    public String dinnerPrice = "저녁 11만원";
    public String holiday = "월";
    public String phone = "";
    public boolean valet = false;
    public boolean corkage = false;
    public boolean max8 = true;
    public boolean depositFree = true;
    public List<String> photos = new ArrayList<>();
    public String notice = "";
}
