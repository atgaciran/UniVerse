package com.example.universe.ui.course_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnItemDeleteListener onItemDeleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClicked(Course course);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    public void setCourseList(List<Course> list) {
        this.courseList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.name.setText(course.getName());
        holder.time.setText(course.getStartTime() + " - " + course.getEndTime());

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemDeleteListener != null) {
                onItemDeleteListener.onDeleteClicked(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView name, time;
        ImageButton deleteButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_course_name);
            time = itemView.findViewById(R.id.text_course_time);
            deleteButton = itemView.findViewById(R.id.delete_bin_button);
        }
    }
}
