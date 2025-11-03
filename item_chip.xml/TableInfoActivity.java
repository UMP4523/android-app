package kr.ac.doowon.makesalfapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TableInfoActivity extends AppCompatActivity {

    private EditText editTwoPersonTable, editFourPersonTable;
    private Button buttonSaveTableInfo;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "StorePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_table_info);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        editTwoPersonTable = findViewById(R.id.edit_two_person_table);
        editFourPersonTable = findViewById(R.id.edit_four_person_table);
        buttonSaveTableInfo = findViewById(R.id.button_save_table_info);

        loadTableInfo();

        buttonSaveTableInfo.setOnClickListener(v -> saveTableInfo());
    }

    private void saveTableInfo() {
        String twoPerson = editTwoPersonTable.getText().toString().trim();
        String fourPerson = editFourPersonTable.getText().toString().trim();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("twoPersonTable", twoPerson);
        editor.putString("fourPersonTable", fourPerson);
        editor.apply();

        Toast.makeText(this, "테이블 정보 저장 완료", Toast.LENGTH_SHORT).show();
        finish(); // 저장 후 이전 화면으로 돌아가기
    }

    private void loadTableInfo() {
        String twoPerson = sharedPreferences.getString("twoPersonTable", "0");
        String fourPerson = sharedPreferences.getString("fourPersonTable", "0");

        editTwoPersonTable.setText(twoPerson);
        editFourPersonTable.setText(fourPerson);
    }
}
