package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
    private OnDateSelectedListener listener;

    private FragmentHomeBinding binding;
    private List<Button> dateButtons = new ArrayList<>();
    private Button selectedDateButton = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDateSelectedListener) {
            listener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDateSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDateButtons();
    }

    private void setupDateButtons() {
        dateButtons.add(binding.buttonDate3);
        dateButtons.add(binding.buttonDate4);
        dateButtons.add(binding.buttonDate5);

        for (Button button : dateButtons) {
            button.setOnClickListener(v -> selectDateButton(button));
        }

        if (!dateButtons.isEmpty()) {
            selectDateButton(dateButtons.get(0));
        }
    }

    private void selectDateButton(Button button) {
        if (selectedDateButton != null) {
            selectedDateButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        }
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorError)));
        selectedDateButton = button;

        if (listener != null) {
            listener.onDateSelected(button.getText().toString().split("\n")[0]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
