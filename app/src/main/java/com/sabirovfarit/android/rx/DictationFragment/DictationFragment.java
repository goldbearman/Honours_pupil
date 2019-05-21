package com.sabirovfarit.android.rx.DictationFragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictationActivity;
import com.sabirovfarit.android.rx.LearningFragment.WordsListLearnAdapter;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.InformationDialog;
import com.sabirovfarit.android.rx.WordViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class DictationFragment extends Fragment implements WordsListLearnAdapter.ListnerAllWords {

    private static final String TAG = "DictationFragment";
    public static final String DICTATION_FRAGMENT_TAG = "DictationFragment tag";

    WordViewModel viewModel;
    WordsListLearnAdapter adapter;
    RecyclerView recyclerView;


    public DictationFragment() {
        // Required empty public constructor
    }

    public static DictationFragment newInstance() {
        Bundle args = new Bundle();
        DictationFragment fragment = new DictationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictation, container, false);
        recyclerView = view.findViewById(R.id.rv_dictation_fr);
        updateMyList();
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
                InformationDialog.newInstance(getString(R.string.information_dictation_fragment))
                        .show(getFragmentManager(), DICTATION_FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        updateMyList();
        super.onStart();
    }

    @Override
    public void onStop() {
        adapter.setListnerAllWords(null);
        super.onStop();
    }

    // Обновляем список подборок для написания диктанта
    private void updateMyList() {
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        viewModel.getLernedTrueLD().observe(this, wordsLists -> {
            adapter = new WordsListLearnAdapter(wordsLists);
            adapter.setListnerAllWords(this);
            recyclerView.setAdapter(adapter);
        });

        GridLayoutManager gridlayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridlayoutManager);
    }

    @Override
    public void onClick(long idWordList) {
        Log.i(TAG, "onClick: ");
        Intent intent = WritingDictationActivity.newIntent(getActivity(), idWordList);
        startActivity(intent);
    }
}
