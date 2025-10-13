package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myapplication.databinding.FragmentMenuBinding;
import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 스크린샷과 유사한 데이터 생성
        ArrayList<MenuItem> menuList = new ArrayList<>();
        // 세트 메뉴
        menuList.add(new MenuItem("세트 메뉴", "155,000 - 165,000원"));
        menuList.add(new MenuItem("등심세트", "(2인기준)", "맛있게 숙성된 등심과 두가지 부위", "155,000원", R.drawable.img_menu1, true));
        menuList.add(new MenuItem("특수부위세트", "(2인기준)", "그날 맛있는 소의 특수부위 3가지", "165,000원", R.drawable.img_menu2, true));

        // 구이 메뉴
        menuList.add(new MenuItem("구이 메뉴", "45,000 - 55,000원"));
        menuList.add(new MenuItem("등심", "", "", "45,000원", 0, false));
        menuList.add(new MenuItem("안심", "", "", "55,000원", 0, false));

        MenuAdapter adapter = new MenuAdapter(menuList);

        // [오타 수정] recycleViewMenu -> recyclerViewMenu
        binding.recyclerViewMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMenu.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
