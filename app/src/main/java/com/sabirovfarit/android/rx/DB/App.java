package com.sabirovfarit.android.rx.DB;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;

import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation.Sound;
import com.sabirovfarit.android.rx.DictationFragment.WritingDictation.WritingDictation.SoundBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class App extends Application {

    private static final String TAG = "Lyy";

    private static App instance;
    private AppDB database;

    @Override
    public void onCreate() {
        super.onCreate();
        // Ловим ошибку в rxJava из WritingDictationFragment
        RxJavaPlugins.setErrorHandler(throwable -> {
        });

        instance = this;
        Log.i(TAG, "onCreate() : " + Thread.currentThread().getName());
        database = Room.databaseBuilder(this, AppDB.class, "database")
                .addCallback(new RoomDatabase.Callback() {   // Предварительно заполняем базу данных
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        fillList();
                        fillWordDao();
                    }
                })
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDB getDatabase() {
        return database;
    }

    private void fillList() {
        Log.i(TAG, "fillList: " + "start");
        Flowable.fromCallable(() -> (takeData()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle(list ->
                        Flowable.fromIterable(list)
                                .zipWith(Flowable.fromIterable(initSoundBox()), (word, sound) -> {
                                    word.setSoundWId(sound.getSoundId()); // Присваемваем word id звука из assets
                                    return word;
                                }).toList())
                .subscribe(words -> {
                            Log.i(TAG, "fillList: " + words.size());
                            App.getInstance().getDatabase().wordDao().add(words);
                        }
                );
    }

    private void fillWordDao() {
        Log.i(TAG, "fillWordDao: " + "Зашли");
        Flowable.defer(() -> Flowable.fromArray(takeData()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(words -> {

                    // Создаем списки для слов, относящихся к разным классам от 1 до 11. 0 класс будем игнорироваться
                    List<Class> classList = new ArrayList<>();
                    for (int i = 0; i < 12; i++) {
                        classList.add(new Class());
                    }

                    for (int i = 0; i < words.size(); i++) {
                        int wordsClass = words.get(i).getWordsClass();
                        long id = i + 1;
                        switch (wordsClass) {
                            case 1:
                                classList.get(1).getListId().add(id);
                                break;
                            case 2:
                                classList.get(2).getListId().add(id);
                                break;
                            case 3:
                                classList.get(3).getListId().add(id);
                                break;
                            case 4:
                                classList.get(4).getListId().add(id);
                                break;
                            case 5:
                                classList.get(5).getListId().add(id);
                                break;
                            case 6:
                                classList.get(6).getListId().add(id);
                                break;
                            case 7:
                                classList.get(7).getListId().add(id);
                                break;
                            case 8:
                                classList.get(8).getListId().add(id);
                                break;
                            case 9:
                                classList.get(9).getListId().add(id);
                                break;
                            case 10:
                                classList.get(10).getListId().add(id);
                                break;
                            case 11:
                                classList.get(11).getListId().add(id);
                                break;
                        }
                    }

                    Log.i(TAG, "firstClass: " + classList.get(1).getListId());
                    Log.i(TAG, "secondClass: " + classList.get(2).getListId());

                    //Создаем цвета подборок по классам
                    Integer[] color = {-291, -256, -32768, -23867, -65536, -4194560, -13582592, -65408, -65281, -16728065, -1677696, -8388353};
                    List<Integer> listColor = new ArrayList<>(Arrays.asList(color));

                    for (int i = 1; i < 12; i++) {
                        if (classList.get(i).getListId().size() > 0) {
                            App.getInstance().getDatabase().wordsListDao().add(new WordsList(i + " КЛАСС", listColor.get(i), classList.get(i).getListId(), true, false));
                        }
                    }
                });
    }

    //Из списка в assets заполняем таблиу
    private List<Word> takeData() {
        Log.i(TAG, "takeData: " + "start");
        Log.i(TAG, "takeData: " + Thread.currentThread());
        List<Word> list = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(App.this.getAssets().open("words.csv"));
            BufferedReader reader = new BufferedReader(isr);
            reader.read();
            String line;
            String[] st;
            while ((line = reader.readLine()) != null) {
                st = line.split(",");
                Word word = new Word();
                word.setValue(st[0]);

                // Преобазуем полученный String в long номера проблемных букв
                List<Long> listLetter = new ArrayList<>();
                for (int i = 0; i < st[1].length(); i++) {
                    char letter = st[1].charAt(i);
                    long g = Character.getNumericValue(letter);
                    listLetter.add(g - 1);
                }

                word.setDiffLetter(listLetter);
                word.setWordsClass(Integer.parseInt(st[2]));
                list.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "doInBackground: " + "end ");
        }
        return list;
    }

    // Получаем список звуков
    private List<Sound> initSoundBox() {
        SoundBox soundBox = new SoundBox(this);
        return soundBox.getSounds();
    }

}



























