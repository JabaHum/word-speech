package com.wizlif.wordtospeech.persistence;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalVocabularyDataSource implements VocabularyDataSource {

    private final VocabularyDao mVocabularyDao;

    public LocalVocabularyDataSource(VocabularyDao userDao) {
        mVocabularyDao = userDao;
    }

//    @Override
//    public Flowable<Vocabulary> getVocabulary() {
//        return mVocabularyDao.get;
//    }

    @Override
    public Flowable<List<Vocabulary>> getVocabularies() {
        return mVocabularyDao.getAll();
    }

    @Override
    public Single<Long> insertVocabulary(Vocabulary vocabulary) {
        return mVocabularyDao.insertVocabulary(vocabulary);
    }

    @Override
    public void deleteAllVocabularies() {
        mVocabularyDao.deleteAllVocabularies();
    }

    @Override
    public Single<List<Long>> insertAll(Vocabulary[] vocabularies) {
        return mVocabularyDao.insertAll(vocabularies);
    }

    @Override
    public Completable update(String id, String text) {
        return mVocabularyDao.update(id,text);
    }

    @Override
    public Completable delete(String id) {
        return mVocabularyDao.delete(id);
    }
}
