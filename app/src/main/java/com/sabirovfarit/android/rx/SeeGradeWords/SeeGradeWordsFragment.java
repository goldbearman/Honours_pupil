package com.sabirovfarit.android.rx.SeeGradeWords;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.SearchFragment.WordsListSearchAdapter;
import com.sabirovfarit.android.rx.DB.Word;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeeGradeWordsFragment extends Fragment {


    public static final String SEE_GRADE_WORDS_FRAGMENT_ARGS = "SeeGradeWordsFragment args";
    RecyclerView rvSeeGradeWord;
    private long idWordList;

    //Передаем через intent Id нажатого списка
    public static SeeGradeWordsFragment newInstance(long idWordList) {

        Bundle args = new Bundle();
        args.putLong(SEE_GRADE_WORDS_FRAGMENT_ARGS, idWordList);

        SeeGradeWordsFragment fragment = new SeeGradeWordsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SeeGradeWordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see_grade_words, container, false);
        Bundle args = getArguments();
        idWordList = args.getLong(SEE_GRADE_WORDS_FRAGMENT_ARGS);

        rvSeeGradeWord = view.findViewById(R.id.rv_see_grade_words);

        // По id нажатого списка получаем WordsList
        // Из WordsList получаем поле ListId, где храняться id всех word(слов)
        // По этим id из таблицы word получаем сами слова
        App.getInstance().getDatabase().wordsListDao().getFlowableIdsFromListId(idWordList)
                .observeOn(Schedulers.io())
                .flatMap(wordsList -> {
                    List<Long> listId = wordsList.getListId();
                    Flowable<List<Word>> wordsById = App.getInstance().getDatabase().wordDao().getWordsByIdFlowable(listId);
                    return wordsById;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Word>>() {
                    @Override
                    public void accept(List<Word> words) throws Exception {
                       WordsListSearchAdapter adapter = new WordsListSearchAdapter(words);
                        rvSeeGradeWord.setAdapter(adapter);
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvSeeGradeWord.setLayoutManager(linearLayoutManager);


        return view;
    }

}
