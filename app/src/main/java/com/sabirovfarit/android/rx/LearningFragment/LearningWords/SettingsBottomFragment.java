package com.sabirovfarit.android.rx.LearningFragment.LearningWords;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.WordViewModel;

import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsBottomFragment extends BottomSheetDialogFragment {

    private static final String TAG = "CreateNewListBottomFrag";
    public static final String SETTINGS_BOTTOM_FRAGMENT_KEY = "SettingsBottomFragment key";

    private Button btnChooseColor;
    private EditText etNewListName;
    private Button btnCreateList;
    private TextView tvCreateNewList;
    private int newColor;
    private BottomNavigationView bottomNavigationView;
    private WordViewModel viewModel;
    private long idWordList;


    public static SettingsBottomFragment newInstance(long idWordList) {
        Bundle args = new Bundle();
        args.putLong(SETTINGS_BOTTOM_FRAGMENT_KEY, idWordList);
        SettingsBottomFragment fragment = new SettingsBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsBottomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        idWordList = getArguments().getLong(SETTINGS_BOTTOM_FRAGMENT_KEY); // Получаем аргумент фрагмента
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Переиспользуем R.layout.fragment_create_new_list
        View view = inflater.inflate(R.layout.fragment_create_new_list, container, false);
        initUi(view);

        btnChooseColor.setOnClickListener(v -> setColorNewList());  // Выбираем цвет при нажатии на кнопку
        btnCreateList.setOnClickListener(v -> {
            String text = etNewListName.getText().toString();
            // Получаем wordList по id и меняем его, если будут внесены изменения
            App.getInstance().getDatabase().wordsListDao().getWordListByIdSingle(idWordList)
                    .subscribeOn(Schedulers.io())
                    .map(wordsList -> {
                        if (newColor != 0) {
                            wordsList.setColor(newColor);
                        }
                        if (text.trim().length() > 0) {
                            wordsList.setName(text);
                        }
                        return wordsList;
                    }).subscribe(wordsList -> App.getInstance().getDatabase().wordsListDao().update(wordsList));
            // Закрываем SettingsBottomFragment
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
        return view;
    }

    private void initUi(View view) {
        btnChooseColor = view.findViewById(R.id.btn_choose_color);
        etNewListName = view.findViewById(R.id.et_new_list_name);
        btnCreateList = view.findViewById(R.id.btn_create_list);
        tvCreateNewList = view.findViewById(R.id.tv_create_new_list);
        btnCreateList.setBackground(WordColor.assignColorDrawable(getActivity(), 2, R.drawable.all_button, 1));

        viewModel.getWordsListByIdLiveData(idWordList).observe(this, wordsList -> {
            // Меняем названия и цвета на нужные в R.layout.fragment_create_new_list
            tvCreateNewList.setText("Редактировать: " + wordsList.getName());
            btnChooseColor.setBackground(WordColor.assignColorDrawable(getActivity(), wordsList.getColor(), R.drawable.botton_shoose_color, 2));
            etNewListName.setHint("Новое название");
            btnCreateList.setText("Изменить подборку");
        });
    }

    // Используем стороннею библиотеку библиотеку для выбора цвета
    private void setColorNewList() {
        int currentBackgroundColor = getActivity().getResources().getColor(R.color.purple_primary);
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle("Выберите цвет сборки")
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    assignColor(selectedColor);
                    QueryPreferences.setColorButton(getActivity(), selectedColor);
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();
    }

    private void assignColor(int selectedColor) {
        // Устанавливаем цвет фона selectedColor
        btnChooseColor.setBackground(WordColor.assignColorDrawable(getActivity(), selectedColor, R.drawable.botton_shoose_color, 2));
        newColor = selectedColor;
    }

}
