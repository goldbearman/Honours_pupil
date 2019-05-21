package com.sabirovfarit.android.rx.LearningFragment.LearningWords;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.WordViewModel;

public class LearningWordsActivity extends AppCompatActivity implements AllWordsLearningWordsFragment.AllWordsFragmentListener, FiveWordsFragment.FiveWordsFragmentListener {

    public static final String LEARNING_WORDS_ACTIVITY_KEY = "LearningWordsActivity key";

    private AllWordsLearningWordsFragment allWordsLearningWordsFragment;
    private FiveWordsFragment fiveWordsFragment;
    private FourStepsFragment mFourStepsFragment;

    private WordViewModel viewModel;
    long idWordList;

    public static Intent newIntent(Context context, long id) {
        Intent intent = new Intent(context, LearningWordsActivity.class);
        intent.putExtra(LEARNING_WORDS_ACTIVITY_KEY, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(QueryPreferences.getLastTheme(this));
        setContentView(R.layout.activity_container);

        viewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        idWordList = getIntent().getLongExtra(LEARNING_WORDS_ACTIVITY_KEY, 0L);

        fiveWordsFragment = FiveWordsFragment.newInstance(idWordList);
        allWordsLearningWordsFragment = AllWordsLearningWordsFragment.newInstance(idWordList);
        loadFragment(allWordsLearningWordsFragment, true);

        // Меняем заголовок ActionBar
        viewModel.getWordsListByIdLiveData(idWordList)
                .observe(this,wordsList -> this.setTitle("Подборка: "+wordsList.getName()));
    }


    @Override
    protected void onStart() {
        allWordsLearningWordsFragment.setListener(this);
        fiveWordsFragment.setListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        allWordsLearningWordsFragment.setListener(null);
        fiveWordsFragment.setListener(null);
        super.onStop();
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_container, fragment);
            // Добавляем когда нужно BackStack
            if (addToBackStack) {
                ft.addToBackStack(null);
            }
            fm.addOnBackStackChangedListener(()->{
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                }
            });
            ft.commit();
        }
    }

    @Override
    public void allWordsFragmentOnClick() {
        loadFragment(fiveWordsFragment, true);
    }


    @Override
    public void fiveWordsFragmentOnClick() {
        mFourStepsFragment = FourStepsFragment.newInstance();
        loadFragment(mFourStepsFragment, true);
    }
}
