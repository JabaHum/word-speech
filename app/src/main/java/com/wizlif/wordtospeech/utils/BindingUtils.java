package com.wizlif.wordtospeech.utils;

import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.ui.fragments.management.VocabulariesAdapter;

import java.util.List;

public final class BindingUtils {

    @BindingAdapter({"vocabularies"})
    public static void setVocabularies(RecyclerView recyclerView, List<Vocabulary> vocabularies) {
        VocabulariesAdapter adapter = (VocabulariesAdapter) recyclerView.getAdapter();
        if (adapter != null && vocabularies != null) {
            adapter.clearItems();
            adapter.addVocabulary(vocabularies);
            adapter.notifyDataSetChanged();
        }
    }

    @BindingAdapter({"image"})
    public static void setImage(ImageView imageView, Number id) {
        if (id != null) {
            try {
                imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), id.intValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
