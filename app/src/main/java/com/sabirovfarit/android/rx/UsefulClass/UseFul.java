package com.sabirovfarit.android.rx.UsefulClass;

import android.text.InputFilter;

public class UseFul {

   public static InputFilter[] getMaxLenghtFilter(int maxLength) {
       InputFilter[] FilterArray = new InputFilter[1];
       FilterArray[0] = new InputFilter.LengthFilter(maxLength);
       return FilterArray;
   }
}
