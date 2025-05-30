package com.example.findhdd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findhdd.R;

import java.util.ArrayList;
import java.util.List;

public class CheckboxAdapter extends RecyclerView.Adapter<CheckboxAdapter.ViewHolder> {

    private List<String> items;
    private List<String> selectedItems = new ArrayList<>();

    public CheckboxAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.checkBox.setText(item);
        holder.checkBox.setChecked(selectedItems.contains(item));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}