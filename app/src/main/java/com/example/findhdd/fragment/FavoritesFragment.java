package com.example.findhdd.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.HardDriveApi;
import com.example.findhdd.dialog.EditHardDriveDialog;
import com.example.findhdd.adapter.HardDriveAdapter;
import com.example.findhdd.activity.MainActivity;
import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.api.ApiMessage;
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
    private HardDriveApi hardDriveApi;
    private ProgressBar progressBar;
    private LinearLayout emptyPlaceholder;
    private Set<Long> favoriteIds = new HashSet<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        emptyPlaceholder = view.findViewById(R.id.empty_placeholder);
        recyclerView = view.findViewById(R.id.favorites_recycler_view);
        progressBar = view.findViewById(R.id.favorites_progress_bar);

        PreferencesManager prefs = new PreferencesManager(requireContext());
        String username = prefs.getUsername();
        String password = prefs.getPassword();
        boolean isAdmin = prefs.isAdmin();

        adapter = new HardDriveAdapter(isAdmin);
        adapter.setActionListener(new HardDriveAdapter.ActionListener() {
            @Override
            public void onFavoriteToggled(HardDriveDTO hdd, boolean isFavorite) {
                toggleFavorite(hdd, isFavorite);
            }

            @Override
            public void onDetailsClick(HardDriveDTO hdd) {
            }

            @Override
            public void onEdit(HardDriveDTO hdd) {
                showEditDialog(hdd);
            }

            @Override
            public void onDelete(HardDriveDTO hdd) {
                if (isAdmin) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Удаление")
                            .setMessage("Удалить диск \"" + hdd.getBrand() + " " + hdd.getModel() + "\"?")
                            .setPositiveButton("Удалить", (d, w) -> deleteHardDrive(hdd.getId()))
                            .setNegativeButton("Отмена", null)
                            .show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        hardDriveApi = ApiClient.getClient(username, password).create(HardDriveApi.class);
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
                            Toast.makeText(requireContext(), "Не удалось загрузить избранное", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<HardDriveDTO>> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Ошибка сети: ", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), shouldBeFavorite ?
                            "Добавлено в избранное" : "Удалено из избранного", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                } else {
                    Toast.makeText(requireContext(), shouldBeFavorite ?
                                    "Ошибка при добавлении в избранное" : "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiMessage> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHardDrive(Long id) {
        hardDriveApi.delete(id).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                } else {
                    Toast.makeText(getContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(getContext(), "Сервер недоступен", Toast.LENGTH_SHORT).show();
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
        hardDriveApi.update(updatedHdd.getId(), updatedHdd).enqueue(new Callback<HardDriveDTO>() {
            @Override
            public void onResponse(Call<HardDriveDTO> call, Response<HardDriveDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Обновлено", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                } else {
                    Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<HardDriveDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
