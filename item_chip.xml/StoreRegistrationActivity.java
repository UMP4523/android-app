package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivityStoreRegistrationBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class StoreRegistrationActivity extends AppCompatActivity {

    private ActivityStoreRegistrationBinding binding;
    private Uri selectedStoreImageUri = null;

    // Gallery result launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedStoreImageUri = result.getData().getData();
                    // Load selected image into ImageView using Glide
                    Glide.with(this)
                            .load(selectedStoreImageUri)
                            .into(binding.imageStorePhoto);
                    binding.imageStorePhoto.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up button click listeners
        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        // Add/Change store photo button
        binding.buttonAddStorePhoto.setOnClickListener(v -> openGallery());

        // Manage news button (Placeholder)
        binding.buttonManageNews.setOnClickListener(v ->
                Toast.makeText(this, "소식 관리 기능 준비 중", Toast.LENGTH_SHORT).show());

        // Manage menu button (Placeholder)
        binding.buttonManageMenu.setOnClickListener(v ->
                Toast.makeText(this, "메뉴 관리 기능 준비 중", Toast.LENGTH_SHORT).show());

        // Manage tables button (Placeholder)
        binding.buttonManageTables.setOnClickListener(v ->
                Toast.makeText(this, "테이블 설정 기능 준비 중", Toast.LENGTH_SHORT).show());

        // Save store info button
        binding.buttonSaveStoreInfo.setOnClickListener(v -> saveStoreInformation());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void saveStoreInformation() {
        // --- Collect data from input fields ---
        String storeName = binding.editStoreName.getText().toString().trim();
        String storePhone = binding.editStorePhone.getText().toString().trim();
        String storeAddress = binding.editStoreAddress.getText().toString().trim();
        String storeDescription = binding.editStoreDescription.getText().toString().trim();
        String diningDurationStr = binding.editDiningDuration.getText().toString().trim();
        List<String> selectedHolidays = getSelectedHolidays();
        String photoUriString = (selectedStoreImageUri != null) ? selectedStoreImageUri.toString() : null;

        // --- Basic Validation ---
        if (storeName.isEmpty()) {
            binding.layoutStoreName.setError("가게 이름을 입력해주세요.");
            return;
        } else {
            binding.layoutStoreName.setError(null);
        }
        // TODO: Add more validation for other fields (phone format, address, duration etc.)

        int diningDuration = 0;
        try {
            if (!diningDurationStr.isEmpty()) {
                diningDuration = Integer.parseInt(diningDurationStr);
            }
        } catch (NumberFormatException e) {
            binding.layoutDiningDuration.setError("숫자만 입력해주세요.");
            return;
        }

        // --- Process the collected data (e.g., save to database) ---
        // For now, just display a confirmation message
        String holidaysText = selectedHolidays.isEmpty() ? "없음" : String.join(", ", selectedHolidays);
        String confirmationMessage = "가게 정보가 저장되었습니다.\n" +
                "이름: " + storeName + "\n" +
                "전화: " + storePhone + "\n" +
                "주소: " + storeAddress + "\n" +
                "설명: " + storeDescription + "\n" +
                "식사시간: " + diningDuration + "분\n" +
                "휴무일: " + holidaysText + "\n" +
                "사진 URI: " + (photoUriString != null ? photoUriString : "없음");

        Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show();

        // TODO: Implement actual saving logic (e.g., using Room database)
        // After saving, you might want to finish this activity
        // finish();
    }

    private List<String> getSelectedHolidays() {
        List<String> holidays = new ArrayList<>();
        for (int i = 0; i < binding.chipgroupHolidays.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipgroupHolidays.getChildAt(i);
            if (chip.isChecked()) {
                holidays.add(chip.getText().toString());
            }
        }
        return holidays;
    }
}
