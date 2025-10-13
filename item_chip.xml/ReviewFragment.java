package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myapplication.databinding.FragmentReviewBinding;
import java.util.ArrayList;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private ReviewAdapter adapter;
    private ArrayList<ReviewData> reviewList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        binding.fabAddReview.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).launchReviewActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            this.reviewList = ((MainActivity) getActivity()).getReviewList();
            adapter = new ReviewAdapter(reviewList);
            binding.recyclerViewReviews.setAdapter(adapter);
            updateEmptyView();
        }
    }

    private void setupRecyclerView() {
        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(reviewList);
        binding.recyclerViewReviews.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (reviewList.isEmpty()) {
            binding.recyclerViewReviews.setVisibility(View.GONE);
            binding.textNoReviews.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewReviews.setVisibility(View.VISIBLE);
            binding.textNoReviews.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
