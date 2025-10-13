package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.databinding.ItemMenuEntryBinding;
import com.example.myapplication.databinding.ItemMenuHeaderBinding;
import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MenuItem> menuList;

    public MenuAdapter(ArrayList<MenuItem> menuList) {
        this.menuList = menuList;
    }

    @Override
    public int getItemViewType(int position) {
        return menuList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MenuItem.TYPE_HEADER) {
            ItemMenuHeaderBinding binding = ItemMenuHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(binding);
        } else {
            ItemMenuEntryBinding binding = ItemMenuEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EntryViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof EntryViewHolder) {
            ((EntryViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    // 헤더를 위한 ViewHolder
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ItemMenuHeaderBinding binding;
        public HeaderViewHolder(ItemMenuHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(MenuItem item) {
            binding.textHeaderTitle.setText(item.headerTitle);
            binding.textPriceRange.setText(item.priceRange);
        }
    }

    // 메뉴 항목을 위한 ViewHolder
    static class EntryViewHolder extends RecyclerView.ViewHolder {
        private ItemMenuEntryBinding binding;
        public EntryViewHolder(ItemMenuEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(MenuItem item) {
            binding.textMenuName.setText(item.itemName + " " + item.itemStandard);
            binding.textMenuDescription.setText(item.itemDescription);
            binding.textMenuPrice.setText(item.itemPrice);
            binding.imageMenuPhoto.setImageResource(item.imageResId);

            if (item.isSignature) {
                binding.tagSignature.setVisibility(View.VISIBLE);
            } else {
                binding.tagSignature.setVisibility(View.GONE);
            }
        }
    }
}
