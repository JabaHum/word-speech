package com.wizlif.wordtospeech.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Vocabulary.class}, version = 1, exportSchema = false)
public abstract class VocabularyDatabase extends RoomDatabase {

    public abstract VocabularyDao vocabularyDao();

    public static VocabularyDatabase getInstance(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                VocabularyDatabase.class, "vocabularies.db")
                .build();
    }
}
