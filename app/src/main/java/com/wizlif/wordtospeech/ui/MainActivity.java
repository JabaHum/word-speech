package com.wizlif.wordtospeech.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.wizlif.wordtospeech.R;
import com.wizlif.wordtospeech.ViewModelProviderFactory;
import com.wizlif.wordtospeech.databinding.ActivityWordtospeechBinding;
import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.ui._base.BaseActivity;
import com.wizlif.wordtospeech.utils.ExcelManager;
import com.wizlif.wordtospeech.utils.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity<ActivityWordtospeechBinding> {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    CompositeDisposable disposable = new CompositeDisposable();
    private ActivityWordtospeechBinding binding;

    @Inject
    ViewModelProviderFactory providerFactory;

    private VocabularyViewModel viewModel;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wordtospeech;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();



        viewModel = new ViewModelProvider(this, providerFactory).get(VocabularyViewModel.class);
        progressBar = new ProgressDialog(this);

        subscribeObservers();
    }

    private List<Vocabulary> vocabularies = new ArrayList<>();
    private void subscribeObservers(){
        viewModel.observeVocabulariesData().removeObservers(this);
        viewModel.observeVocabulariesData().observe(this, vocabulariesResource -> {
            switch (vocabulariesResource.status){

                case SUCCESS:
                    if(vocabulariesResource.data != null && vocabulariesResource.data.size() > 0){
                        vocabularies = vocabulariesResource.data;

                    }
                    break;
                case ERROR:
                case LOADING:
                    break;
            }
        });
    }


    /**
     * This method creates menu item
     *
     * @param menu holds Menu
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This methods handles menu items clicks
     *
     * @param item holds MenuItem
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.manage_words:
                Navigation.findNavController(this,R.id.fragment).navigate(R.id.managementFragment);
                return true;
            case R.id.add_single_word:
                addWord();
                return true;
            case R.id.import_excel:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                // The result of the SAF Intent is handled in onActivityResult.
                startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                Uri uri = resultData.getData();
                if (uri != null) {
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setMessage("Importing Excel Sheet");
                    progressBar.show();
                    disposable.add(
                            ExcelManager.openExcelSheet(getContentResolver(), uri)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(texts -> {
                                                if(texts.size() == 0){
                                                    progressBar.dismiss();
                                                    showSnack("No text to import",android.R.color.holo_red_light);
                                                }else{
                                                    importToDatabase(texts);
                                                }
                                            },
                                            error -> {
                                                showSnack(error.getMessage(),android.R.color.holo_red_light);
                                                progressBar.dismiss();
                                            })
                    );
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void showSnack(String message, int color) {
        Snackbar snackBar = Snackbar.make(binding.getRoot(),
                message, Snackbar.LENGTH_LONG);

        snackBar.getView().setBackgroundColor(ContextCompat.getColor(this, color));

        snackBar.show();
    }

    private ProgressDialog progressBar;
    private void importToDatabase(List<String> words) {
        // Initialize Database

        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setMessage("Importing Vocabulary");
        List<Vocabulary> totalWords = new ArrayList<>();
        final int[] completed = {0};

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
                                        progressBar.setProgress((completed[0] / words.size()) * 100);
                                        if (completed[0] == words.size()) {
                                            progressBar.dismiss();
                                            showSnack(String.format(Locale.UK,"%d Words Imported",words.size()), android.R.color.holo_green_dark);
                                            viewModel.reloadVocabularyData();
                                        }
                                    },
                                    e -> {
                                        completed[0]++;
                                        if (completed[0] == words.size()) {progressBar.dismiss();}
                                        showSnack("Failed to import "+s,android.R.color.holo_red_dark);
                                    }));
        }
    }

    public void addWord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        TextView tv = customLayout.findViewById(R.id.textView);
        tv.setText(R.string.add_new_word);
        EditText et = customLayout.findViewById(R.id.et);

        Button update = customLayout.findViewById(R.id.bt_update);
        Button cancel = customLayout.findViewById(R.id.bt_cancel);
        update.setText(R.string.save);
        update.setOnClickListener(v -> {

            String newText = et.getText().toString().trim();
            if (newText.isEmpty()) {
                showSnack("Text can not be empty", android.R.color.holo_red_light);
            } else {

                Vocabulary index = Filters.indexOfText(vocabularies, newText.toLowerCase());

                if (index != null) {
                    showSnack("Already Exists", android.R.color.holo_red_light);
                } else {
                    Vocabulary newVb = new Vocabulary(newText);
                    disposable.add(
                            viewModel.getVocabularyDataSource()
                                    .insertVocabulary(newVb)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(id -> {
                                                showSnack(String.format("Added %s", newVb.text), android.R.color.holo_green_dark);
                                                viewModel.addVocabulary(newVb);
                                            },
                                            error -> showSnack(String.format("Failed to add %s", newVb.text), android.R.color.holo_red_light))
                    );

                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}