package com.example.universe.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.universe.databinding.FragmentMenuBinding;
import com.example.universe.ui.BaseFragment;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private MenuViewModel viewModel;
    private MenuAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        adapter = new MenuAdapter();

        binding.recyclerMeals.setAdapter(adapter);
        binding.recyclerMeals.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        viewModel.getMealList().observe(getViewLifecycleOwner(), items -> adapter.setItems(items));

        viewModel.getTotalCaloriesText().observe(getViewLifecycleOwner(), calText ->
                binding.textTotalCalories.setText(calText)
        );

        viewModel.getDateText().observe(getViewLifecycleOwner(), dateText ->
                binding.textDate.setText(dateText)
        );

        binding.buttonPrevDay.setOnClickListener(v -> viewModel.previousDay());
        binding.buttonNextDay.setOnClickListener(v -> viewModel.nextDay());

        viewModel.loadMenuForToday(requireContext());

        return view;
    }
}
