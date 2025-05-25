package com.example.universe.ui.material;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;

import java.util.ArrayList;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<MaterialItem> materialItems = new ArrayList<>();

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialItem material = materialItems.get(position);

        String displayText = material.getType() + " – " + material.getTitle();
        holder.typeTitleTextView.setText(displayText);

        holder.fileBoxTextView.setText("Tıklayarak içeriği aç");

        holder.fileBoxTextView.setOnClickListener(v -> {
            String url = material.getFileUrl();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialItems.size();
    }

    public void updateList(List<MaterialItem> newList) {
        materialItems.clear();
        materialItems.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder {

        TextView typeTitleTextView;
        TextView fileBoxTextView;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTitleTextView = itemView.findViewById(R.id.item_type_title);
            fileBoxTextView = itemView.findViewById(R.id.item_file_box);
        }
    }
}
