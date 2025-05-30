package com.example.findhdd.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.HardDriveApi;
import com.example.findhdd.dto.HardDriveDTO;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddHardDriveDialog extends DialogFragment {
    private HardDriveApi hardDriveApi;
    private OnHardDriveAddedListener listener;

    public interface OnHardDriveAddedListener {
        void onHardDriveAdded();
    }

    public void setOnHardDriveAddedListener(OnHardDriveAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager prefs = new PreferencesManager(requireContext());
        hardDriveApi = ApiClient.getClient(prefs.getUsername(), prefs.getPassword()).create(HardDriveApi.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_hard_drive, null);
        builder.setView(dialogView)
                .setTitle("Добавить жёсткий диск")
                .setPositiveButton("Добавить", null)
                .setNegativeButton("Отмена", (d, which) -> d.dismiss());

        // Настройка адаптеров для выпадающих списков
        setupDropdownAdapters(dialogView);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                HardDriveDTO dto = collectHardDriveFromDialog(dialogView);
                if (dto == null) {
                    Toast.makeText(getContext(), "Заполните все поля корректно", Toast.LENGTH_SHORT).show();
                    return;
                }

                hardDriveApi.create(dto).enqueue(new Callback<HardDriveDTO>() {
                    @Override
                    public void onResponse(Call<HardDriveDTO> call, Response<HardDriveDTO> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Диск добавлен", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onHardDriveAdded();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HardDriveDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        return dialog;
    }

    private void setupDropdownAdapters(View dialogView) {
        MaterialAutoCompleteTextView inputType = dialogView.findViewById(R.id.inputType);
        MaterialAutoCompleteTextView inputFormFactor = dialogView.findViewById(R.id.inputFormFactor);
        MaterialAutoCompleteTextView inputPurpose = dialogView.findViewById(R.id.inputPurpose);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new String[]{"SSD", "HDD"});
        ArrayAdapter<String> formFactorAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new String[]{"2.5\"", "3.5\"", "M.2", "PCIe"});
        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getUniquePurposes());

        inputType.setAdapter(typeAdapter);
        inputFormFactor.setAdapter(formFactorAdapter);
        inputPurpose.setAdapter(purposeAdapter);
    }

    private HardDriveDTO collectHardDriveFromDialog(View dialogView) {
        try {
            String brand = ((TextInputEditText) dialogView.findViewById(R.id.inputBrand)).getText().toString().trim();
            String model = ((TextInputEditText) dialogView.findViewById(R.id.inputModel)).getText().toString().trim();
            String type = ((MaterialAutoCompleteTextView) dialogView.findViewById(R.id.inputType)).getText().toString().trim();
            String capacityStr = ((TextInputEditText) dialogView.findViewById(R.id.inputCapacity)).getText().toString().trim();
            String formFactor = ((MaterialAutoCompleteTextView) dialogView.findViewById(R.id.inputFormFactor)).getText().toString().trim();
            String readSpeedStr = ((TextInputEditText) dialogView.findViewById(R.id.inputReadSpeed)).getText().toString().trim();
            String writeSpeedStr = ((TextInputEditText) dialogView.findViewById(R.id.inputWriteSpeed)).getText().toString().trim();
            String purpose = ((MaterialAutoCompleteTextView) dialogView.findViewById(R.id.inputPurpose)).getText().toString().trim();
            String interfaceType = ((TextInputEditText) dialogView.findViewById(R.id.inputInterfaceType)).getText().toString().trim();
            String powerStr = ((TextInputEditText) dialogView.findViewById(R.id.inputPower)).getText().toString().trim();
            String warranty = ((TextInputEditText) dialogView.findViewById(R.id.inputWarranty)).getText().toString().trim();
            String lengthStr = ((TextInputEditText) dialogView.findViewById(R.id.inputLength)).getText().toString().trim();
            String widthStr = ((TextInputEditText) dialogView.findViewById(R.id.inputWidth)).getText().toString().trim();
            String heightStr = ((TextInputEditText) dialogView.findViewById(R.id.inputHeight)).getText().toString().trim();
            String weightStr = ((TextInputEditText) dialogView.findViewById(R.id.inputWeight)).getText().toString().trim();
            String description = ((TextInputEditText) dialogView.findViewById(R.id.inputDescription)).getText().toString().trim();
            String priceStr = ((TextInputEditText) dialogView.findViewById(R.id.inputPrice)).getText().toString().trim();

            if (brand.isEmpty() || model.isEmpty() || type.isEmpty() || capacityStr.isEmpty() || formFactor.isEmpty()
                    || readSpeedStr.isEmpty() || writeSpeedStr.isEmpty() || purpose.isEmpty()
                    || interfaceType.isEmpty() || powerStr.isEmpty() || warranty.isEmpty()
                    || lengthStr.isEmpty() || widthStr.isEmpty() || heightStr.isEmpty()
                    || weightStr.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                return null;
            }

            HardDriveDTO dto = new HardDriveDTO();
            dto.setBrand(brand);
            dto.setModel(model);
            dto.setType(type);
            dto.setCapacity(Integer.parseInt(capacityStr));
            dto.setFormFactor(formFactor);
            dto.setReadSpeed(Integer.parseInt(readSpeedStr));
            dto.setWriteSpeed(Integer.parseInt(writeSpeedStr));
            dto.setPurpose(purpose);
            dto.setInterfaceType(interfaceType);
            dto.setPowerConsumption(Double.parseDouble(powerStr));
            dto.setWarranty(warranty);
            dto.setLength(Double.parseDouble(lengthStr));
            dto.setWidth(Double.parseDouble(widthStr));
            dto.setHeight(Double.parseDouble(heightStr));
            dto.setWeight(Integer.parseInt(weightStr));
            dto.setDescription(description);
            dto.setPrice(Double.parseDouble(priceStr));

            return dto;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<String> getUniquePurposes() {
        return Arrays.asList(
                "для ноутбука", "для настольного компьютера",
                "для настольного компьютера и ноутбука",
                "для сервера", "для NAS", "для систем видеонаблюдения"
        );
    }
}