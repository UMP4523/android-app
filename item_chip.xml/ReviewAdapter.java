package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ItemReviewBinding;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final ArrayList<ReviewData> reviewList;

    public ReviewAdapter(ArrayList<ReviewData> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemReviewBinding binding;
        private final Context context;

        public ReviewViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        public void bind(ReviewData review) {
            binding.itemRatingBar.setRating(review.getRating());
            binding.itemTextContent.setText(review.getContent());

            if (review.getPhotoUri() != null) {
                binding.itemImagePhoto.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Uri.parse(review.getPhotoUri()))
                        .into(binding.itemImagePhoto);
            } else {
                binding.itemImagePhoto.setVisibility(View.GONE);
            }
        }
    }
}
