package com.wizlif.wordtospeech.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.wizlif.wordtospeech.R;
import com.wizlif.wordtospeech.ViewModelProviderFactory;
import com.wizlif.wordtospeech.databinding.FragmentMainBinding;
import com.wizlif.wordtospeech.persistence.SharedPreferences;
import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.ui.MainActivity;
import com.wizlif.wordtospeech.ui.VocabularyViewModel;
import com.wizlif.wordtospeech.ui._base.BaseFragment;
import com.wizlif.wordtospeech.utils.Filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends BaseFragment<FragmentMainBinding,VocabularyViewModel> {

    @Inject
    ViewModelProviderFactory providerFactory;

    @Inject
    SharedPreferences sp;


    private FragmentMainBinding binding;

    private TextToSpeech textToSpeech;

    int current_index = 0;

    final String[] words = {"smiling", "soft", "someone", "something", "speak", "spread", "spring", "stairs", "stopped", "straight", "street", "stretch", "string", "strong", "suit", "summer", "sunday", "tenth", "that's", "thick", "threw", "throw", "thursday", "tiny", "today", "together", "tooth", "touch", "town", "tries", "trouble", "true", "tuesday", "turn", "until", "used", "voice", "walk", "warm", "we'll", "wednesday", "whole", "window", "without", "won't", "wore", "wrong", "wrote", "young", "you're"};

    private List<Vocabulary> vocabularies = new ArrayList<>();

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }


    @Override
    public VocabularyViewModel getViewModel() {
        viewModel = new ViewModelProvider(getBaseActivity(),providerFactory).get(VocabularyViewModel.class);
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        init();
        subscribeObservers();
    }

    private CompositeDisposable disposable = new CompositeDisposable();
    private VocabularyViewModel viewModel;


    private boolean isDataLoading = true;
    private boolean isT2SLoading = true;
    private void init() {
        binding.setLoading(true);

        if (sp.getIsFirstRun()) {
            initializeDatabase(sp);
        }

        textToSpeech = new TextToSpeech(requireActivity(), status -> {
            if (status != -1) {
                isT2SLoading = false;
                if(!isDataLoading)binding.setLoading(false);
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak("Welcome to me, the best word to speech application, select a word with the blue navigation buttons and click say for me to pronounce the word for you", 0, null);
            }
        });

        binding.prev.setOnClickListener(v -> {
            current_index--;
            if (current_index < 0){
                current_index = 0;
                showSnack("First word!", android.R.color.holo_green_dark);
            }
            Vocabulary voc = vocabularies.get(current_index);
            binding.word.setText(voc.text);

        });

        binding.next.setOnClickListener(v -> {
            current_index++;
            if (current_index >= vocabularies.size()) {
                current_index = vocabularies.size() -1;
                showSnack("Last word!", android.R.color.holo_green_dark);
            }

            Vocabulary voc = vocabularies.get(current_index);
            binding.word.setText(voc.text);

        });

        binding.say.setOnClickListener(v -> {
            String toSpeak = binding.word.getText().toString();
            textToSpeech.speak(toSpeak, 0, null);
        });
    }

    private void initializeDatabase(SharedPreferences sp) {
        // Initialize Database
        ProgressDialog progressBar = new ProgressDialog(requireContext());
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setMessage("Initializing Vocabulary");
        List<Vocabulary> totalWords = new ArrayList<>();
        final int[] completed = {0};

        progressBar.show();
        for (String s : words) {
            Vocabulary word = new Vocabulary(s.toLowerCase());
            totalWords.add(word);
            disposable.add(
                    viewModel.getVocabularyDataSource()
                            .insertVocabulary(word)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(r -> {
                                        completed[0]++;
                                        progressBar.setProgress((completed[0] / 50) * 100);
                                        if (completed[0] == 50) {
                                            progressBar.dismiss();
                                            showSnack("Vocabulary Initialized", android.R.color.holo_green_dark);
                                            sp.setIsFirstRun(false);
                                            viewModel.setVocabularyData(totalWords);
                                        }
                                    },
                                    e -> {
                                        completed[0]++;
                                        showSnack("Failed to import "+s,android.R.color.holo_red_dark);
                                    }));
        }
    }

    private void subscribeObservers(){
        viewModel.observeVocabulariesData().removeObservers(getViewLifecycleOwner());
        viewModel.observeVocabulariesData().observe(getViewLifecycleOwner(), vocabulariesResource -> {
            switch (vocabulariesResource.status){

                case SUCCESS:
                    if(vocabulariesResource.data != null && vocabulariesResource.data.size() > 0){
                        vocabularies = vocabulariesResource.data;
                        isDataLoading = false;
                        Collections.sort(vocabularies, (o1, o2) -> o1.text.compareTo(o2.text));
                        binding.word.setText(vocabularies.get(current_index).text);
                        if(!isT2SLoading)binding.setLoading(false);

                    }
                    break;
                case ERROR:
                    binding.setLoading(false);
                    showSnack(vocabulariesResource.message,android.R.color.holo_red_dark);
                    break;
                case LOADING:
                    binding.setLoading(true);
                    binding.setMessage("LOADING VOCABULARIES ...");
                    break;
            }
        });
    }

    private void showSnack(String message, int color) {
        Snackbar snackBar = Snackbar.make(binding.getRoot(),
                message, Snackbar.LENGTH_LONG);

        snackBar.getView().setBackgroundColor(ContextCompat.getColor(requireActivity(), color));

        snackBar.show();
    }
}