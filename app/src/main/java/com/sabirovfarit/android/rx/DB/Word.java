package com.sabirovfarit.android.rx.DB;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@Entity(tableName = "words")
public class Word {

    @PrimaryKey(autoGenerate = true)
    private long Id;

    private String value;                      //Само слова

    @TypeConverters({WordsListsConverter.class})
    private List<Long> diffLetter;                    //Сложные буквы Difficult

    private int wordsClass;                    // К какому классу относится слово

//    private int emphLetter;                    //Номер буквы ударения Emphasis

    private boolean stage1;                  //Слово прочитано
    private boolean stage2;                  //Правильно вставлена одна буква
    private boolean stage3;                  //Правильно собраны все буквы
    private boolean stage4;                  //Правильно написано слово

    private boolean mLearned;                  //Выучено ли слово

    private boolean inList;                    //Добавлено ли в подборку

    //    private int sound1;                        //Звук произошение с выделением сложной буквы
    private int soundWId;                         //Звук обычного произношения слова


    public Word() {
    }

    public Word(String value, List<Long> diffLetter, int wordsClass, boolean inList, int soundWId) {
        this.value = value;
        this.diffLetter = diffLetter;
        this.wordsClass = wordsClass;
        this.inList = inList;
        this.soundWId = soundWId;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Long> getDiffLetter() {
        return diffLetter;
    }

    public int getWordsClass() {
        return wordsClass;
    }

    public void setWordsClass(int wordsClass) {
        this.wordsClass = wordsClass;
    }

    public void setDiffLetter(List<Long> diffLetter) {
        this.diffLetter = diffLetter;
    }

    public boolean isStage1() {
        return stage1;
    }

    public void setStage1(boolean stage1) {
        this.stage1 = stage1;
    }

    public boolean isStage2() {
        return stage2;
    }

    public void setStage2(boolean stage2) {
        this.stage2 = stage2;
    }

    public boolean isStage3() {
        return stage3;
    }

    public void setStage3(boolean stage3) {
        this.stage3 = stage3;
    }

    public boolean isStage4() {
        return stage4;
    }

    public void setStage4(boolean stage4) {
        this.stage4 = stage4;
    }

    public boolean isLearned() {
        return mLearned;
    }

    public void setLearned(boolean learned) {
        mLearned = learned;
    }

    public boolean isInList() {
        return inList;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }


    //    public int getSound1() {
//        return sound1;
//    }
//
//    public void setSound1(int sound1) {
//        this.sound1 = sound1;
//    }
//
    public int getSoundWId() {
        return soundWId;
    }

    public void setSoundWId(int soundWId) {
        this.soundWId = soundWId;
    }
}
