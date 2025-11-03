package com.example.myapplication3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuManagementActivity extends AppCompatActivity {

    private LinearLayout menuListContainer;
    private Button buttonAddMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        menuListContainer = findViewById(R.id.menu_list_container);
        buttonAddMenu = findViewById(R.id.button_add_menu);

        buttonAddMenu.setOnClickListener(v -> showAddMenuDialog());
    }

    private void showAddMenuDialog() {
        // 다이얼로그 안에 LinearLayout 만들어서 EditText 2개 추가
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        final EditText inputName = new EditText(this);
        inputName.setHint("메뉴 이름");
        layout.addView(inputName);

        final EditText inputPrice = new EditText(this);
        inputPrice.setHint("가격");
        inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPrice);

        new AlertDialog.Builder(this)
                .setTitle("메뉴 추가")
                .setView(layout)
                .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = inputName.getText().toString().trim();
                        String price = inputPrice.getText().toString().trim();

                        if (!name.isEmpty() && !price.isEmpty()) {
                            addMenuItem(name, price);
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void addMenuItem(String name, String price) {
        // 새로운 메뉴 아이템 LinearLayout 생성
        LinearLayout menuItem = new LinearLayout(this);
        menuItem.setOrientation(LinearLayout.HORIZONTAL);
        menuItem.setPadding(12, 12, 12, 12);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        menuItem.setLayoutParams(params);
        menuItem.setBackground(getResources().getDrawable(R.drawable.border_rounded_grey));

        // 메뉴 이름
        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(16);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);

        // 메뉴 가격
        TextView tvPrice = new TextView(this);
        tvPrice.setText(price);
        tvPrice.setTextSize(16);

        menuItem.addView(tvName);
        menuItem.addView(tvPrice);

        menuListContainer.addView(menuItem);
    }
}
