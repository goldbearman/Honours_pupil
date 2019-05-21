package com.sabirovfarit.android.rx;

import android.arch.persistence.room.ColumnInfo;

public class WordCheckBox {


    private String value;                      //Само слова

    private boolean inList;                    //Добавлено ли в подборку

    public WordCheckBox(String value, boolean inList) {
        this.value = value;
        this.inList = inList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInList() {
        return inList;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }
}
