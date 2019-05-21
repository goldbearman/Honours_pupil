package com.sabirovfarit.android.rx.DB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Word.class,WordsList.class},version = 1)
public abstract class AppDB extends RoomDatabase {

    public abstract WordDao wordDao();
    public abstract WordsListDao wordsListDao();
}
