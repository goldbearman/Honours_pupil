package com.sabirovfarit.android.rx;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.DB.AppDB;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.DB.WordsList;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class WordViewModel extends ViewModel {

    private static final String TAG = "WordViewModel";
//
//    private final MutableLiveData<List<Long>> selected = new MutableLiveData<List<Long>>();

    private List<Long> idsList;

    private LiveData<List<Word>> wordLiveData;
    private LiveData<List<WordsList>> ldLearnInClassTrue;
    private LiveData<List<WordsList>> ldLearnInClassFalse;
    private LiveData<List<WordsList>> learnedTrueLD;
    private AppDB database = App.getInstance().getDatabase();


    private final MutableLiveData<List<Long>> listIdLiveData = new MutableLiveData<>();

    public void setListIdLiveData(List<Long> list) {
        listIdLiveData.postValue(list);
    }

    public LiveData<List<Word>> getListWordLiveData() {
        LiveData<List<Word>> wordsByIdLiveData = database.wordDao().getWordsByIdLiveData(listIdLiveData.getValue());
        return wordsByIdLiveData;
    }

    public LiveData<List<Word>> getData() {
        if (wordLiveData == null) {
            wordLiveData = database.wordDao().getAllWordsForSearchLiveData(false);
        }
        return wordLiveData;
    }


    public LiveData<WordsList> getWordsListByIdLiveData(long idWordsList) {
        return database.wordsListDao().getWordListByIdLiveData(idWordsList);
    }

    public LiveData<List<Word>> getWordsByIdWordListLD(long idWordsList) {
        Flowable<List<Word>> flowable = database.wordsListDao().getFlowableIdsFromListId(idWordsList)
                .observeOn(Schedulers.io())
                .map(wordsList -> wordsList.getListId())
                .flatMap(list -> database.wordDao().getWordsByIdFlowable(list));

        LiveData<List<Word>> wordsByIdWordListLD = LiveDataReactiveStreams.fromPublisher(flowable);
        return wordsByIdWordListLD;
    }

    public LiveData<List<WordsList>> getInClassTrue() {
        if (ldLearnInClassFalse == null) {
            ldLearnInClassFalse = database.wordsListDao().getOnInClass(true);
        }
        return ldLearnInClassFalse;
    }

    public LiveData<List<WordsList>> getInClassFalse() {
        if (ldLearnInClassTrue == null) {
            ldLearnInClassTrue = database.wordsListDao().getOnInClass(false);
        }
        return ldLearnInClassTrue;
    }

    public LiveData<List<WordsList>> getLernedTrueLD() {
        if (learnedTrueLD == null) {
            learnedTrueLD = database.wordsListDao().getLernedLD(true);
        }
        return learnedTrueLD;
    }

    public List<Long> getIdsList() {
        return idsList;
    }

    public void setIdsList(List<Long> idsList) {
        this.idsList = idsList;
    }


}
