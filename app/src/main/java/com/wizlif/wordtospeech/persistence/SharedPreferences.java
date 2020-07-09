package com.wizlif.wordtospeech.persistence;

import android.content.Context;

public class SharedPreferences {
    private static final String PREF_NAME = "text-speech";
    private static final String FIRST_RUN = "FIRST_RUN";

    android.content.SharedPreferences pref;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;


    // Shared preferences file name

    public SharedPreferences(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setIsFirstRun(Boolean isFirstRun) {
        pref.edit()
                .putBoolean(FIRST_RUN, isFirstRun)
                .apply();
    }

    public Boolean getIsFirstRun() {

        return pref.getBoolean(FIRST_RUN, true);

    }
}
