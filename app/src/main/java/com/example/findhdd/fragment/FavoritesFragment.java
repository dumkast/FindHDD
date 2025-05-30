package com.example.findhdd.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findhdd.dialog.EditHardDriveDialog;
import com.example.findhdd.adapter.HardDriveAdapter;
import com.example.findhdd.HardDriveRepository;
import com.example.findhdd.activity.MainActivity;
import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.ViewUtils;
import com.example.findhdd.api.ApiMessage;
import com.example.findhdd.api.HardDriveApi;
import com.example.findhdd.dto.HardDriveDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private HardDriveAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout emptyPlaceholder;

    private HardDriveApi hardDriveApi;
    private List<HardDriveDTO> hardDrives = new ArrayList<>();
    private Set<Long> favoriteIds = new HashSet<>();
    private HardDriveRepository hardDriveRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        hardDriveRepository = new HardDriveRepository(requireContext());
        emptyPlaceholder = view.findViewById(R.id.empty_placeholder);
        recyclerView = view.findViewById(R.id.favorites_recycler_view);
        progressBar = view.findViewById(R.id.favorites_progress_bar);

        PreferencesManager prefs = new PreferencesManager(requireContext());
        boolean isAdmin = prefs.isAdmin();

        adapter = new HardDriveAdapter(new ArrayList<>(), this::toggleFavorite, isAdmin, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (isAdmin) {
            adapter.setAdminActionListener(new HardDriveAdapter.AdminActionListener() {
                @Override
                public void onEdit(HardDriveDTO hdd) {
                    showEditDialog(hdd);
                }

                @Override
                public void onDelete(HardDriveDTO hdd) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Удаление")
                            .setMessage("Удалить диск \"" + hdd.getBrand() + " " + hdd.getModel() + "\"?")
                            .setPositiveButton("Удалить", (dialog, which) -> deleteHardDrive(hdd.getId()))
                            .setNegativeButton("Отмена", null)
                            .show();
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadFavorites();

        return view;
    }
    private void loadFavorites() {
        progressBar.setVisibility(View.VISIBLE);

        ((MainActivity) requireActivity()).getUserApi().getFavorites()
                .enqueue(new Callback<List<HardDriveDTO>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<HardDriveDTO>> call,
                                           @NonNull Response<List<HardDriveDTO>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<HardDriveDTO> favorites = response.body();
                            favoriteIds.clear();
                            for (HardDriveDTO hdd : favorites) {
                                favoriteIds.add(hdd.getId());
                            }
                            adapter.updateData(favorites);
                            adapter.setFavoriteIds(favoriteIds);
                            updateEmptyState(favorites.isEmpty());
                        } else {
                            ViewUtils.showErrorToast(getContext(), "Не удалось загрузить избранное");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<HardDriveDTO>> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        ViewUtils.showErrorToast(getContext(), "Ошибка сети: " + t.getMessage());
                    }
                });
    }

    private void updateEmptyState(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyPlaceholder.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void toggleFavorite(HardDriveDTO hdd, boolean shouldBeFavorite) {
        Call<ApiMessage> call = shouldBeFavorite ?
                ((MainActivity) requireActivity()).getUserApi().addFavorite(hdd.getId()) :
                ((MainActivity) requireActivity()).getUserApi().removeFavorite(hdd.getId());

        call.enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(@NonNull Call<ApiMessage> call, @NonNull Response<ApiMessage> response) {
                if (response.isSuccessful()) {
                    if (shouldBeFavorite) favoriteIds.add(hdd.getId());
                    else favoriteIds.remove(hdd.getId());
                    adapter.setFavoriteIds(favoriteIds);
                    ViewUtils.showToast(getContext(),
                            shouldBeFavorite ? "Добавлено в избранное" : "Удалено из избранного");
                    loadFavorites();
                } else {
                    ViewUtils.showErrorToast(getContext(),
                            shouldBeFavorite ? "Ошибка при добавлении в избранное" :
                                    "Ошибка при удалении из избранного");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiMessage> call, @NonNull Throwable t) {
                ViewUtils.showErrorToast(getContext(), "Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void deleteHardDrive(Long id) {
        hardDriveRepository.deleteHardDrive(id, new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful()) {
                    ViewUtils.showToast(getContext(), "Удалено");
                    loadFavorites();
                } else {
                    ViewUtils.showErrorToast(getContext(), "Ошибка удаления");
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                ViewUtils.showErrorToast(getContext(), "Сервер недоступен");
            }
        });
    }
    private void showEditDialog(HardDriveDTO hdd) {
        EditHardDriveDialog dialog = EditHardDriveDialog.newInstance(hdd);
        dialog.setOnSaveListener(updatedHdd -> {
            updateHardDrive(updatedHdd);
        });
        dialog.show(getParentFragmentManager(), "EditHardDriveDialog");
    }


    private void updateHardDrive(HardDriveDTO updatedHdd) {
        hardDriveRepository.updateHardDrive(updatedHdd.getId(), updatedHdd, new Callback<HardDriveDTO>() {
            @Override
            public void onResponse(Call<HardDriveDTO> call, Response<HardDriveDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ViewUtils.showToast(getContext(), "Обновлено");
                    loadFavorites();
                } else {
                    ViewUtils.showErrorToast(getContext(), "Ошибка обновления");
                }
            }

            @Override
            public void onFailure(Call<HardDriveDTO> call, Throwable t) {
                ViewUtils.showErrorToast(getContext(), "Сервер недоступен");
            }
        });
    }

}
