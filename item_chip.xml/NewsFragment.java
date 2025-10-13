package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myapplication.databinding.FragmentNewsBinding;
import java.util.ArrayList;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 임시 데이터 생성
        ArrayList<NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsItem(
                true,
                "상견례 예약 원앙/토퍼 제공",
                "민규에서 상견례 진행하세요:)\n\n\"넓은 룸\"에서 불편함 없이 가족들과 즐거운 대화 및 식사하세요!\n직원들도 고객님들의 좋은 날 축하드리며 즐겁게 맞이하겠습니다.\n\n<상견례 예약시 원앙/토퍼 세팅>\n-기간: 상시\n-유의사항: 예약시 상견례 예약 작성 필수",
                "2025.04.23"
        ));
        newsList.add(new NewsItem(
                true,
                "그릴링 예약안내",
                "전좌석 그릴링 전문서버가 조리해주는 프라이빗 룸으로, 모임 및 단체 행사 가능하니 많은 이용 바랍니다.\n(Room Charge Free)\n\n당일 예약은 매장으로 전화 문의 주시면 \"빠르게 진행 도와드림니다\"(02-553-0331)",
                "2023.01.04"
        ));

        NewsAdapter adapter = new NewsAdapter(newsList);
        binding.recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewNews.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
