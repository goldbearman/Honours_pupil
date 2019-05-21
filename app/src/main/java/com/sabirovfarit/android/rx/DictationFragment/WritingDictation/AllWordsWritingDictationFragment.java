package com.sabirovfarit.android.rx.DictationFragment.WritingDictation;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sabirovfarit.android.rx.LearningFragment.LearningWords.AllWordsAdapter;
import com.sabirovfarit.android.rx.LearningFragment.LearningWords.SwipeController;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.WordViewModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllWordsWritingDictationFragment extends Fragment {


    public static final String ALL_WORDS_WRITING_DICTATION_FRAGMENT_KEY = "AllWordsWritingDictationFragment key";

    private WordViewModel viewModel;
    private RecyclerView rvWritingDictation;
    private AllWordsAdapter adapter;
    private Button btnWritingDictation;

    private long idWordList;


    public AllWordsWritingDictationFragment() {
        // Required empty public constructor
    }
    private AllWordsWritingDictationFragmentListener listener;

    public void setListener(AllWordsWritingDictationFragmentListener listener) {
        this.listener = listener;
    }

    interface AllWordsWritingDictationFragmentListener {
        void AllWordsWritingDictationFragmentOnClick(long id);
    }


    public static AllWordsWritingDictationFragment newInstance(long idWordList) {
        Bundle args = new Bundle();
        args.putLong(ALL_WORDS_WRITING_DICTATION_FRAGMENT_KEY, idWordList);
        AllWordsWritingDictationFragment fragment = new AllWordsWritingDictationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_words_writing_dictation, container, false);
        rvWritingDictation = view.findViewById(R.id.rv_writing_dictation);
        btnWritingDictation = view.findViewById(R.id.btn_writing_dictation);

        // Получаем аргумент фрагмента
        idWordList = getArguments().getLong(ALL_WORDS_WRITING_DICTATION_FRAGMENT_KEY);

        updateUI();

        btnWritingDictation.setOnClickListener(v -> {
            listener.AllWordsWritingDictationFragmentOnClick(idWordList);
        });

        return view;
    }

    private void updateUI() {

        LiveData<List<Word>> wordsByIdWordListLD = viewModel.getWordsByIdWordListLD(idWordList);
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(getActivity(), wordsByIdWordListLD))
                .observeOn(Schedulers.io())
                .map(list -> {
                    Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue())); // Соритруем по алфавиту
                    return list;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    adapter = new AllWordsAdapter(list);
                    rvWritingDictation.setAdapter(adapter);
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvWritingDictation.setLayoutManager(linearLayoutManager);
        // Делаем горизонтальные линии между словами
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        rvWritingDictation.addItemDecoration(dividerItemDecoration);

        // Устанавливаем ItemTouchHelper для свайпа влево и право
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeController(idWordList, 1));
        itemTouchHelper.attachToRecyclerView(rvWritingDictation);
    }

}
