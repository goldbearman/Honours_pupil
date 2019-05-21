package com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.WordViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class WritingDictationFragment extends Fragment {

    private static final String TAG = "WritingDictationFragmen";
    public static final String WRITING_DICTATION_FRAGMENT_KEY = "WritingDictationFragment key";
    public static final String WORLD_SOUND_1_ALFAVIT = "world_sound";

    long idWordList;
    SoundBox soundBox;
    WordViewModel viewModel;
    long lTime;
    long skipCount = 0;
    int listSize;
    Disposable disposable;
    List<ImageButton> listButton;

    Unbinder unbinder;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.btn_play)
    ImageButton btnPlay;
    @BindView(R.id.btn_pause)
    ImageButton btnPause;
    @BindView(R.id.btn_reset)
    ImageButton btnReset;

    public WritingDictationFragment() {
        // Required empty public constructor
    }

    public static WritingDictationFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(WRITING_DICTATION_FRAGMENT_KEY, id);
        WritingDictationFragment fragment = new WritingDictationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        soundBox = new SoundBox(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_writing_dictation, container, false);
        unbinder = ButterKnife.bind(this, view);
        idWordList = getArguments().getLong(WRITING_DICTATION_FRAGMENT_KEY);
        // Получаем сохраненное ранее значение времени из QuaryPreference
        lTime = QueryPreferences.getTime(getActivity());
        etTime.setText(String.valueOf(lTime));
        // Создаем массив из кнопок, чтобы удобнее было назначать им цвета
        // Назначаем кнопкам и imageView цвет ColorPrimary
        listButton = new ArrayList<>();
        listButton.add(btnPlay);
        listButton.add(btnPause);
        listButton.add(btnReset);
        // Присваиваем всем кнопкам цвет ColorPrimary
        selectColorButton(new ImageButton(getActivity()));
        return view;
    }

    @OnTextChanged(value = R.id.et_time, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        if (s.toString().equals("")) {
            // Если поле останется пустое, сохраним 7 секунд
            QueryPreferences.setTime(getActivity(), 7);
            return;
        }
        // Сохраняем введенное значение
        QueryPreferences.setTime(getActivity(), Long.parseLong(s.toString()));
        // Получаем новое начение времени промежутка между словами
        lTime = Long.parseLong(s.toString());
        Log.i(TAG, "afterTextChanged: " + lTime);
    }

    @OnClick(R.id.btn_play)
    void playOnClick(View view) {
        // Делаем кнопку неактивной
        btnPlay.setEnabled(false);
        // При нажатии перекрашиваем в серый цвет
        selectColorButton(btnPlay);

        LiveData<List<Word>> wordsByIdWordListLD = viewModel.getWordsByIdWordListLD(idWordList);
        Flowable flowable = Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(this, wordsByIdWordListLD))
                .doOnNext(list -> listSize = list.size())  // Узнаем количестов переданных слов
                .flatMap(list -> Flowable.fromIterable(list))
                .skip(skipCount)   // При нажатии pauseOnClick фиксируется ко
                .zipWith(Flowable.interval(lTime + 2, TimeUnit.SECONDS), (word, aLong) -> word.getSoundWId());  // Устанавливаем период lTime + 2, 2 - это время звука

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                soundBox.play(integer);
                skipCount++;  //
                if (skipCount == listSize) {
                    // Диктант написан. Фиксируем +1 в значении написанных диктантов:
                    int learnedWordCount = QueryPreferences.getDictationsWritten(getActivity());
                    QueryPreferences.setDictationsWritten(getActivity(),learnedWordCount+1);  // +1
                    // Вариант не самый лучший, но он прост и работает(вроде:)).На btnPlay.setEnabled(true) вылезает ошибка. Ловим ее в App.
                    finish(true, new ImageButton(getActivity()));
                }
                Log.i(TAG, "accept:skipCount " + skipCount);
            }
        };
        disposable = flowable.subscribe(consumer);
    }

    @OnClick(R.id.btn_pause)
    void pauseOnClick(View view) {
        finish(false, btnPause);
        Log.i(TAG, "pauseOnClick: ");
    }

    @OnClick(R.id.btn_reset)
    void resetOnClick(View view) {
        finish(true, btnReset);
        Log.i(TAG, "resetOnClick: ");
    }

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        soundBox.release();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Отписываемся от Flowable и делаем кнопку активной
    private void finish(boolean isFinish, ImageButton ib) {
        if (isFinish) skipCount = 0;
        // Отписываемся
        if (disposable != null) {
            disposable.dispose();
        }
        // Делаем кнопку активной
        btnPlay.setEnabled(true);
        // Устанавливаем цвет нажатой кнопки
        selectColorButton(ib);
    }

    // Метод для присваивания цветов кнопкам. Нажатой - серый, остальным - ColorPrimary
    // Если в параметр внеси new Button, то кнопкам присвоится ColorPrimary
    private void selectColorButton(ImageButton button) {
        for (int i = 0; i < listButton.size(); i++) {
            ImageButton ib = listButton.get(i);
            WordColor.assignAttributeColorDrawable(getActivity(), 2, ib);
            Log.i(TAG, "selectColor: " + "for");
            if (ib == button) {
                WordColor.assignAttributeColorDrawable(getActivity(), 0, ib);
                Log.i(TAG, "selectColor: " + "if");
            }
        }
    }
}
