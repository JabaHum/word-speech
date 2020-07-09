package com.wizlif.wordtospeech.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;

import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.persistence.VocabularyDataSource;
import com.wizlif.wordtospeech.ui._base.BaseViewModel;
import com.wizlif.wordtospeech.utils.Filters;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VocabularyViewModel extends BaseViewModel {

    private final VocabularyDataSource vocabularyDataSource;
    private MediatorLiveData<Resource<List<Vocabulary>>> vocabularyData;
    private List<Vocabulary> vocabularyList = new ArrayList<>();

    @Inject
    public VocabularyViewModel(VocabularyDataSource vocabularyDataSource){
        this.vocabularyDataSource = vocabularyDataSource;
    }

    public LiveData<Resource<List<Vocabulary>>> observeVocabulariesData() {

        if (vocabularyData == null) {
            vocabularyData = new MediatorLiveData<>();
            loadFromDB();
        }

        return vocabularyData;
    }

    private void loadFromDB() {
        vocabularyData.setValue(Resource.loading(null));

        final LiveData<Resource<List<Vocabulary>>> source = LiveDataReactiveStreams.fromPublisher(
                vocabularyDataSource.getVocabularies()
                        .onErrorReturn(error -> new ArrayList<Vocabulary>() {{

                        }})
                        .map(vocabularyData -> {
                            vocabularyList = vocabularyData;
                            return Resource.success(vocabularyData);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        );

        vocabularyData.addSource(source, expensesResource -> {
            vocabularyData.setValue(expensesResource);
            vocabularyData.removeSource(source);
        });
    }

    public void addVocabulary(Vocabulary vocabulary){
        if(vocabularyData != null && vocabulary != null){
            vocabularyList.add(vocabulary);
            vocabularyData.postValue(Resource.success(vocabularyList));
        }
    }

    public void updateVocabulary(Vocabulary vocabulary){
        if(vocabularyData != null && vocabulary != null){

            int index = Filters.indexOf(vocabularyList,vocabulary);
            if(index != -1){
                vocabularyList.set(index,vocabulary);
                vocabularyData.postValue(Resource.success(vocabularyList));
            }
        }
    }

    public void deleteVocabulary(Vocabulary vocabulary){
        if(vocabularyData != null && vocabulary != null){

            int index = Filters.indexOf(vocabularyList,vocabulary);
            if(index != -1){
                vocabularyList.remove(index);
                vocabularyData.postValue(Resource.success(vocabularyList));
            }
        }
    }

    public VocabularyDataSource getVocabularyDataSource() {
        return vocabularyDataSource;
    }

    public void setVocabularyData(List<Vocabulary> vocabularyData) {
        if(this.vocabularyData == null){
            this.vocabularyData = new MediatorLiveData<>();
        }
        vocabularyList = vocabularyData;
        this.vocabularyData.setValue(Resource.success(vocabularyList));
    }

    public void reloadVocabularyData() {
        if(this.vocabularyData == null){
            this.vocabularyData = new MediatorLiveData<>();
        }

        loadFromDB();
    }
}
