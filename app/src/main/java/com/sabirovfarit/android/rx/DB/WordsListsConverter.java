package com.sabirovfarit.android.rx.DB;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordsListsConverter {

    @TypeConverter
    public String fromTable(List<Long> words) {

        StringBuilder sb = new StringBuilder();
        for (Long l : words) {
            sb.append(l).append(",");
        }
        return sb.toString();
    }

    @TypeConverter
    public List<Long> toTable(String data) {

        List<String> strings = Arrays.asList(data.split(","));
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            if (!strings.get(i).isEmpty()) {
            list.add(Long.parseLong(strings.get(i)));
            }
        }
        return list;
    }

}

