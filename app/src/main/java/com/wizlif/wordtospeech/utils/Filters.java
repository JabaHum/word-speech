package com.wizlif.wordtospeech.utils;

import com.wizlif.wordtospeech.persistence.Vocabulary;

import java.util.List;

public class Filters {
    public static int indexOf(List<Vocabulary> vocabularies, Vocabulary vocabulary) {
        int index = -1;

        if(vocabularies != null && vocabulary != null) {
            for (int i = 0; i < vocabularies.size(); i++) {
                Vocabulary cVocabulary = vocabularies.get(i);
                if (cVocabulary.id == vocabulary.id) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    public Boolean contains(List<Vocabulary> vocabularies,String text){
        boolean contains = false;
        if(vocabularies != null && text != null){
            for(Vocabulary vocabulary:vocabularies){
                if(vocabulary.text.toLowerCase().equals(text.toLowerCase())){
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    public static Vocabulary indexOf(List<Vocabulary> vocabularies,String id){
        Vocabulary vocabulary = null;

        for(Vocabulary vocabulary1:vocabularies){
            if(vocabulary1.getId().equals(id)){
                vocabulary = vocabulary1;
                break;
            }
        }

        return vocabulary;
    }

    public static Vocabulary indexOfText(List<Vocabulary> vocabularies,String text){
        Vocabulary vocabulary = null;

        for(Vocabulary vocabulary1:vocabularies){
            if(vocabulary1.getText().toLowerCase().equals(text.toLowerCase())){
                vocabulary = vocabulary1;
                break;
            }
        }

        return vocabulary;
    }
}
