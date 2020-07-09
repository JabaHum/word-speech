package com.wizlif.wordtospeech.di;

import com.wizlif.wordtospeech.di.modules.MainFragmentBuildersModule;
import com.wizlif.wordtospeech.di.modules.MainViewModelsModule;
import com.wizlif.wordtospeech.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = {
            MainViewModelsModule.class,
            MainFragmentBuildersModule.class
    })
    abstract MainActivity contributeMainActivity();

}
