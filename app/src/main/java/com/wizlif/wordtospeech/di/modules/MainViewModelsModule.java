package com.wizlif.wordtospeech.di.modules;

import androidx.lifecycle.ViewModel;


import com.wizlif.wordtospeech.di.ViewModelKey;
import com.wizlif.wordtospeech.ui.VocabularyViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(VocabularyViewModel.class)
    public abstract ViewModel bindVocabularyViewModel(VocabularyViewModel viewModel);

}
