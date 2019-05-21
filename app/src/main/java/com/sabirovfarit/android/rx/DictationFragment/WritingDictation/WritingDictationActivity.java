package com.sabirovfarit.android.rx.DictationFragment.WritingDictation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation.WritingDictationFragment;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.R;

public class WritingDictationActivity extends AppCompatActivity implements AllWordsWritingDictationFragment.AllWordsWritingDictationFragmentListener {

    public static final String WRITING_DICTATION_ACTIVITY_KEY = "WritingDictationActivity key";

    private AllWordsWritingDictationFragment allWordsWritingDictationFragment;
    private long idWordList;

    public static Intent newIntent(Context context, long id) {
        Intent intent = new Intent(context, WritingDictationActivity.class);
        intent.putExtra(WRITING_DICTATION_ACTIVITY_KEY, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(QueryPreferences.getLastTheme(this));
        setContentView(R.layout.activity_container);

        idWordList = getIntent().getLongExtra(WRITING_DICTATION_ACTIVITY_KEY, 0L);
        allWordsWritingDictationFragment = AllWordsWritingDictationFragment.newInstance(idWordList);
        loadFragment(allWordsWritingDictationFragment, true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        allWordsWritingDictationFragment.setListener(this);
    }

    @Override
    protected void onStop() {
        allWordsWritingDictationFragment.setListener(null);
        super.onStop();
    }

    protected void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            // Добавляем когда нужно BackStack
            if (addToBackStack) {
                ft.addToBackStack(null);
            }
            fm.addOnBackStackChangedListener(() -> {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                }
            });
            ft.commit();
        }
    }

    @Override
    public void AllWordsWritingDictationFragmentOnClick(long id) {
        loadFragment(WritingDictationFragment.newInstance(idWordList),true);
    }
}
