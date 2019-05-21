package com.sabirovfarit.android.rx.LearningFragment.LearningWords;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.DB.AppDB;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.ToastInfomation;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.WordViewModel;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllWordsLearningWordsFragment extends Fragment {

    private static final String TAG = "AllWordsLearningWordsFr";

    public static final String ALL_WORDS_LEARNING_WORDS_FRAGMENT_ARGS = "AllWordsLearningWordsFragment args";
    //    @BindView(R.id.rv_all_words)
    RecyclerView rvAllWords;
    Unbinder unbinder;
    long idWordList;
    List<Word> wordsById;
    private Paint p = new Paint();
    Flowable<List<Word>> wordsByIdFlowable;
    Single<List<Word>> listSingle;
    AppDB database;
    WordViewModel viewModel;


    //Передаем через intent Id нажатого списка
    public static AllWordsLearningWordsFragment newInstance(long idWordList) {
        Bundle args = new Bundle();
        args.putLong(ALL_WORDS_LEARNING_WORDS_FRAGMENT_ARGS, idWordList);
        AllWordsLearningWordsFragment fragment = new AllWordsLearningWordsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private AllWordsFragmentListener listener;

    interface AllWordsFragmentListener {
        void allWordsFragmentOnClick();
    }

    public void setListener(AllWordsFragmentListener listener) {
        this.listener = listener;
    }

    public AllWordsLearningWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        database = App.getInstance().getDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_words_learning_words, container, false);
        unbinder = ButterKnife.bind(this, view);
        rvAllWords = view.findViewById(R.id.rv_all_words);   //Почему-то падает приложение, если находить через butterknife
        idWordList = getArguments().getLong(ALL_WORDS_LEARNING_WORDS_FRAGMENT_ARGS); // Получаем аргумент фрагмента
        updateUI();
        Log.i(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.setting_allwords,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_allwords:
                SettingsBottomFragment.newInstance(idWordList).show(getActivity().getSupportFragmentManager(), TAG);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        // Проверяем, выучены или нет все слова. Если выучены, вся подборка объявляется выученной и появляется в Диктанте
        getWordsByIdSingle()
                .subscribe(list -> {
                    if (list.size() == 0) {
                        Log.i(TAG, "onDestroy: " + "if" + list.size());
                        database.wordsListDao().updateLearnedById(true, idWordList);
                    } else {
                        Log.i(TAG, "onDestroy: " + "else" + list.size());
                        database.wordsListDao().updateLearnedById(false, idWordList);
                    }
                });
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_all_words)
    void onClick(View view) {

        getWordsByIdSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (list.size() == 0) {
                        ToastInfomation.showToast(getActivity(), "Список выучен, переходите в Диктант");
                    } else {
                        if (listener != null) {
                            listener.allWordsFragmentOnClick();
                        }
                    }
                });
    }

    private void updateUI() {
        // Конвертируем LiveData to Flowable
        LiveData<List<Word>> wordsByIdWordListLD = viewModel.getWordsByIdWordListLD(idWordList);
        wordsByIdFlowable = Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(getActivity(), wordsByIdWordListLD));

        wordsByIdWordListLD.observe(this, list -> {
            AllWordsAdapter adapter = new AllWordsAdapter(list);
            rvAllWords.setAdapter(adapter);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvAllWords.setLayoutManager(linearLayoutManager);
        // Делаем горизонтальные линии между словами
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        rvAllWords.addItemDecoration(dividerItemDecoration);

        // Устанавливаем ItemTouchHelper для свайпа влево и право
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeController(idWordList, 2));
        itemTouchHelper.attachToRecyclerView(rvAllWords);
    }

    private Single<List<Word>> getWordsByIdSingle() {
        // Делаем Single для дальнейшего использования в onDestroy() и onClick()
        return wordsByIdFlowable
                .observeOn(Schedulers.io())
                .flatMapSingle(list ->
                        Flowable.fromIterable(list)
                                .filter(word -> !word.isLearned())
                                .toList())
                .first(new ArrayList<>());
    }
}
