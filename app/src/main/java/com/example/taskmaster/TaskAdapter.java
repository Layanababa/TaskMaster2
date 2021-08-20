package com.example.taskmaster;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<TaskItem> taskItems;
    private OnTaskItemClickListener listener;

    public interface OnTaskItemClickListener{
        void onItemClicked(int position);
    }

    public TaskAdapter(List<TaskItem> taskItems, OnTaskItemClickListener listener) {
        this.taskItems = taskItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder viewHolder, int position) {
        TaskItem item = taskItems.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.body.setText(item.getBody());
        viewHolder.state.setText(item.getState());
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView body;
        private TextView state;

        public ViewHolder(@NonNull  View itemView, OnTaskItemClickListener listener ) {
            super(itemView);

            title=itemView.findViewById(R.id.task_title);
            body=itemView.findViewById(R.id.task_body);
            state=itemView.findViewById(R.id.task_state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }
}
