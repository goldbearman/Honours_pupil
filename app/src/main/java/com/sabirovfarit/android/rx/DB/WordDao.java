package com.sabirovfarit.android.rx.DB;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sabirovfarit.android.rx.WordCheckBox;

import java.util.List;


import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface WordDao {

    @Insert
    void add(Word word);

    @Insert
    void add(Word... word);

    @Insert
    void add(List<Word> words);

    @Insert
    List<Long> addReturnIds(List<Word> words);

    @Delete
    void delete(Word word);

    @Query("DELETE FROM words WHERE id = :id ")
    void deleteWordById(Long id);


    @Query("UPDATE words SET inList = :inList WHERE id = :id ")
    void updateInBaseById(long id, boolean inList);

    @Query("UPDATE words SET mLearned = :mLearned WHERE id = :id ")
    void updatemLearnedById(long id, boolean mLearned);

    @Query("UPDATE words SET stage1 = :trueOrFalse,stage2 = :trueOrFalse,stage3 = :trueOrFalse,stage4 = :trueOrFalse,mLearned = :trueOrFalse  WHERE id = :id ")
    void updatemStage1234andmLearnedById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage1 = :trueOrFalse,stage2 = :trueOrFalse,stage3 = :trueOrFalse,stage4 = :trueOrFalse  WHERE id = :id ")
    void updatemStage1234ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage1 = :trueOrFalse,stage2 = :trueOrFalse,stage3 = :trueOrFalse  WHERE id = :id ")
    void updatemStage123ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage1 = :trueOrFalse,stage2 = :trueOrFalse WHERE id = :id ")
    void updatemStage12ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage1 = :trueOrFalse WHERE id = :id ")
    void updatemStage1ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage2 = :trueOrFalse WHERE id = :id ")
    void updatemStage2ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage3 = :trueOrFalse WHERE id = :id ")
    void updatemStage3ById(long id, boolean trueOrFalse);

    @Query("UPDATE words SET stage4 = :trueOrFalse WHERE id = :id ")
    void updatemStage4ById(long id, boolean trueOrFalse);


    @Query("SELECT * FROM words ")
    List<Word> getAllWords();

    @Query("SELECT * FROM words ")
    Single<List<Word>> getAllWordsFlowable();

    @Query("SELECT * FROM words WHERE id = :id")
    Word getWordById(Long id);

    @Query("SELECT * FROM words WHERE id = :id")
    Flowable<Word> getWordByIdFlowable(Long id);


    @Query("SELECT * FROM words WHERE id IN (:ids)")
    List<Word> getWordsByIdList(List<Long> ids);

    @Query("SELECT * FROM words WHERE id IN (:ids)")
    LiveData<List<Word>> getWordsByIdLiveData(List<Long> ids);

    @Query("SELECT * FROM words WHERE id IN (:ids)")
    Single<List<Word>> getWordsByIdSingle(List<Long> ids);

    @Query("SELECT * FROM words WHERE id IN (:ids) ORDER BY value")
    Flowable<List<Word>> getWordsByIdFlowable(List<Long> ids);


    @Query("SELECT value,inList FROM words")
    Flowable<List<WordCheckBox>> getFlowWordsForSearch();

    @Query("SELECT * FROM words")
    Flowable<List<Word>> getWordsForSearch();


    @Query("SELECT * FROM words WHERE inList = :inList")
    LiveData<List<Word>> getAllWordsForSearchLiveData(boolean inList);
}
