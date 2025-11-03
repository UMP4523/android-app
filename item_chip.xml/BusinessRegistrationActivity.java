package com.example.myapplication3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BusinessRegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_BUSINESS_LICENSE = 101;
    private static final int PICK_IMAGE_REQUEST_BANK_BOOK = 102;
    private static final int PICK_IMAGE_REQUEST_ID_CARD = 103;

    private ImageView imageBusinessLicense, imageBankBook, imageIdCard;
    private Button buttonSelectBusinessLicense, buttonSelectBankBook, buttonSelectIdCard, buttonSubmitRegistration;

    private Uri uriBusinessLicense, uriBankBook, uriIdCard;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_registration);

        // 뷰 초기화
        imageBusinessLicense = findViewById(R.id.image_business_license);
        imageBankBook = findViewById(R.id.image_bank_book);
        imageIdCard = findViewById(R.id.image_id_card);

        buttonSelectBusinessLicense = findViewById(R.id.button_select_business_license);
        buttonSelectBankBook = findViewById(R.id.button_select_bank_book);
        buttonSelectIdCard = findViewById(R.id.button_select_id_card);
        buttonSubmitRegistration = findViewById(R.id.button_submit_registration);

        storageRef = FirebaseStorage.getInstance().getReference("business_registration_docs");
        databaseRef = FirebaseDatabase.getInstance().getReference("business_registrations");

        // 클릭 리스너
        buttonSelectBusinessLicense.setOnClickListener(v -> selectImage(PICK_IMAGE_REQUEST_BUSINESS_LICENSE));
        buttonSelectBankBook.setOnClickListener(v -> selectImage(PICK_IMAGE_REQUEST_BANK_BOOK));
        buttonSelectIdCard.setOnClickListener(v -> selectImage(PICK_IMAGE_REQUEST_ID_CARD));

        buttonSubmitRegistration.setOnClickListener(v -> submitRegistration());
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지 선택"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            switch (requestCode) {
                case PICK_IMAGE_REQUEST_BUSINESS_LICENSE:
                    uriBusinessLicense = selectedUri;
                    Glide.with(this).load(uriBusinessLicense).into(imageBusinessLicense);
                    break;
                case PICK_IMAGE_REQUEST_BANK_BOOK:
                    uriBankBook = selectedUri;
                    Glide.with(this).load(uriBankBook).into(imageBankBook);
                    break;
                case PICK_IMAGE_REQUEST_ID_CARD:
                    uriIdCard = selectedUri;
                    Glide.with(this).load(uriIdCard).into(imageIdCard);
                    break;
            }
        }
    }

    private void submitRegistration() {
        if (uriBusinessLicense == null || uriBankBook == null || uriIdCard == null) {
            Toast.makeText(this, "모든 필수 서류를 업로드해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadDocument(uriBusinessLicense, "business_license.jpg", success1 -> 
            uploadDocument(uriBankBook, "bank_book.jpg", success2 ->
                uploadDocument(uriIdCard, "id_card.jpg", success3 -> 
                    saveRegistrationToDatabase(success1, success2, success3)
                )
            )
        );
    }

    private void uploadDocument(Uri uri, String filename, OnUploadSuccessListener listener) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "_" + filename);
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uriDownload -> {
                    listener.onSuccess(uriDownload.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(BusinessRegistrationActivity.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveRegistrationToDatabase(String urlBusinessLicense, String urlBankBook, String urlIdCard) {
        String key = databaseRef.push().getKey();
        if (key == null) return;

        BusinessRegistration registration = new BusinessRegistration(urlBusinessLicense, urlBankBook, urlIdCard);
        databaseRef.child(key).setValue(registration)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "사업자 등록 신청 완료", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public interface OnUploadSuccessListener {
        void onSuccess(String downloadUrl);
    }

    // 모델 클래스
    public static class BusinessRegistration {
        public String businessLicenseUrl;
        public String bankBookUrl;
        public String idCardUrl;

        public BusinessRegistration() {} //
