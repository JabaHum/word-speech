package com.wizlif.wordtospeech.di.modules;

import com.wizlif.wordtospeech.ui.fragments.MainFragment;
import com.wizlif.wordtospeech.ui.fragments.management.ManagementFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MainFragment contributeMainFragment();

    @ContributesAndroidInjector
    abstract ManagementFragment contributeManagementFragment();

}
