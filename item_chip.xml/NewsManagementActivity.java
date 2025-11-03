package com.example.myapplication3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NewsManagementActivity extends AppCompatActivity {

    private LinearLayout newsListContainer;
    private Button buttonAddNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_management);

        newsListContainer = findViewById(R.id.news_list_container);
        buttonAddNews = findViewById(R.id.button_add_news);

        // 예시: 새로운 소식 추가 기능
        buttonAddNews.setOnClickListener(v -> addNewsItem("이벤트 소식", "신규 메뉴 출시!"));
    }

    // 새 소식을 동적으로 추가
    private void addNewsItem(String title, String content) {
        LinearLayout newsItem = new LinearLayout(this);
        newsItem.setOrientation(LinearLayout.VERTICAL);
        newsItem.setPadding(16, 16, 16, 16);

        TextView titleView = new TextView(this);
        titleView.setText("제목: " + title);
        titleView.setTextSize(18);

        TextView contentView = new TextView(this);
        contentView.setText("내용: " + content);
        contentView.setTextSize(14);

        newsItem.addView(titleView);
        newsItem.addView(contentView);

        newsListContainer.addView(newsItem);
    }
}
