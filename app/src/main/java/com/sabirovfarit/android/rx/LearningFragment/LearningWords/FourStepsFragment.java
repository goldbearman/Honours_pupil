package com.sabirovfarit.android.rx.LearningFragment.LearningWords;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation.SoundBox;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.UsefulClass.ToastInfomation;
import com.sabirovfarit.android.rx.UsefulClass.UseFul;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.WordViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourStepsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FourStepsFragment";

    @BindView(R.id.tv_action)
    TextView tvAction;
    @BindView(R.id.iv_volume)
    ImageView ivVolume;
    @BindView(R.id.tv_check_word)
    TextView tvCheckWord;
    @BindView(R.id.btn_next)
    Button btnNext;
    private View view;
    private LinearLayout linear;
    List<View> allView;
    Word testWord;
    SoundBox soundBox;
    Unbinder unbinder;
    private WordViewModel viewModel;

    public FourStepsFragment() {
        // Required empty public constructor
    }

    public static FourStepsFragment newInstance() {
        Bundle args = new Bundle();
        FourStepsFragment fragment = new FourStepsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        allView = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_three_step, container, false);
        linear = view.findViewById(R.id.ll_container);
        unbinder = ButterKnife.bind(this, view);
        soundBox = new SoundBox(getActivity());
        insertListFromVM();
        tvCheckWord.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        checkLearned();
        super.onDestroy();
    }

    private void checkLearned() {
        // При выходе из фрагмента проверяем, выучено слово или нет
        // Если не выучено, то присвоиваем всем stage false
        // Из ViewModel получаем список изучаемых слов
        // Из LiveData получаем Flowable
        LiveData<List<Word>> listWordLiveData = viewModel.getListWordLiveData();
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(getActivity(), listWordLiveData))
//                .first(new ArrayList<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle(list -> Flowable.fromIterable(list)
                        .filter(word -> !word.isLearned())
                        .toList()
                ).first(new ArrayList<>())
                .subscribe(list1 -> {
                    Log.i(TAG, "checkLearned: " + list1.size());
                    for (int i = 0; i < list1.size(); i++) {
                        Log.i(TAG, "checkLearned: " + list1.get(i).getValue());
                        long id = list1.get(i).getId();
                        App.getInstance().getDatabase().wordDao().updatemStage1234ById(id, false);
                    }
                });
    }

    @OnClick(R.id.btn_next)
    void onClickBtnNext(View view) {
        Log.i(TAG, "onClickBtnNext: " + "Вошли");
        insertListFromVM();
    }

    private void insertListFromVM() {
        // Получаем список слов из ViewModel и преобразуем его в Single
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(getActivity(), viewModel.getListWordLiveData()))
                .first(new ArrayList<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    Log.i(TAG, "onClickBtnNext: " + Thread.currentThread());
                    Log.i(TAG, "onClickBtnNext: " + list);
                    changeContent(list);
                });
    }

    // Метод для проверки слова по 3 stage. Если stage1 у слова true, слудующая стадия. И т.д.
    private void changeContent(List<Word> words) {
        Log.i(TAG, "changeContent: " + "Вошли");
        btnNext.setVisibility(View.VISIBLE);
        for (int i = 0; i < words.size(); i++) {
            if (!words.get(i).isStage1()) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Word word = words.get(i);
                initUI(word,R.string.void_text,R.string.learn_words);

                for (int j = 0; j < word.getValue().length(); j++) {
                    EditText etCustome = new EditText(getActivity());
                    String s = Character.toString(word.getValue().charAt(j));
                    etCustome.setText(s);
                    etCustome.setEnabled(false);
                    etCustome.setTextColor(getResources().getColor(R.color.black));

                    // Устанавливаем фон - убираем линию подчеркивания
                    etCustome.getBackground()
                            .setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

                    etCustome.setTextSize(36);
                    for (int k = 0; k < word.getDiffLetter().size(); k++) {
                        if (word.getDiffLetter().get(k) == j) {
                            etCustome.setTextColor(-1507328);
                        }
                    }
                    allView.add(etCustome);
                    linear.addView(etCustome);
                }
                Log.i(TAG, "underEx: ");
                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemStage1ById(word.getId(), true));
                volumeOnClick(getView());
                break;
            }
        }
        if (checkIsStage(words, 1) == words.size()) {
            btnNext.setVisibility(View.GONE);
            for (int i = 0; i < words.size(); i++) {
                if (!words.get(i).isStage2()) {
                    Word word = words.get(i);
                    initUI(word,R.string.check,R.string.insert_word);

                    for (int j = 0; j < word.getValue().length(); j++) {
                        EditText etCustome = new EditText(getActivity());
                        String s = Character.toString(word.getValue().charAt(j));
                        etCustome.setText(s);
                        etCustome.setEnabled(false);
                        etCustome.setTextColor(getResources().getColor(R.color.black));

                        // Ограничиваем вводимые символы одним
                        etCustome.setFilters(UseFul.getMaxLenghtFilter(1));

                        // Устанавливаем фон - убераем линию подчеркивания
                        etCustome.getBackground()
                                .setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

                        etCustome.setTextSize(36);
                        for (int k = 0; k < word.getDiffLetter().size(); k++) {
                            if (word.getDiffLetter().get(k) == j) {
                                etCustome.setTextColor(-1507328);
                                etCustome.setText("");
                                etCustome.getBackground().clearColorFilter();
                                etCustome.setEnabled(true);
                            }
                        }
                        allView.add(etCustome);
                        linear.addView(etCustome);
                    }

                    // Устанавливаем фокус на первую и открываем клавиатуру
                    // Определяем EditText, на котором будет первый фокус
                    int firstLetterFocus = word.getDiffLetter().get(0).intValue();
                    allView.get(firstLetterFocus).requestFocus();
                    openKeyboard(((EditText) allView.get(firstLetterFocus)), true);

                    // Делаем перемещение фокуса при вводе одной буквы
                    // Два перемещения фокуса
                    if (word.getDiffLetter().size() > 1) {
                        int secondLetterFocus = word.getDiffLetter().get(1).intValue();
                        RxTextView.afterTextChangeEvents(((EditText) allView.get(firstLetterFocus)))
                                .skipInitialValue()
                                .subscribe(textViewAfterTextChangeEvent -> {
                                    if (textViewAfterTextChangeEvent.editable().toString().length() == 1) {
                                        allView.get(secondLetterFocus).requestFocus();
                                    }
                                });

                        // Три и более перемещения фокуса
                        if (word.getDiffLetter().size() > 2) {
                            int thirdLetterFocus = word.getDiffLetter().get(2).intValue();
                            RxTextView.afterTextChangeEvents(((EditText) allView.get(secondLetterFocus)))
                                    .skipInitialValue()
                                    .subscribe(textViewAfterTextChangeEvent -> {
                                        if (textViewAfterTextChangeEvent.editable().toString().length() == 1) {
                                            allView.get(thirdLetterFocus).requestFocus();
                                        }
                                    });
                        }
                    }
                    Log.i(TAG, "step 2: " + words.get(i).getValue());
                    break;
                }
            }
        }
        if (checkIsStage(words, 2) == words.size()) {
            btnNext.setVisibility(View.INVISIBLE);
            btnNext.setEnabled(false);
            for (int i = 0; i < words.size(); i++) {
                if (!words.get(i).isStage3()) {
                    Word word = words.get(i);
                    initUI(word,R.string.check,R.string.write_word);

                    EditText etCustome = new EditText(getActivity());
                    etCustome.setTextSize(30);
                    etCustome.setMaxEms(13);
                    etCustome.setInputType(InputType.TYPE_CLASS_TEXT);
                    etCustome.setMaxLines(1);
                    // Ограничиваем количестро вводимых символов
                    etCustome.setFilters(UseFul.getMaxLenghtFilter(13));
                    etCustome.setGravity(Gravity.CENTER);

                    allView.add(etCustome);
                    linear.addView(etCustome);
                    // Устанавливаем фокус и открываем клавиатуру
                    etCustome.requestFocus();
                    openKeyboard(etCustome, true);
                    Log.i(TAG, "step 2: " + words.get(i).getValue());
                    break;
                }
            }
        }
        if (checkIsStage(words, 3) == words.size()) {
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().onBackPressed();
        }
    }

    private void initUI(Word word,int tvCheckWordText,int tvActionText) {
        testWord = word;
        linear.removeAllViews();
        soundBox.play(word.getSoundWId());
        allView.clear();
        tvCheckWord.setTextColor(getResources().getColor(R.color.green_primary_dark));
        tvCheckWord.setText(tvCheckWordText);
        tvAction.setText(tvActionText);
        tvCheckWord.setTextSize(20);
    }

    // При нажатии "Проверить", проверяем правильность введенного текста
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: " + "Вошли!");
        String sWord = "";
        long id = testWord.getId();
        if (allView.size() == 1) {
            String letter = ((EditText) allView.get(0)).getText().toString();
            sWord = letter;
            if (testWord.getValue().equals(sWord)) {
                Log.i(TAG, "onClick: " + "if");
                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemStage3ById(id, true));
                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemLearnedById(id, true));
                ToastInfomation.showToast(getActivity(),"Слово \""+testWord.getValue()+"\" выучено");
                // Слово выучено. Фиксируем +1 в значении выученных слов:
                int learnedWordCount = QueryPreferences.getLearnedWordCount(getActivity());
                QueryPreferences.setLearnedWordCount(getActivity(),learnedWordCount+1);  // +1

                tvCheckWord.setTextSize(28);
                tvCheckWord.setText("Правильно");
                // Убираем клавиатуру
                openKeyboard(new EditText(getActivity()), false);

                Handler h = new Handler();
                h.postDelayed(() -> insertListFromVM(), 1500);
            } else {
                Log.i(TAG, "onClick: " + "else");
                tvCheckWord.setTextColor(-1507328);
                tvCheckWord.setTextSize(28);
                tvCheckWord.setText("Неправильно");
                // Убираем клавиатуру
                openKeyboard(new EditText(getActivity()), false);

                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemStage123ById(id, false));
                Handler h = new Handler();
                h.postDelayed(() -> insertListFromVM(), 1500);
            }
        }
        if (allView.size() > 1) {
            for (int i = 0; i < testWord.getValue().length(); i++) {
                String letter = ((EditText) allView.get(i)).getText().toString();
                sWord = sWord + letter;
            }
            Log.i(TAG, "onClick: " + sWord);
            if (testWord.getValue().equals(sWord)) {
                Log.i(TAG, "onClick: " + "if");
                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemStage2ById(id, true));
                tvCheckWord.setTextSize(28);
                tvCheckWord.setText("Правильно");
                // Убираем клавиатуру
                openKeyboard(new EditText(getActivity()), false);

                Handler h = new Handler();
                h.postDelayed(() -> insertListFromVM(), 1500);
            } else {
                Log.i(TAG, "onClick: " + "else");
                tvCheckWord.setTextColor(-1507328);
                tvCheckWord.setTextSize(28);
                tvCheckWord.setText("Неправильно");
                // Убираем клавиатуру
                openKeyboard(new EditText(getActivity()), false);

                Executors.newSingleThreadScheduledExecutor().execute(() -> App.getInstance().getDatabase().wordDao().updatemStage12ById(id, false));
                Handler h = new Handler();
                h.postDelayed(() -> insertListFromVM(), 1500);
            }
        }
    }

    // Проверка параметра isStage всех элементов на true/false
    private int checkIsStage(List<Word> words, int number) {
        int count = 0;
        for (int i = 0; i < words.size(); i++) {
            if (number == 1) {
                if (words.get(i).isStage1() == true) {
                    count++;
                }
            }
            if (number == 2) {
                if (words.get(i).isStage2() == true) {
                    count++;
                }
            }
            if (number == 3) {
                if (words.get(i).isStage3() == true) {
                    count++;
                }
            }
        }
        return count;
    }

    // Открываем и закрываем клавиатуру
    private void openKeyboard(EditText et, boolean open) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (open) {
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @OnClick(R.id.iv_volume)
    void volumeOnClick(View view) {
        soundBox.play(testWord.getSoundWId());
    }
}
