package com.wizlif.wordtospeech.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface VocabularyDao {
    @Query("SELECT * FROM vocabulary")
    Flowable<List<Vocabulary>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertVocabulary(Vocabulary vocabulary);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(Vocabulary[] vocabularies);

    @Query("DELETE FROM vocabulary WHERE id = :id")
    Completable delete(String id);

    @Query("UPDATE vocabulary SET vocabulary_text = :text WHERE id = :id")
    Completable update(String id, String text);


    @Query("DELETE FROM vocabulary")
    void deleteAllVocabularies();

    @Query("SELECT COUNT(*) FROM vocabulary")
    Flowable<Integer> getRowCount();
}
