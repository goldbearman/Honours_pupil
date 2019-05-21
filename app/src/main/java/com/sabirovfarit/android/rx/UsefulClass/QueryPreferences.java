package com.sabirovfarit.android.rx.UsefulClass;

import android.content.Context;
import android.preference.PreferenceManager;

import com.sabirovfarit.android.rx.R;

public class QueryPreferences {

    public static final String PREF_COLOR = "pref color";
    public static final String PREF_NAME = "pref name";
    public static final String PREF_EMAIL = "pref email";
    public static final String PREF_CLASS = "pref class";
    public static final String PREF_THEME = "pref theme";
    public static final String PREF_PHOTO = "pref photo";
    public static final String PREF_TIME = "pref_time";
    public static final String PREF_WORD_COUNT = "pref WordCount";
    public static final String PREF_DICTATIONS_WRITTEN = "pref DictationsWritten";

    public static int getColorButton(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_COLOR, -256);
    }

    public static void setColorButton(Context context,int color) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_COLOR,color)
                .apply();
    }

    public static String getClass(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_CLASS, null);
    }

    public static void setClass(Context context,String s) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CLASS,s)
                .apply();
    }


    public static String getName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_NAME, null);
    }

    public static void setName(Context context,String s) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_NAME,s)
                .apply();
    }

    public static String getEmail(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EMAIL, null);
    }

    public static void setEmail(Context context,String s) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_EMAIL,s)
                .apply();
    }
    public static int getLastTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_THEME, R.style.GreenTheme);
    }

    public static void setLastTheme(Context context,int theme) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_THEME,theme)
                .apply();
    }

    public static long getTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PREF_TIME, 7L);
    }

    public static void setTime(Context context,long time) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_TIME,time)
                .apply();
    }

    public static int getLearnedWordCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_WORD_COUNT, 0);
    }

    public static void setLearnedWordCount(Context context,int learnedWordCount) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_WORD_COUNT,learnedWordCount)
                .apply();
    }
    public static int getDictationsWritten(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_DICTATIONS_WRITTEN, 0);
    }

    public static void setDictationsWritten(Context context,int dictationsWritten) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_DICTATIONS_WRITTEN,dictationsWritten)
                .apply();
    }



}
