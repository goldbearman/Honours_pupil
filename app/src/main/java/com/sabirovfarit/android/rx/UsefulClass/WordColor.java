package com.sabirovfarit.android.rx.UsefulClass;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

import com.sabirovfarit.android.rx.R;

public class WordColor {

    // Получаем int color выбранного атрибута. Если numAttr = 0, получаем серый цвет
    public static int getAttributeColor(Context context, int numAttr, boolean isAttributeResourceId) {
        int color;
        int attributeResourceId = 0;
        if (numAttr == 0) {
            return Color.GRAY;
        }
        if (numAttr > 0 && numAttr < 4) {
            TypedArray b = null;
            switch (numAttr) {
                case 1:
                    b = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
                    break;
                case 2:
                    b = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
                    break;
                case 3:
                    b = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
                    break;
            }
            attributeResourceId = b.getResourceId(0, 0);
        }
        if (numAttr > 3) {
            return Color.RED;
        }
        if (isAttributeResourceId) {
            return attributeResourceId;
        }
        color = context.getResources().getColor(attributeResourceId);
        return color;
    }

    // Получаем drawable с выбранным цветом фона. (контекст, цвет, @DrawableRes int Id, выбираем фильтр)
    public static Drawable assignColorDrawable(Context context, int color, int idDrawable, int numberMode) {
        // Если ввести
        if (color > 0 && color < 4) {
            color = getAttributeColor(context, color, false);
        }
        Drawable drawable = context.getResources().getDrawable(idDrawable);
        if (numberMode == 1) drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        else drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        return drawable;
    }


    // Метод для присавивания ImageButton выбранного цвета
    public static void assignAttributeColorDrawable(Context context, int numAttr, ImageButton ibutton) {
        int attributeColor = getAttributeColor(context, numAttr, false);
        // Из кнопки получаем drawable
        Drawable drawable = ibutton.getDrawable();
        // Присваиваем drawable выбранный цвет
        drawable.setColorFilter(attributeColor, PorterDuff.Mode.SRC_IN);
        ibutton.setImageDrawable(drawable);
    }
}
