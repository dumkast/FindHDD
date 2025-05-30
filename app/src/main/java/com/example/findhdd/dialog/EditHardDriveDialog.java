package com.example.findhdd.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.findhdd.R;
import com.example.findhdd.dto.HardDriveDTO;

public class EditHardDriveDialog extends DialogFragment {

    private HardDriveDTO hardDriveDTO;
    private OnSaveListener onSaveListener;

    public interface OnSaveListener {
        void onSave(HardDriveDTO updatedHdd);
    }

    public static EditHardDriveDialog newInstance(HardDriveDTO hdd) {
        EditHardDriveDialog dialog = new EditHardDriveDialog();
        Bundle args = new Bundle();
        args.putSerializable("hdd", hdd);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.onSaveListener = listener;
    }

    private EditText etBrand, etModel, etCapacity, etReadSpeed, etWriteSpeed,
            etInterfaceType, etPowerConsumption, etWarranty, etLength, etWidth, etHeight, etWeight, etDescription, etPrice;

    private AutoCompleteTextView etType, etFormFactor, etPurpose;

    private final String[] typeOptions = {"SSD", "HDD"};
    private final String[] formFactorOptions = {"2.5\"", "3.5\"", "M.2", "PCIe"};
    private final String[] purposeOptions = {
            "для ноутбука",
            "для настольного компьютера",
            "для настольного компьютера и ноутбука",
            "для сервера",
            "для NAS",
            "для систем видеонаблюдения"
    };



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            hardDriveDTO = (HardDriveDTO) getArguments().getSerializable("hdd");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_edit_hard_drive, null);

        etBrand = view.findViewById(R.id.etBrand);
        etModel = view.findViewById(R.id.etModel);
        etCapacity = view.findViewById(R.id.etCapacity);
        etReadSpeed = view.findViewById(R.id.etReadSpeed);
        etWriteSpeed = view.findViewById(R.id.etWriteSpeed);
        etInterfaceType = view.findViewById(R.id.etInterfaceType);
        etPowerConsumption = view.findViewById(R.id.etPowerConsumption);
        etWarranty = view.findViewById(R.id.etWarranty);
        etLength = view.findViewById(R.id.etLength);
        etWidth = view.findViewById(R.id.etWidth);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        etDescription = view.findViewById(R.id.etDescription);
        etPrice = view.findViewById(R.id.etPrice);

        etType = view.findViewById(R.id.etType);
        etFormFactor = view.findViewById(R.id.etFormFactor);
        etPurpose = view.findViewById(R.id.etPurpose);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, typeOptions);
        etType.setAdapter(typeAdapter);

        ArrayAdapter<String> formFactorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, formFactorOptions);
        etFormFactor.setAdapter(formFactorAdapter);

        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, purposeOptions);
        etPurpose.setAdapter(purposeAdapter);

        if (hardDriveDTO != null) {
            etBrand.setText(hardDriveDTO.getBrand());
            etModel.setText(hardDriveDTO.getModel());
            etType.setText(hardDriveDTO.getType(), false);
            etCapacity.setText(String.valueOf(hardDriveDTO.getCapacity()));
            etFormFactor.setText(hardDriveDTO.getFormFactor(), false);
            etReadSpeed.setText(String.valueOf(hardDriveDTO.getReadSpeed()));
            etWriteSpeed.setText(String.valueOf(hardDriveDTO.getWriteSpeed()));
            etPurpose.setText(hardDriveDTO.getPurpose(), false);
            etInterfaceType.setText(hardDriveDTO.getInterfaceType());
            etPowerConsumption.setText(String.valueOf(hardDriveDTO.getPowerConsumption()));
            etWarranty.setText(hardDriveDTO.getWarranty());
            etLength.setText(String.valueOf(hardDriveDTO.getLength()));
            etWidth.setText(String.valueOf(hardDriveDTO.getWidth()));
            etHeight.setText(String.valueOf(hardDriveDTO.getHeight()));
            etWeight.setText(String.valueOf(hardDriveDTO.getWeight()));
            etDescription.setText(hardDriveDTO.getDescription());
            etPrice.setText(String.valueOf(hardDriveDTO.getPrice()));
        }

        builder.setView(view)
                .setTitle("Редактировать диск")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    HardDriveDTO updated = new HardDriveDTO();
                    updated.setId(hardDriveDTO.getId()); // обязательно id

                    updated.setBrand(etBrand.getText().toString());
                    updated.setModel(etModel.getText().toString());
                    updated.setType(etType.getText().toString());
                    updated.setCapacity(Integer.parseInt(etCapacity.getText().toString()));
                    updated.setFormFactor(etFormFactor.getText().toString());
                    updated.setReadSpeed(Integer.parseInt(etReadSpeed.getText().toString()));
                    updated.setWriteSpeed(Integer.parseInt(etWriteSpeed.getText().toString()));
                    updated.setPurpose(etPurpose.getText().toString());
                    updated.setInterfaceType(etInterfaceType.getText().toString());
                    updated.setPowerConsumption(Double.parseDouble(etPowerConsumption.getText().toString()));
                    updated.setWarranty(etWarranty.getText().toString());
                    updated.setLength(Double.parseDouble(etLength.getText().toString()));
                    updated.setWidth(Double.parseDouble(etWidth.getText().toString()));
                    updated.setHeight(Double.parseDouble(etHeight.getText().toString()));
                    updated.setWeight(Integer.parseInt(etWeight.getText().toString()));
                    updated.setDescription(etDescription.getText().toString());
                    updated.setPrice(Double.parseDouble(etPrice.getText().toString()));

                    if (onSaveListener != null) {
                        onSaveListener.onSave(updated);
                    }
                })
                .setNegativeButton("Отмена", null);

        return builder.create();
    }
}
