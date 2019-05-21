package com.sabirovfarit.android.rx.UsefulClass;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sabirovfarit.android.rx.LearningFragment.LearningFragment;
import com.sabirovfarit.android.rx.R;

public class InformationDialog extends DialogFragment {

    public static final String INFORMATION_DIALOG_KEY = "InformationDialog key";
    private TextView tvInformation;

    public static InformationDialog newInstance(String sInformation) {
        Bundle args = new Bundle();
        args.putString(INFORMATION_DIALOG_KEY,sInformation);
        InformationDialog fragment = new InformationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast_cardview, null);
        // Убираем radius, заданный в CardView
        CardView cardView = view.findViewById(R.id.toast_card_view);
        cardView.setRadius(0);

        // Получаем текст из args
        String sInformation = getArguments().getString(INFORMATION_DIALOG_KEY);

        tvInformation = view.findViewById(R.id.tv_massage);
        tvInformation.setText(sInformation);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                .create();
    }
}
