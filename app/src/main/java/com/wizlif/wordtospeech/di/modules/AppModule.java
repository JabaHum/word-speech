package com.wizlif.wordtospeech.di.modules;

import android.app.Application;
import android.content.Context;

import com.wizlif.wordtospeech.persistence.LocalVocabularyDataSource;
import com.wizlif.wordtospeech.persistence.SharedPreferences;
import com.wizlif.wordtospeech.persistence.VocabularyDataSource;
import com.wizlif.wordtospeech.persistence.VocabularyDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    static VocabularyDataSource provideUserDataSource(VocabularyDatabase database) {
        return new LocalVocabularyDataSource(database.vocabularyDao());
    }

    @Singleton
    @Provides
    static VocabularyDatabase database(Application context){
        return VocabularyDatabase.getInstance(context);
    }

    @Singleton
    @Provides
    static SharedPreferences sharedPreferences(Application context){
        return new SharedPreferences(context);
    }

}
