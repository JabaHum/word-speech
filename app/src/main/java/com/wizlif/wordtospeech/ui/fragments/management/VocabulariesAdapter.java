package com.wizlif.wordtospeech.ui.fragments.management;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wizlif.wordtospeech.R;
import com.wizlif.wordtospeech.databinding.ItemVocabularyBinding;
import com.wizlif.wordtospeech.databinding.TemplateNoItemBinding;
import com.wizlif.wordtospeech.persistence.VIEW_TYPE;
import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.ui._base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;



public class VocabulariesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<Vocabulary> vocabularyList;
    private final VocabularyItemListener listener;

    public VocabulariesAdapter(VocabularyItemListener listener) {

        this.vocabularyList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                ItemVocabularyBinding itemVocabularyBinding = ItemVocabularyBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new VocabularyViewHolder(itemVocabularyBinding);
            case 1:
            default:
                TemplateNoItemBinding noFarmPlanBinding = TemplateNoItemBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EmptyViewHolder(noFarmPlanBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (vocabularyList.isEmpty()) {
            return 1;
        } else {
            return vocabularyList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (vocabularyList.isEmpty()) {
            return VIEW_TYPE.EMPTY.ordinal();
        } else {
            return VIEW_TYPE.NORMAL.ordinal();
        }
    }

    // Main ViewHolder
    public class VocabularyViewHolder extends BaseViewHolder {

        private final ItemVocabularyBinding mBinding;

        VocabularyViewHolder(ItemVocabularyBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        @Override
        public void onBind(int position) {
            Vocabulary vocabulary = vocabularyList.get(position);
            mBinding.setVocabulary(vocabulary);

            mBinding.edit.setOnClickListener(v -> listener.onEdit(vocabulary));
            mBinding.delete.setOnClickListener(v -> listener.onDelete(vocabulary));

        }

    }

    // NoItem ViewHolder
    public static class EmptyViewHolder extends BaseViewHolder {

        private final TemplateNoItemBinding mBinding;

        EmptyViewHolder(TemplateNoItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            mBinding.setImage(R.drawable.abc);
            mBinding.setTitle("No Vocabularies");
            mBinding.setMessage("You have no vocabularies\nImport an excel sheet with\nvocabularies.");
        }

    }

    public void addVocabulary(List<Vocabulary> farmRecordList) {
        vocabularyList.addAll(farmRecordList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        vocabularyList.clear();
    }

    void updateVocabulary(Vocabulary vocabulary, int position) {
        if (position < vocabularyList.size()) {
            vocabularyList.set(position, vocabulary);
            notifyItemChanged(position);
        }
    }
}

