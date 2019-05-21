package com.sabirovfarit.android.rx.LearningFragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sabirovfarit.android.rx.LearningFragment.LearningWords.LearningWordsActivity;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.InformationDialog;
import com.sabirovfarit.android.rx.WordViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class LearningFragment extends Fragment implements WordsListLearnAdapter.ListnerAllWords, WordsListGradeLearnAdapter.ListnerAllWords {

    public static final String LEARNING_FRAGMENT_TAG = "LearningFragment tag";
    RecyclerView rvMyList;
    RecyclerView rvListClass;
    WordViewModel viewModel;
    public WordsListGradeLearnAdapter adapterGradeList;
    WordsListLearnAdapter adapterMyList;

    //Будет использован дважды
    LearningFragmentListener listener;

    public interface LearningFragmentListener {
        void learningFragmentOnClick(long idWordList, int j);
    }

    public void setListenerLearningFragment(LearningFragmentListener listener) {
        this.listener = listener;
    }

    public LearningFragment() {
        // Required empty public constructor
    }

    public static LearningFragment newInstance() {
        Bundle args = new Bundle();
        LearningFragment fragment = new LearningFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learning, container, false);
        rvMyList = view.findViewById(R.id.rv_my_list);
        rvListClass = view.findViewById(R.id.rvListClass);
        updateListGrade();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.information, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.information:
                // Выводим информацию
                InformationDialog.newInstance(getString(R.string.information_learning_fragment))
                        .show(getFragmentManager(), LEARNING_FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // При первом использоании отключаем BottomNavigation, когда SearchFragment закроется(второе использование), нам нужно будет включить BottomNavigation
        // Второе ипользование
        // Подписываем слушателя(MainActivity), чтобы он включил BottomNavigationView
        if (listener != null) {
            listener.learningFragmentOnClick(0, 1);
        }
        updateMyList();
    }

    @Override
    public void onStop() {
        adapterMyList.setListnerAllWords(null);
        super.onStop();
    }

    //Заполняем RecycleView
    private void updateListGrade() {
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        viewModel.getInClassTrue().observe(this, wordsLists -> {

            adapterGradeList = new WordsListGradeLearnAdapter(wordsLists);
            adapterGradeList.setListnerAllWords(this);
            rvListClass.setAdapter(adapterGradeList);
        });

        GridLayoutManager gridlayoutManager = new GridLayoutManager(getActivity(), 3);
        rvListClass.setLayoutManager(gridlayoutManager);
    }

    private void updateMyList() {
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        viewModel.getInClassFalse().observe(this, wordsLists -> {

            adapterMyList = new WordsListLearnAdapter(wordsLists);
            adapterMyList.setListnerAllWords(this);
            rvMyList.setAdapter(adapterMyList);
        });

        GridLayoutManager gridlayoutManager = new GridLayoutManager(getActivity(), 3);
        rvMyList.setLayoutManager(gridlayoutManager);
    }

    // Прослушиваем нажатие на пункт из WordsListLearnAdapter и передаем id нажатой стоки таблицы WordsList
    @Override
    public void onClick(long idWordList) {
        Intent intent = LearningWordsActivity.newIntent(getActivity(), idWordList);
        startActivity(intent);
    }

    // Прослушиваем нажатие на пункт из WordsListGradeLearnAdapter и передаем id нажатой стоки таблицы WordsList
    @Override
    public void onClickGrade(long idWordList) {
        // Первое использование
        // Подписываем слушателя(MainActivity) на вызов из WordsListGradeLearnAdapter,
        // чтобы он в SearchFragment(второе использование) открыл нам список Word, id которых хронится в listId(таблица WordsList)
        // и отключил BottomNavigationView
        if (listener != null) {
            listener.learningFragmentOnClick(idWordList, 0);
        }
    }
}
