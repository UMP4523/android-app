package com.example.myapplication;

public class MenuItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ENTRY = 1;

    public int type; // 이 항목이 헤더인지, 메뉴인지 구분

    // 헤더용 데이터
    public String headerTitle;
    public String priceRange;

    // 메뉴 항목용 데이터
    public String itemName;
    public String itemStandard; // 예: (2인기준)
    public String itemDescription;
    public String itemPrice;
    public int imageResId; // 이미지 리소스 ID
    public boolean isSignature; // '대표' 메뉴 여부

    // 헤더 생성자
    public MenuItem(String headerTitle, String priceRange) {
        this.type = TYPE_HEADER;
        this.headerTitle = headerTitle;
        this.priceRange = priceRange;
    }

    // 메뉴 항목 생성자
    public MenuItem(String itemName, String itemStandard, String itemDescription, String itemPrice, int imageResId, boolean isSignature) {
        this.type = TYPE_ENTRY;
        this.itemName = itemName;
        this.itemStandard = itemStandard;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.imageResId = imageResId;
        this.isSignature = isSignature;
    }
}
