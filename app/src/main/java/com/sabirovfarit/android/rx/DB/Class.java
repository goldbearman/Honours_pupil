package com.sabirovfarit.android.rx.DB;

import java.util.ArrayList;
import java.util.List;

public class Class {

   private List<Long> listId;

    public Class() {
        listId = new ArrayList<>();
    }

    public List<Long> getListId() {
        return listId;
    }

    public void setListId(List<Long> listId) {
        this.listId = listId;
    }
}
