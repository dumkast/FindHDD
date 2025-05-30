package com.example.findhdd.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

public class HardDriveFilterHelper {
    public static List<String> getUniqueBrands() {
        return Arrays.asList("Seagate", "Western Digital", "Toshiba", "Samsung", "Kingston", "Crucial");
    }

    public static List<String> getUniqueTypes() {
        return Arrays.asList("HDD", "SSD");
    }

    public static List<String> getUniqueFormFactors() {
        return Arrays.asList("2.5\"", "3.5\"", "M.2");
    }
    public static List<String> getUniquePurposes() {
        return Arrays.asList("для ноутбука", "для настольного компьютера", "для настольного компьютера и ноутбука", "для сервера", "для NAS", "для систем видеонаблюдения");
    }

    public static void setupCheckboxList(Context context, LinearLayout layout, List<String> items, List<String> selectedItems) {
        layout.removeAllViews();
        int customColor = Color.parseColor("#4A90E2");

        for (String item : items) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(item);
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            checkBox.setHeight((int)(48 * context.getResources().getDisplayMetrics().density));
            checkBox.setPadding((int)(16 * context.getResources().getDisplayMetrics().density), 0, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkBox.setButtonTintList(ColorStateList.valueOf(customColor));
            }

            if (selectedItems != null && selectedItems.contains(item)) {
                checkBox.setChecked(true);
            }

            layout.addView(checkBox);
        }
    }
}