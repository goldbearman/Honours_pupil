package com.sabirovfarit.android.rx.LearningFragment.LearningWords;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FiveWordsFragment extends Fragment {

    public static final String FIVE_WORDS_FRAGMENT_ARGS = "Five words fragment args";
    private static final String TAG = "FiveWordsFragment";

    private Boolean randomOrder;

    Button btnFiveWords;
    RecyclerView rvFiveWords;
    FiveWordsAdapter adapter;
    WordViewModel viewModel;

    long idWordList;

    private FiveWordsFragmentListener listener;

    interface FiveWordsFragmentListener {
        void fiveWordsFragmentOnClick();
    }

    public void setListener(FiveWordsFragmentListener listener) {
        this.listener = listener;
    }


    public static FiveWordsFragment newInstance(long idWordList) {
        Bundle args = new Bundle();
        args.putLong(FIVE_WORDS_FRAGMENT_ARGS, idWordList);

        FiveWordsFragment fragment = new FiveWordsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FiveWordsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_five_words, container, false);

        Bundle args = getArguments();
        idWordList = args.getLong(FIVE_WORDS_FRAGMENT_ARGS);
        Log.i(TAG, "idWordList: " + idWordList);

        rvFiveWords = view.findViewById(R.id.rv_five_words);
        btnFiveWords = view.findViewById(R.id.btn_five_words);

        randomOrder = true;

        // По id нажатого списка получаем WordsList
        // Из WordsList получаем поле ListId, где храняться id всех word(слов)
        // По этим id из таблицы word получаем сами слова
        App.getInstance().getDatabase().wordsListDao().getFlowableIdsFromListId(idWordList)
                .observeOn(Schedulers.io())
                .map(wordsList -> {
                    Log.i(TAG, "wordsList: " + wordsList);
                    List<Long> listId = wordsList.getListId();
                    List<Word> wordsById = App.getInstance().getDatabase().wordDao().getWordsByIdList(listId);
                    Log.i(TAG, "wordsById: " + wordsById);
                    if (randomOrder) {
                        Collections.shuffle(wordsById);
                    }
                    return wordsById;
                }).flatMapSingle(words ->
                        Flowable.fromIterable(words)
                                .filter(word -> !word.isLearned())
                                .take(5)
                                .toList()
                // Сохраняю id слов для изучения во ViewModel
        ).map(words -> {
            List<Long> listId = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                listId.add(words.get(i).getId());
            }
            viewModel.setListIdLiveData(listId);
            return words;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(words -> {
                    Log.i(TAG, "words: " + words);
                    FiveWordsAdapter adapter = new FiveWordsAdapter(words);
                    rvFiveWords.setAdapter(adapter);
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvFiveWords.setLayoutManager(linearLayoutManager);
        // Делаем горизонтальные линии между словами
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        rvFiveWords.addItemDecoration(dividerItemDecoration);

        btnFiveWords.setOnClickListener(v -> {

            if (listener != null) {
                listener.fiveWordsFragmentOnClick();
            }
        });

        return view;
    }
}
