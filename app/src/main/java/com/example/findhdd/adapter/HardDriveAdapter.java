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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;

public class HardDriveAdapter extends RecyclerView.Adapter<HardDriveAdapter.HardDriveViewHolder> {

    private List<HardDriveDTO> hardDrives;
    private FavoritesToggleListener favoritesToggleListener;
    private Set<Long> favoriteIds = new HashSet<>();
    private boolean isAdmin;
    private boolean isFavoritesTab;
    @Setter
    private AdminActionListener adminActionListener;

    public interface FavoritesToggleListener {
        void onFavoriteToggled(HardDriveDTO hdd, boolean isFavorite);
    }

    public interface AdminActionListener {
        void onEdit(HardDriveDTO hdd);
        void onDelete(HardDriveDTO hdd);
    }

    // Новый упрощённый конструктор с двумя флагами
    public HardDriveAdapter(List<HardDriveDTO> hardDrives, FavoritesToggleListener favoritesToggleListener, boolean isAdmin, boolean isFavoritesTab) {
        this.hardDrives = hardDrives;
        this.favoritesToggleListener = favoritesToggleListener;
        this.isAdmin = isAdmin;
        this.isFavoritesTab = isFavoritesTab;
    }

    public void updateData(List<HardDriveDTO> newData) {
        this.hardDrives = newData;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<Long> favoriteIds) {
        this.favoriteIds = new HashSet<>(favoriteIds);
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
        HardDriveDTO hdd = hardDrives.get(position);
        holder.tvBrandModel.setText(hdd.getBrand() + " " + hdd.getModel());
        holder.tvType.setText("Тип: " + hdd.getType());
        holder.tvCapacity.setText("Объем: " + hdd.getCapacity() + " ГБ");
        holder.tvFormFactor.setText("Форм-фактор: " + hdd.getFormFactor());
        holder.tvSpeeds.setText("Скорости: Чтение " + hdd.getReadSpeed() + " МБ/с, Запись " + hdd.getWriteSpeed() + " МБ/с");

        // Новые поля:
        holder.tvPurpose.setText("Назначение: " + hdd.getPurpose()); // назначение
        holder.tvPrice.setText(String.format("%,.2f ₽", hdd.getPrice())); // цена с двумя знаками и ₽

        holder.itemView.setOnClickListener(v -> {
            if (detailsClickListener != null) {
                detailsClickListener.onDetailsClick(hdd);
            }
        });

        if (isAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (adminActionListener != null) {
                    adminActionListener.onEdit(hdd);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (adminActionListener != null) {
                    adminActionListener.onDelete(hdd);
                }
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        boolean isFavorite = favoriteIds.contains(hdd.getId());
        holder.ivFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border);

        holder.ivFavorite.setOnClickListener(v -> {
            boolean newFavoriteState = !favoriteIds.contains(hdd.getId());

            holder.ivFavorite.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                holder.ivFavorite.setImageResource(
                        newFavoriteState ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border
                );
                holder.ivFavorite.animate().alpha(1f).setDuration(150).start();
            }).start();

            if (favoritesToggleListener != null) {
                favoritesToggleListener.onFavoriteToggled(hdd, newFavoriteState);
            }

            if (newFavoriteState) {
                favoriteIds.add(hdd.getId());
            } else {
                favoriteIds.remove(hdd.getId());
            }
        });
    }

    public interface OnDetailsClickListener {
        void onDetailsClick(HardDriveDTO hdd);
    }

    private OnDetailsClickListener detailsClickListener;

    public void setOnDetailsClickListener(OnDetailsClickListener listener) {
        this.detailsClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return hardDrives.size();
    }

    static class HardDriveViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandModel, tvType, tvCapacity, tvFormFactor, tvSpeeds, tvPurpose, tvPrice;
        ImageView ivFavorite;
        ImageButton btnEdit, btnDelete;

        public HardDriveViewHolder(@NonNull View itemView) {
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
    }

}
