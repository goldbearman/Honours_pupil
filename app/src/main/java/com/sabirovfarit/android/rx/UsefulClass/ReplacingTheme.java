package com.sabirovfarit.android.rx.UsefulClass;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;

public class ReplacingTheme {


    public static void replace(Activity activity, View view) {
         int theme = 0 ;
        switch (view.getId()) {
            case R.id.btn_theme_yellow:
//                QueryPreferences.setLastTheme(activity, R.style.YellowTheme);
                theme = R.style.YellowTheme;
                break;
            case R.id.btn_theme_orange:
//                QueryPreferences.setLastTheme(activity, R.style.OrangeTheme);
                theme = R.style.OrangeTheme;
                break;
            case R.id.btn_theme_red:
//                QueryPreferences.setLastTheme(activity, R.style.RedTheme);
                theme = R.style.RedTheme;
                break;
            case R.id.btn_theme_green:
//                QueryPreferences.setLastTheme(activity, R.style.GreenTheme);
                theme = R.style.GreenTheme;
                break;
            case R.id.btn_theme_dark_blue:
//                QueryPreferences.setLastTheme(activity, R.style.DarkBlueTheme);
                theme = R.style.DarkBlueTheme;
                break;
            default:
                break;
        }
        if (theme != QueryPreferences.getLastTheme(activity)) {
            QueryPreferences.setLastTheme(activity, theme);
            activity.finish();
            activity.startActivity(new Intent(activity, activity.getClass()));
        }
    }
}