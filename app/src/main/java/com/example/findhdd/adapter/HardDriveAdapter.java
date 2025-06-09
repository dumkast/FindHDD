package com.example.findhdd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findhdd.R;
import com.example.findhdd.dto.HardDriveDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;

public class HardDriveAdapter extends RecyclerView.Adapter<HardDriveAdapter.HardDriveViewHolder> {
    private List<HardDriveDTO> hardDrives = new ArrayList<>();
    private final Set<Long> favoriteIds = new HashSet<>();
    private final boolean isAdmin;
    @Setter
    private ActionListener actionListener;

    public interface ActionListener {
        void onFavoriteToggled(HardDriveDTO hdd, boolean isFavorite);
        void onDetailsClick(HardDriveDTO hdd);
        void onEdit(HardDriveDTO hdd);
        void onDelete(HardDriveDTO hdd);
    }

    public HardDriveAdapter(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void updateData(List<HardDriveDTO> newData) {
        this.hardDrives = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<Long> ids) {
        favoriteIds.clear();
        favoriteIds.addAll(ids);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HardDriveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hard_drive, parent, false);
        return new HardDriveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HardDriveViewHolder holder, int position) {
        holder.bind(hardDrives.get(position), isAdmin, favoriteIds.contains(hardDrives.get(position).getId()), actionListener);
    }

    @Override
    public int getItemCount() {
        return hardDrives.size();
    }

    static class HardDriveViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBrandModel, tvType, tvCapacity, tvFormFactor, tvSpeeds, tvPurpose, tvPrice;
        private final ImageView ivFavorite;
        private final ImageButton btnEdit, btnDelete;

        HardDriveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandModel = itemView.findViewById(R.id.tvBrandModel);
            tvType = itemView.findViewById(R.id.tvType);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvFormFactor = itemView.findViewById(R.id.tvFormFactor);
            tvSpeeds = itemView.findViewById(R.id.tvSpeeds);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(HardDriveDTO hdd, boolean isAdmin, boolean isFavorite, ActionListener listener) {
            // Установка текстовых значений
            tvBrandModel.setText(String.format("%s %s", hdd.getBrand(), hdd.getModel()));
            tvType.setText(String.format("Тип: %s", hdd.getType()));
            tvCapacity.setText(String.format("Объем: %s ГБ", hdd.getCapacity()));
            tvFormFactor.setText(String.format("Форм-фактор: %s", hdd.getFormFactor()));
            tvSpeeds.setText(String.format("Скорости: Чтение %s МБ/с, Запись %s МБ/с", hdd.getReadSpeed(), hdd.getWriteSpeed()));
            tvPurpose.setText(String.format("Назначение: %s", hdd.getPurpose()));
            tvPrice.setText(String.format("%,.2f ₽", hdd.getPrice()));

            // Обработка кликов
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDetailsClick(hdd);
            });

            // Настройка видимости кнопок админа
            btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

            if (isAdmin) {
                btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(hdd);
                });
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(hdd);
                });
            }

            // Настройка избранного
            ivFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border);
            ivFavorite.setOnClickListener(v -> {
                boolean newState = !isFavorite;
                animateFavoriteIcon(newState);
                if (listener != null) listener.onFavoriteToggled(hdd, newState);
            });
        }

        private void animateFavoriteIcon(boolean newState) {
            ivFavorite.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                ivFavorite.setImageResource(newState ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border);
                ivFavorite.animate().alpha(1f).setDuration(150).start();
            }).start();
        }
    }
}
