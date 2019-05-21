package com.sabirovfarit.android.rx.DB;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@Entity
public class WordsList {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private int color;

    @TypeConverters({WordsListsConverter.class})
    private List<Long> listId;

    private boolean inClass;

    private boolean learned;


    public WordsList(String name, int color, List<Long> listId, boolean inClass, boolean learned) {
        this.name = name;
        this.color = color;
        this.listId = listId;
        this.inClass = inClass;
        this.learned = learned;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Long> getListId() {
        return listId;
    }

    public void setListId(List<Long> listId) {
        this.listId = listId;
    }

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
    }

    public boolean isInClass() {
        return inClass;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }
}
