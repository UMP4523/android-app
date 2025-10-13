package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// [수정] ActivityMainBinding -> ActivityViewReastaurantBinding
import com.example.myapplication.databinding.ActivityViewReastaurantBinding;
import com.example.myapplication.databinding.DialogReservationBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activityrestaurant extends AppCompatActivity implements HomeFragment.OnDateSelectedListener {

    // [수정] ActivityMainBinding -> ActivityViewReastaurantBinding
    private ActivityViewReastaurantBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    private int personCount = 2;
    private TextView selectedTimeSlot = null;
    private String selectedTime = "";
    private String selectedDate = "";

    private ArrayList<ReviewData> reviewList = new ArrayList<>();
    private ActivityResultLauncher<Intent> addReviewResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [수정] ActivityMainBinding -> ActivityViewReastaurantBinding
        binding = ActivityViewReastaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("홈"); break;
                case 1: tab.setText("소식"); break;
                case 2: tab.setText("메뉴"); break;
                case 3: tab.setText("사진"); break;
                case 4: tab.setText("리뷰"); break;
                case 5: tab.setText("매장정보"); break;
            }
        }).attach();

        // [수정] binding.bottomBar.buttonReserve 로 접근
        binding.bottomBar.buttonReserve.setOnClickListener(v -> showReservationDialog());

        setupAddReviewResultLauncher();
    }

    public ArrayList<ReviewData> getReviewList() {
        return reviewList;
    }

    public void launchReviewActivity() {
        Intent intent = new Intent(this, ReviewActivity.class);
        addReviewResultLauncher.launch(intent);
    }

    @Override
    public void onDateSelected(String date) {
        this.selectedDate = date;
    }

    private void setupAddReviewResultLauncher() {
        addReviewResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ReviewData newReview = (ReviewData) data.getSerializableExtra("newReview");
                            if (newReview != null) {
                                reviewList.add(0, newReview);

                                // [수정] binding.storeInfoContainer 를 통해 접근
                                updateRatingInfo(binding.storeInfoContainer.textRating, binding.storeInfoContainer.textReviewCount);
                                
                                Toast.makeText(this, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                viewPagerAdapter.notifyItemChanged(4);
                            }
                        }
                    }
                }
        );
    }

    private void showReservationDialog() {
        DialogReservationBinding dialogBinding = DialogReservationBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());
        dialogBinding.textSelectedDate.setText("날짜: " + selectedDate);

        setupTimeSlots(dialogBinding.timeSlotsContainer);
        dialogBinding.textPersonCount.setText(personCount + " 명");
        dialogBinding.buttonMinus.setOnClickListener(v -> {
            if (personCount > 1) {
                personCount--;
                dialogBinding.textPersonCount.setText(personCount + " 명");
            }
        });
        dialogBinding.buttonPlus.setOnClickListener(v -> {
            personCount++;
            dialogBinding.textPersonCount.setText(personCount + " 명");
        });

        builder.setPositiveButton("확인", (dialog, which) -> {
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String message = personCount + "명, " + selectedTime + "으로 예약 요청되었습니다.";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void setupTimeSlots(android.widget.LinearLayout container) {
        List<String> timeSlots = Arrays.asList("17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00");
        selectedTimeSlot = null;
        selectedTime = "";
        container.removeAllViews();
        for (String time : timeSlots) {
            TextView timeSlotView = (TextView) getLayoutInflater().inflate(R.layout.item_timeslot, container, false);
            timeSlotView.setText(time);
            timeSlotView.setOnClickListener(v -> {
                if (selectedTimeSlot != null) {
                    selectedTimeSlot.setSelected(false);
                }
                timeSlotView.setSelected(true);
                selectedTimeSlot = timeSlotView;
                selectedTime = time;
            });
            container.addView(timeSlotView);
        }
    }
    
    // [수정] UI 요소를 직접 받아서 처리하도록 변경
    private void updateRatingInfo(TextView textRating, TextView textReviewCount) {
        if (reviewList.isEmpty()) {
            textRating.setText("0.0");
            textReviewCount.setText("리뷰 0개");
            return;
        }
        float totalRating = 0;
        for (ReviewData review : reviewList) {
            totalRating += review.getRating();
        }
        float averageRating = totalRating / reviewList.size();
        textRating.setText(String.format("%.1f", averageRating));
        textReviewCount.setText("리뷰 " + reviewList.size() + "개");
    }
}

// 리뷰 데이터 클래스
class ReviewData implements Serializable {
    private float rating;
    private String content;
    private String photoUri;

    public ReviewData(float rating, String content, String photoUri) {
        this.rating = rating;
        this.content = content;
        this.photoUri = photoUri;
    }

    public float getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
