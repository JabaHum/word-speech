package com.wizlif.wordtospeech;


import com.wizlif.wordtospeech.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class WordToSpeech extends DaggerApplication {

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {


        return DaggerAppComponent.builder()
                .application(this)
                .build();
    }

}