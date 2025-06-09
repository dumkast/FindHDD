package com.example.findhdd.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.findhdd.R;
import com.example.findhdd.dto.HardDriveFilter;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class FilterDialog extends DialogFragment {
    @Setter
    private HardDriveFilter currentFilter = new HardDriveFilter();
    @Setter
    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(HardDriveFilter filter);
        void onFilterReset();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        // Настройка чекбоксов
        setupCheckboxLists(dialogView);

        // Настройка полей ввода
        EditText etMinPrice = dialogView.findViewById(R.id.etMinPrice);
        EditText etMaxPrice = dialogView.findViewById(R.id.etMaxPrice);
        EditText etMinCapacity = dialogView.findViewById(R.id.etMinCapacity);
        EditText etMaxCapacity = dialogView.findViewById(R.id.etMaxCapacity);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnApply).setOnClickListener(v -> {
            HardDriveFilter newFilter = new HardDriveFilter();
            newFilter.setBrands(getSelectedItems(dialogView, R.id.llBrands));
            newFilter.setTypes(getSelectedItems(dialogView, R.id.llTypes));
            newFilter.setPurposes(getSelectedItems(dialogView, R.id.llPurposes));
            newFilter.setFormFactors(getSelectedItems(dialogView, R.id.llFormFactors));

            newFilter.setMinPrice(parseDouble(etMinPrice.getText().toString()));
            newFilter.setMaxPrice(parseDouble(etMaxPrice.getText().toString()));
            newFilter.setMinCapacity(parseInt(etMinCapacity.getText().toString()));
            newFilter.setMaxCapacity(parseInt(etMaxCapacity.getText().toString()));

            if (listener != null) listener.onFilterApplied(newFilter);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnReset).setOnClickListener(v -> {
            currentFilter = new HardDriveFilter();
            if (listener != null) listener.onFilterReset();
            dialog.dismiss();
        });

        return dialog;
    }

    private void setupCheckboxLists(View dialogView) {
        if (currentFilter == null) {
            currentFilter = new HardDriveFilter();
        }
        LinearLayout llBrands = dialogView.findViewById(R.id.llBrands);
        LinearLayout llTypes = dialogView.findViewById(R.id.llTypes);
        LinearLayout llPurposes = dialogView.findViewById(R.id.llPurposes);
        LinearLayout llFormFactors = dialogView.findViewById(R.id.llFormFactors);

        HardDriveFilterHelper.setupCheckboxList(requireContext(), llBrands,
                HardDriveFilterHelper.getUniqueBrands(), currentFilter.getBrands());
        HardDriveFilterHelper.setupCheckboxList(requireContext(), llTypes,
                HardDriveFilterHelper.getUniqueTypes(), currentFilter.getTypes());
        HardDriveFilterHelper.setupCheckboxList(requireContext(), llPurposes,
                HardDriveFilterHelper.getUniquePurposes(), currentFilter.getPurposes());
        HardDriveFilterHelper.setupCheckboxList(requireContext(), llFormFactors,
                HardDriveFilterHelper.getUniqueFormFactors(), currentFilter.getFormFactors());

    }

    private List<String> getSelectedItems(View dialogView, int layoutId) {
        LinearLayout layout = dialogView.findViewById(layoutId);
        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) layout.getChildAt(i);
            if (checkBox.isChecked()) {
                selectedItems.add(checkBox.getText().toString());
            }
        }
        return selectedItems;
    }
    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}