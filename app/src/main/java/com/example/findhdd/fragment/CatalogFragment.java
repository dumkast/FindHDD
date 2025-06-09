package com.example.findhdd.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findhdd.*;
import com.example.findhdd.activity.MainActivity;
import com.example.findhdd.adapter.HardDriveAdapter;
import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.ApiMessage;
import com.example.findhdd.api.HardDriveApi;
import com.example.findhdd.dialog.AddHardDriveDialog;
import com.example.findhdd.dialog.EditHardDriveDialog;
import com.example.findhdd.dialog.FilterDialog;
import com.example.findhdd.dto.HardDriveDTO;
import com.example.findhdd.dto.HardDriveFilter;
import com.example.findhdd.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogFragment extends Fragment {

    private RecyclerView recyclerView;
    private HardDriveAdapter adapter;
    private List<HardDriveDTO> hardDrives = new ArrayList<>();
    private HardDriveApi hardDriveApi;
    private HardDriveFilter currentFilter = new HardDriveFilter();
    private Set<Long> favoriteIds = new HashSet<>();
    private LinearLayout emptyPlaceholder;
    private TextInputEditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyPlaceholder = view.findViewById(R.id.empty_placeholder);

        PreferencesManager prefs = new PreferencesManager(requireContext());
        String username = prefs.getUsername();
        String password = prefs.getPassword();
        boolean isAdmin = prefs.isAdmin();

        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        TextInputLayout searchContainer = view.findViewById(R.id.searchContainer);
        searchContainer.setEndIconOnClickListener(v -> performSearch());

        adapter = new HardDriveAdapter(isAdmin);
        adapter.setActionListener(new HardDriveAdapter.ActionListener() {
            @Override
            public void onFavoriteToggled(HardDriveDTO hdd, boolean isFavorite) {
                toggleFavorite(hdd, isFavorite);
            }

            @Override
            public void onDetailsClick(HardDriveDTO hdd) {
                showDetailsDialog(hdd);
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
        recyclerView.setAdapter(adapter);

        if (username == null || password == null) {
            Toast.makeText(getContext(), "Ошибка авторизации. Повторите вход.", Toast.LENGTH_SHORT).show();
            return view;
        }

        hardDriveApi = ApiClient.getClient(username, password).create(HardDriveApi.class);

        loadFavorites();
        updateHardDriveList();

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddHardDrive);
        fabAdd.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        if (isAdmin) {
            fabAdd.setOnClickListener(v -> showAddDialog());
        }

        view.findViewById(R.id.sortCard).setOnClickListener(v -> showSortDialog());
        view.findViewById(R.id.filterCard).setOnClickListener(v -> showFilterDialog());

        return view;
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            hardDriveApi.search(query).enqueue(new Callback<List<HardDriveDTO>>() {
                @Override
                public void onResponse(Call<List<HardDriveDTO>> call, Response<List<HardDriveDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        hardDrives = response.body();
                        adapter.updateData(hardDrives);
                        updateEmptyState();
                    } else {
                        Toast.makeText(getContext(), "Ошибка поиска", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<HardDriveDTO>> call, Throwable t) {
                    Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentFilter = new HardDriveFilter();
            updateHardDriveList();
        }
    }

    private void updateHardDriveList() {
        // Если фильтр пустой, то просто getAll, иначе filter
        Call<List<HardDriveDTO>> call = (currentFilter == null || currentFilter.isEmpty()) ?
                hardDriveApi.getAll() : hardDriveApi.filter(currentFilter);

        call.enqueue(new Callback<List<HardDriveDTO>>() {
            @Override
            public void onResponse(Call<List<HardDriveDTO>> call, Response<List<HardDriveDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hardDrives = response.body();
                    adapter.updateData(hardDrives);
                    updateEmptyState();
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<HardDriveDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        recyclerView.setVisibility(hardDrives.isEmpty() ? View.GONE : View.VISIBLE);
        emptyPlaceholder.setVisibility(hardDrives.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadFavorites() {
        ((MainActivity) requireActivity()).getUserApi().getFavorites()
                .enqueue(new Callback<List<HardDriveDTO>>() {
                    @Override
                    public void onResponse(Call<List<HardDriveDTO>> call, Response<List<HardDriveDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            favoriteIds.clear();
                            for (HardDriveDTO hdd : response.body()) {
                                favoriteIds.add(hdd.getId());
                            }
                            adapter.setFavoriteIds(favoriteIds);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<HardDriveDTO>> call, Throwable t) {}
                });
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
                    Toast.makeText(getContext(), shouldBeFavorite ? "Добавлено в избранное" : "Удалено из избранного", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                } else {
                    Toast.makeText(getContext(), shouldBeFavorite ? "Ошибка при добавлении в избранное" : "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiMessage> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHardDrive(Long id) {
        hardDriveApi.delete(id).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                    updateHardDriveList();
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
        dialog.setOnSaveListener(this::updateHardDrive);
        dialog.show(getParentFragmentManager(), "editHardDrive");
    }

    private void updateHardDrive(HardDriveDTO updatedHdd) {
        hardDriveApi.update(updatedHdd.getId(), updatedHdd).enqueue(new Callback<HardDriveDTO>() {
            @Override
            public void onResponse(Call<HardDriveDTO> call, Response<HardDriveDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Обновлено", Toast.LENGTH_SHORT).show();
                    updateHardDriveList();
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

    private void showSortDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_sort, null);
        RadioGroup rgSortOptions = dialogView.findViewById(R.id.rgSortOptions);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnApply = dialogView.findViewById(R.id.btnApply);

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogView).create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnApply.setOnClickListener(v -> {
            int checkedId = rgSortOptions.getCheckedRadioButtonId();
            if (checkedId == R.id.rbNone) {
                currentFilter = new HardDriveFilter(); // сброс фильтра при сортировке none
                updateHardDriveList();
            } else {
                String direction = checkedId == R.id.rbAsc ? "asc" : "desc";
                hardDriveApi.sort(direction).enqueue(new Callback<List<HardDriveDTO>>() {
                    @Override
                    public void onResponse(Call<List<HardDriveDTO>> call, Response<List<HardDriveDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            hardDrives = response.body();
                            adapter.updateData(hardDrives);
                            updateEmptyState();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при сортировке", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<HardDriveDTO>> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка сортировки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void showFilterDialog() {
        FilterDialog dialog = new FilterDialog();
        dialog.setCurrentFilter(currentFilter);
        dialog.setListener(new FilterDialog.OnFilterAppliedListener() {
            @Override
            public void onFilterApplied(HardDriveFilter filter) {
                currentFilter = filter;
                updateHardDriveList();
            }
            @Override
            public void onFilterReset() {
                currentFilter = null;
                updateHardDriveList();
            }
        });
        dialog.show(getParentFragmentManager(), "filterDialog");
    }

    private void showAddDialog() {
        AddHardDriveDialog dialog = new AddHardDriveDialog();
        dialog.setOnHardDriveAddedListener(() -> {
            Toast.makeText(getContext(), "Диск добавлен", Toast.LENGTH_SHORT).show();
            updateHardDriveList();
        });
        dialog.show(getParentFragmentManager(), "addHardDriveDialog");
    }

    private void showDetailsDialog(HardDriveDTO hdd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_hdd_details, null);
        builder.setView(dialogView);

        ImageView ivHddType = dialogView.findViewById(R.id.ivHddType);
        ivHddType.setImageResource(
                hdd.getType().equalsIgnoreCase("SSD") ?
                        R.drawable.ic_ssd : R.drawable.ic_hdd
        );

        TextView tvBrandModel = dialogView.findViewById(R.id.tvBrandModel);
        tvBrandModel.setText(hdd.getBrand() + " " + hdd.getModel());

        setupSpecView(dialogView, R.id.specType, "Тип", hdd.getType());
        setupSpecView(dialogView, R.id.specCapacity, "Объем", hdd.getCapacity() + " ГБ");
        setupSpecView(dialogView, R.id.specFormFactor, "Форм-фактор", hdd.getFormFactor());
        setupSpecView(dialogView, R.id.specPurpose, "Назначение", hdd.getPurpose());
        setupSpecView(dialogView, R.id.specSpeeds, "Скорости",
                "Чтение: " + hdd.getReadSpeed() + " МБ/с\nЗапись: " + hdd.getWriteSpeed() + " МБ/с");
        setupSpecView(dialogView, R.id.specInterface, "Интерфейс", hdd.getInterfaceType());
        setupSpecView(dialogView, R.id.specPower, "Потребление", hdd.getPowerConsumption() + " Вт");
        setupSpecView(dialogView, R.id.specDimensions, "Размеры",
                String.format(Locale.getDefault(), "%.1f × %.1f × %.1f мм",
                        hdd.getLength(), hdd.getWidth(), hdd.getHeight()));
        setupSpecView(dialogView, R.id.specWeight, "Вес", hdd.getWeight() + " г");
        setupSpecView(dialogView, R.id.specWarranty, "Гарантия", hdd.getWarranty());
        setupSpecView(dialogView, R.id.specPrice, "Цена", String.format(Locale.getDefault(), "%,.0f ₽", hdd.getPrice()));

        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);
        tvDescription.setText(hdd.getDescription() != null && !hdd.getDescription().isEmpty() ?
                hdd.getDescription() : "Описание отсутствует");

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void setupSpecView(View parent, int resId, String name, String value) {
        View specView = parent.findViewById(resId);
        if (specView != null) {
            TextView tvName = specView.findViewById(R.id.tvSpecName);
            TextView tvValue = specView.findViewById(R.id.tvSpecValue);
            if (tvName != null) tvName.setText(name);
            if (tvValue != null) tvValue.setText(value);
        }
    }
}
