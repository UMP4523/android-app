package com.example.myapplication;

public class NewsItem {
    private boolean isSignature;
    private String title;
    private String content;
    private String date;

    public NewsItem(boolean isSignature, String title, String content, String date) {
        this.isSignature = isSignature;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public boolean isSignature() { return isSignature; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}
