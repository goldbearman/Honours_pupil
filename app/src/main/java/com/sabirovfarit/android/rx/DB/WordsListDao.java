package com.sabirovfarit.android.rx.DB;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface WordsListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WordsList wordsList);

    @Insert
    void add(WordsList wordsList);

    @Insert
    void add(WordsList... wordsList);

    @Insert
    void add(List<WordsList> wordsLists);

    @Delete
    int delete(WordsList wordsList);

    @Update
    void update(WordsList wordsList);

    @Query("UPDATE wordslist SET listId = :list WHERE id = :id")
    void updateWordListIdById(List<Long> list, long id);

    @Query("UPDATE wordslist SET learned = :learned WHERE id = :id")
    void updateLearnedById(boolean learned, long id);

    //Почему-то не работает!!!
    @Query("UPDATE wordslist SET listId = :list WHERE id = :id")
    void updateListIdById(List<Long> list, long id);

    @Query("SELECT * FROM words")
    Single<List<Word>> getAllWards();

    @Query("SELECT * FROM wordslist WHERE id = :id")
    Flowable<WordsList> getFlowableIdsFromListId(long id);

    // Почему-то не работает
//    @Query("SELECT listId FROM wordslist WHERE id = :id")
//    LiveData<List<Long>> getListIdFromWordListLD(long id);

    // Почему-то не работает
//    @Query("SELECT listId FROM wordslist WHERE id = :id")
//    List<Long> getListIdFromWordListFlowable(long id);


    @Query("SELECT * FROM wordslist WHERE id = :id")
    Single<WordsList> getWordListByIdSingle(long id);

    @Query("SELECT * FROM wordslist WHERE id = :id")
    LiveData<WordsList> getWordListByIdLiveData(long id);

    @Query("SELECT * FROM wordslist WHERE id = :id")
    WordsList getWordListById(long id);

    @Query("DELETE FROM wordslist")
    void deleteAll();

    @Query("SELECT*FROM wordslist WHERE inClass = :b")
    LiveData<List<WordsList>> getOnInClass(boolean b);

    @Query("SELECT*FROM wordslist WHERE learned = :b")
    LiveData<List<WordsList>> getLernedLD(boolean b);


}
