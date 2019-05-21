package com.sabirovfarit.android.rx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.sabirovfarit.android.rx.DictationFragment.DictationFragment;
import com.sabirovfarit.android.rx.LearningFragment.LearningFragment;
import com.sabirovfarit.android.rx.SearchFragment.BottomFragments.CreateNewListBottomFragment;
import com.sabirovfarit.android.rx.SearchFragment.SearchFragment;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;


public class MainActivity extends AppCompatActivity implements LearningFragment.LearningFragmentListener {

    public static BottomNavigationView mBottomNavigationView;
    private LearningFragment learningFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private CreateNewListBottomFragment createNewListBottomFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Устанавливаем тему, выбранную ранее
        setTheme(QueryPreferences.getLastTheme(this));
        setContentView(R.layout.activity_main);

        // Смена темы плавная снизу вверх
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        mBottomNavigationView = findViewById(R.id.navigation);
        learningFragment = LearningFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();
        searchFragment = SearchFragment.newInstance(0);
//        createNewListBottomFragment = CreateNewListBottomFragment.newInstance();

        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_profile);

//        CreateNewListBottomFragment.newInstance().show(getActivity().getSupportFragmentManager(), NEW_LIST_BOTTOM_FRAGMENT);

//        //Фрагмент, зарзужа
//        loadFragment(profileFragment, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Обратный вызов из WordsListGradeLearnAdapter, проброшенный через LearningFragment с передачей idWordList
        learningFragment.setListenerLearningFragment(this);

    }

    @Override
    protected void onStop() {
        // Отписываемся от обратного вызова WordsListGradeLearnAdapter, проброшенного через LearningFragment
        learningFragment.setListenerLearningFragment(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_container, fragment);
            // Добавляем когда нужно BackStack
            if (addToBackStack) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    loadFragment(profileFragment, false);
                    return true;
                case R.id.navigation_search:
                    loadFragment(searchFragment, false);
                    return true;
                case R.id.navigation_learn:
                    loadFragment(learningFragment, false);
                    return true;
                case R.id.navigation_dictation:
                    loadFragment(DictationFragment.newInstance(), false);
                    return true;
            }
            return false;
        }
    };

    // Дважды используем этот метод
    @Override
    public void learningFragmentOnClick(long idWordList, int j) {
        if (idWordList == 0 && j == 1) {
            //Включаем BottomNavigationView.
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            // Отключаем BottomNavigationView.
            mBottomNavigationView.setVisibility(View.GONE);
            // При нажатии на элемент списка WordsListGradeLearnAdapter(Подборка по классам)
            // двойным обратным вызовом через LearningFragment устанавливаем SeeGradeWordsFragment
            loadFragment(SearchFragment.newInstance(idWordList), true);
        }
    }
}
