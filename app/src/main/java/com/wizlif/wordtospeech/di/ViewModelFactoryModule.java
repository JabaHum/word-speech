package com.wizlif.wordtospeech.di;

import androidx.lifecycle.ViewModelProvider;


import com.wizlif.wordtospeech.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);
}
