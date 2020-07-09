package com.wizlif.wordtospeech.persistence;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface VocabularyDataSource {

    /**
     * Gets the user from the data source.
     *
     * @return the user from the data source.
     */
//    Flowable<Vocabulary> getVocabulary();

    Flowable<List<Vocabulary>> getVocabularies();


    Single<Long> insertVocabulary(Vocabulary vocabulary);

    /**
     * Deletes all users from the data source.
     */
    void deleteAllVocabularies();

    Single<List<Long>> insertAll(Vocabulary[] vocabularies);

    Completable update(String id, String text);

    Completable delete(String id);
}
