package com.sabirovfarit.android.rx.UsefulClass;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sabirovfarit.android.rx.R;

import java.util.zip.Inflater;

public class ToastInfomation {

    public static void showToast(Context context,String massage) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_cardview, null);
        TextView tvMassage = toastView.findViewById(R.id.tv_massage);
        tvMassage.setText(massage);
        Toast toast = Toast.makeText(context, massage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

}
