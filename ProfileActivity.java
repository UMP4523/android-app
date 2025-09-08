package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE = 1;
    private ImageView imgProfile;
    private EditText etNickname, etName, etEmail;
    private Button btnChangePhoto, btnSave;
    private Uri selectedImageUri; // 선택된 이미지 경로 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        etNickname = findViewById(R.id.etNickname);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSave = findViewById(R.id.btnSave);

        // 사진 변경 버튼
        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // 저장 버튼
        btnSave.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString();
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();

            // 저장 로직 (DB, SharedPreferences 등 연결 가능)
            Toast.makeText(this, "저장 완료!\n닉네임: " + nickname + "\n이름: " + name + "\n이메일: " + email, Toast.LENGTH_SHORT).show();
        });
    }

    // 이미지 선택 후 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
