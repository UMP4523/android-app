package kr.ac.doowon.makesalfapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyInfoActivity extends AppCompatActivity {

    // --- UI ---
    private CircleImageView ivProfileImage;
    private TextView tvNickname;
    private ImageView ivEditNickname;
    private Button btnRegisterAsOwner;
    private BottomNavigationView bottomNav;

    // --- Firebase ---
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private DocumentReference userDocRef;

    // --- 갤러리 런처 ---
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = auth.getCurrentUser();

        initializeViews(); // UI 연결
        setupBottomNavigation(); // 하단 네비 설정

        if (currentUser != null) {
            userDocRef = firestore.collection("users").document(currentUser.getUid());
            loadUserData(); // 사용자 정보 로딩
            setupClickListeners(); // 클릭 리스너 설정
            setupGalleryLauncher(); // 갤러리 런처 설정
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvNickname = findViewById(R.id.tvNickname);
        ivEditNickname = findViewById(R.id.ivEditNickname);
        btnRegisterAsOwner = findViewById(R.id.btnRegisterAsOwner);
        bottomNav = findViewById(R.id.bottom_navigation2);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_myinfo);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(MyInfoActivity.this, Main2Activity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_reservations) {
                // 예약 화면 이동 예시
                return true;
            } else if (itemId == R.id.nav_myinfo) {
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                String userType = documentSnapshot.getString("accountType");

                if (username != null && !username.isEmpty()) {
                    tvNickname.setText(username);
                } else {
                    tvNickname.setText("닉네임 없음");
                }

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this).load(profileImageUrl).into(ivProfileImage);
                } else {
                    ivProfileImage.setImageResource(R.drawable.default_profile_image);
                }

                // 사용자 타입에 따라 사업자 등록 버튼 표시
                if ("customer".equals(userType)) {
                    btnRegisterAsOwner.setVisibility(View.VISIBLE);
                } else {
                    btnRegisterAsOwner.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "정보 로딩 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        // 프로필 이미지 클릭 -> 갤러리 열기
        ivProfileImage.setOnClickListener(v -> openGallery());

        // 닉네임 수정
        ivEditNickname.setOnClickListener(v -> showEditNicknameDialog());

        // [추가] 사업자 등록 신청 버튼 클릭 -> BusinessRegistrationActivity 이동
        btnRegisterAsOwner.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfoActivity.this, BusinessRegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadProfileImage();
                        }
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadProfileImage() {
        if (selectedImageUri == null || currentUser == null) return;

        Toast.makeText(this, "프로필 사진 업로드 중...", Toast.LENGTH_SHORT).show();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + currentUser.getUid() + "/profile.jpg");

        profileImageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            userDocRef.update("profileImageUrl", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MyInfoActivity.this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                        Glide.with(MyInfoActivity.this).load(imageUrl).into(ivProfileImage);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(MyInfoActivity.this, "Firestore 업데이트 실패", Toast.LENGTH_SHORT).show());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "사진 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showEditNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("닉네임 변경");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_nickname, null);
        final EditText input = dialogView.findViewById(R.id.etNewNickname);
        input.setText(tvNickname.getText());
        builder.setView(dialogView);

        builder.setPositiveButton("변경", (dialog, which) -> {
            String newNickname = input.getText().toString().trim();
            if (!TextUtils.isEmpty(newNickname) && !newNickname.equals(tvNickname.getText().toString())) {
                updateNickname(newNickname);
            } else if (TextUtils.isEmpty(newNickname)) {
                Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateNickname(String newNickname) {
        userDocRef.update("username", newNickname)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MyInfoActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    tvNickname.setText(newNickname);
                })
                .addOnFailureListener(e -> Toast.makeText(MyInfoActivity.this, "닉네임 변경 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
