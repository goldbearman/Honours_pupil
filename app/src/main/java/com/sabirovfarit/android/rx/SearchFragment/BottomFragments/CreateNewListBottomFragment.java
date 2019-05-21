package com.sabirovfarit.android.rx.SearchFragment.BottomFragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.MainActivity;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.ToastInfomation;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.WordViewModel;
import com.sabirovfarit.android.rx.DB.WordsList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateNewListBottomFragment extends BottomSheetDialogFragment {

    private static final String TAG = "CreateNewListBottomFrag";

    Button btnChooseColor;
    EditText etNewListName;
    Button btnCreateList;
    int newColor;
    private BottomNavigationView bottomNavigationView;

    public static CreateNewListBottomFragment newInstance() {
        Bundle args = new Bundle();
        CreateNewListBottomFragment fragment = new CreateNewListBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CreateNewListBottomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_list, container, false);

        btnChooseColor = view.findViewById(R.id.btn_choose_color);
        etNewListName = view.findViewById(R.id.et_new_list_name);
        btnCreateList = view.findViewById(R.id.btn_create_list);

        int defaultColorButton = QueryPreferences.getColorButton(getActivity());
        btnCreateList.setBackground(WordColor.assignColorDrawable(getActivity(),2,R.drawable.all_button,1));
        assignColor(defaultColorButton);

        btnChooseColor.setOnClickListener(v -> setColorNewList());
        WordViewModel viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        Log.i(TAG, "onCreateView: " + viewModel.getIdsList().size());

        btnCreateList.setOnClickListener(v -> {

            Log.i(TAG, "onClickGrade: " + "Вошли");
            String text = etNewListName.getText().toString();
            if (text.length() > 0) {
                //Получаем список id выбранных в SearchFragment элементов
                List<Long> idsList = viewModel.getIdsList();
                //Получаем word по id, которые мы выбрали в checkBox
                //Создаем новый список, копирую данные старых слов, чтобы убрать Id.
                //Вставляем новый список и получаем их id
                App.getInstance().getDatabase().wordDao().getWordsByIdSingle(idsList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(words -> {
                            //Создаем новый список слов без id для вставки
                            List<Word> listWithoutId = new ArrayList<>();
                            for (Word word : words) {
                                listWithoutId.add(new Word(word.getValue(), word.getDiffLetter(), word.getWordsClass(), true, word.getSoundWId()));
                            }
                            return listWithoutId;
                        }).map(words -> {
                    //Вставляем новый список и получаем их id
                    List<Long> idInsertWords = App.getInstance().getDatabase().wordDao().addReturnIds(words);
                    return idInsertWords;
                })
                        .subscribe(list -> {
                            //Создаем новую строчку в таблице WordsList
                            WordsList wordsList = new WordsList(text, newColor, list, false, false);
                            App.getInstance().getDatabase().wordsListDao().add(wordsList);
                        });

                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                // Получаем ссылку на BottomNavigationView и переходим в LearningFragment
                bottomNavigationView = MainActivity.mBottomNavigationView;
                bottomNavigationView.setSelectedItemId(R.id.navigation_learn);
            } else {
                ToastInfomation.showToast(getActivity(), "Введите название");
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        // Обнуляем ссылку
        bottomNavigationView = null;
        super.onDestroy();
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
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        assignColor(selectedColor);
                        QueryPreferences.setColorButton(getActivity(), selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void assignColor(int selectedColor) {
        // Устанавливаем цвет фона selectedColor
        btnChooseColor.setBackground(WordColor.assignColorDrawable(getActivity(),selectedColor, R.drawable.botton_shoose_color, 2));
        newColor = selectedColor;
    }

}
