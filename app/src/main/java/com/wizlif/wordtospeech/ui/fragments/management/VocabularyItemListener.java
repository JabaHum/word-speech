package com.wizlif.wordtospeech.ui.fragments.management;

import com.wizlif.wordtospeech.persistence.Vocabulary;

public interface VocabularyItemListener {
    void onDelete(Vocabulary vocabulary);
    void onEdit(Vocabulary vocabulary);
}
