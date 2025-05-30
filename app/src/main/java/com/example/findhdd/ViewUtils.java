package com.example.findhdd;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUtils {
    public static void setupSpecView(View parent, int viewId, String name, String value) {
        View specView = parent.findViewById(viewId);
        TextView tvName = specView.findViewById(R.id.tvSpecName);
        TextView tvValue = specView.findViewById(R.id.tvSpecValue);
        tvName.setText(name);
        tvValue.setText(value);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showErrorToast(Context context, String error) {
        Toast.makeText(context, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
    }
}