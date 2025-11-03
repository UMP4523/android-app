package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.DialogReservationBinding;
import com.example.myapplication.databinding.ItemTimeslotBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnDateSelectedListener {

    private ActivityMainBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    // --- 테이블 구성 ---
    private static final int TABLES_2P = 10;  // 2인용 테이블
    private static final int TABLES_4P = 15;  // 4인용 테이블

    // --- 예약 관련 변수 ---
    private int personCount = 2;
    private View selectedTimeSlotView = null;
    private String selectedTime = "";
    private String selectedDate = "";

    private List<Reservation> reservations = new ArrayList<>();

    // --- 리뷰 관련 변수 ---
    private ArrayList<ReviewData> reviewList = new ArrayList<>();
    private ActivityResultLauncher<Intent> addReviewResultLauncher;

    // --- UI 참조 ---
    private LinearLayout storeInfoContainer;
    private TextView textRating;
    private TextView textReviewCount;
    private Button buttonReserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupVirtualData();

        // 매장 정보
        storeInfoContainer = findViewById(R.id.store_info_container);
        textRating = storeInfoContainer.findViewById(R.id.text_rating);
        textReviewCount = storeInfoContainer.findViewById(R.id.text_review_count);
        buttonReserve = findViewById(R.id.button_reserve);

        buttonReserve.setOnClickListener(v -> showReservationDialog());

        // 탭 연결
        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("홈"); break;
                        case 1: tab.setText("소식"); break;
                        case 2: tab.setText("메뉴"); break;
                        case 3: tab.setText("사진"); break;
                        case 4: tab.setText("리뷰"); break;
                        case 5: tab.setText("매장정보"); break;
                    }
                }).attach();

        setupAddReviewResultLauncher();
    }

    // --- 더미 데이터 ---
    private void setupVirtualData() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd (E)", Locale.KOREAN);
        selectedDate = sdf.format(cal.getTime());

        reservations.add(new Reservation(selectedDate, "18:00", 4));
        reservations.add(new Reservation(selectedDate, "12:00", 2));
    }

    // --- 예약 다이얼로그 표시 ---
    private void showReservationDialog() {
        DialogReservationBinding dialogBinding = DialogReservationBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());

        dialogBinding.textSelectedDate.setText("날짜: " + selectedDate);
        dialogBinding.textPersonCount.setText(personCount + " 명");

        setupTimeSlots(dialogBinding.timeSlotsContainer);

        dialogBinding.buttonMinus.setOnClickListener(v -> {
            if (personCount > 1) {
                personCount--;
                dialogBinding.textPersonCount.setText(personCount + " 명");
                setupTimeSlots(dialogBinding.timeSlotsContainer);
            }
        });

        dialogBinding.buttonPlus.setOnClickListener(v -> {
            personCount++;
            dialogBinding.textPersonCount.setText(personCount + " 명");
            setupTimeSlots(dialogBinding.timeSlotsContainer);
        });

        builder.setPositiveButton("확인", (dialog, which) -> {
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isAlreadyReserved(selectedDate, selectedTime)) {
                Toast.makeText(this, "이미 예약된 시간입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getRemainingTables(selectedDate, selectedTime, personCount) <= 0) {
                Toast.makeText(this, "예약이 마감되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            reservations.add(new Reservation(selectedDate, selectedTime, personCount));
            Toast.makeText(this, personCount + "명 " + selectedTime + " 예약 완료", Toast.LENGTH_SHORT).show();
            setupTimeSlots(dialogBinding.timeSlotsContainer);
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // --- 타임슬롯 세팅 ---
    private void setupTimeSlots(LinearLayout container) {
        container.removeAllViews();
        selectedTimeSlotView = null;
        selectedTime = "";

        TextView lunchLabel = new TextView(this);
        lunchLabel.setText("🍱 점심 시간대");
        lunchLabel.setTextSize(18);
        lunchLabel.setPadding(0, 10, 0, 10);
        container.addView(lunchLabel);

        List<String> lunch = Arrays.asList("11:00", "12:00", "13:00", "14:00", "15:00");
        addTimeButtons(container, lunch);

        TextView dinnerLabel = new TextView(this);
        dinnerLabel.setText("🍽 저녁 시간대");
        dinnerLabel.setTextSize(18);
        dinnerLabel.setPadding(0, 30, 0, 10);
        container.addView(dinnerLabel);

        List<String> dinner = Arrays.asList("17:00", "18:00", "19:00", "20:00", "21:00");
        addTimeButtons(container, dinner);
    }

    // --- 버튼 생성 ---
    private void addTimeButtons(LinearLayout container, List<String> times) {
        for (String time : times) {
            ItemTimeslotBinding slotBinding = ItemTimeslotBinding.inflate(LayoutInflater.from(this), container, false);
            View slotView = slotBinding.getRoot();

            int remain = getRemainingTables(selectedDate, time, personCount);
            if (remain > 0) {
                slotBinding.textAvailability.setText("남은 테이블: " + remain + "개");
                slotView.setEnabled(true);
                slotView.setOnClickListener(v -> {
                    if (selectedTimeSlotView != null) selectedTimeSlotView.setSelected(false);
                    slotView.setSelected(true);
                    selectedTimeSlotView = slotView;
                    selectedTime = time;
                });
            } else {
                slotBinding.textAvailability.setText("마감");
                slotView.setEnabled(false);
            }

            slotBinding.textTime.setText(time);
            container.addView(slotView);
        }
    }

    // --- 남은 테이블 계산 ---
    private int getRemainingTables(String date, String time, int people) {
        int totalTables = (people <= 2) ? TABLES_2P : TABLES_4P;
        int used = 0;

        for (Reservation r : reservations) {
            if (r.date.equals(date) && isTimeConflict(r.time, time)) {
                int needed = getTableCountForPeople(r.partySize);
                used += needed;
            }
        }

        return totalTables - getTableCountForPeople(people) - used < 0 ? 0 :
                totalTables - used;
    }

    // ✅ 인원수에 따른 테이블 개수 계산
    private int getTableCountForPeople(int people) {
        if (people <= 2) return 1;
        if (people <= 4) return 1;
        if (people <= 8) return 2;
        if (people <= 12) return 3;
        return 4;
    }

    private boolean isAlreadyReserved(String date, String time) {
        for (Reservation r : reservations) {
            if (r.date.equals(date) && r.time.equals(time)) return true;
        }
        return false;
    }

    private boolean isTimeConflict(String t1, String t2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            return Math.abs(d1.getTime() - d2.getTime()) < 60 * 60 * 1000;
        } catch (ParseException e) {
            return true;
        }
    }

    // --- 리뷰 관련 ---
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
                                updateRatingInfo();
                                Toast.makeText(this, "리뷰 등록 완료", Toast.LENGTH_SHORT).show();
                                viewPagerAdapter.notifyItemChanged(4);
                            }
                        }
                    }
                }
        );
    }

    private void updateRatingInfo() {
        if (reviewList.isEmpty()) {
            textRating.setText("0.0");
            textReviewCount.setText("리뷰 0개");
            return;
        }
        float totalRating = 0;
        for (ReviewData review : reviewList) totalRating += review.getRating();
        float avg = totalRating / reviewList.size();
        textRating.setText(String.format("%.1f", avg));
        textReviewCount.setText("리뷰 " + reviewList.size() + "개");
    }

    @Override
    public void onDateSelected(String date) { this.selectedDate = date; }

    // ✅ ReviewFragment에서 접근 가능하도록 메서드 추가
    public ArrayList<ReviewData> getReviewList() {
        return reviewList;
    }

    public void launchReviewActivity() {
        Intent intent = new Intent(this, ReviewActivity.class);
        addReviewResultLauncher.launch(intent);
    }
}

// --- 데이터 클래스 ---
class Reservation {
    String date;
    String time;
    int partySize;
    public Reservation(String date, String time, int partySize) {
        this.date = date;
        this.time = time;
        this.partySize = partySize;
    }
}

class ReviewData implements Serializable {
    private float rating;
    private String content;
    private String photoUri;
    public ReviewData(float rating, String content, String photoUri) {
        this.rating = rating;
        this.content = content;
        this.photoUri = photoUri;
    }
    public float getRating() { return rating; }
    public String getContent() { return content; }
    public String getPhotoUri() { return photoUri; }
}
