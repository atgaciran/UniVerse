package com.example.universe.ui.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> items = new ArrayList<>();

    public void setItems(List<MenuItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = items.get(position);

        // Kalori bilgisi varsa ekleyerek göster
        String displayText = item.getName();
        if (item.getCalories() > 0) {
            displayText += " - " + item.getCalories() + " kcal";
        }

        holder.textMealName.setText(displayText);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView textMealName;

        public MenuViewHolder(View itemView) {
            super(itemView);
            textMealName = itemView.findViewById(R.id.text_meal_name);
        }
    }
}
