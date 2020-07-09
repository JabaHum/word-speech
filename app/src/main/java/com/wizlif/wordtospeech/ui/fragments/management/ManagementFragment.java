package com.wizlif.wordtospeech.ui.fragments.management;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.material.snackbar.Snackbar;
import com.wizlif.wordtospeech.R;
import com.wizlif.wordtospeech.ViewModelProviderFactory;
import com.wizlif.wordtospeech.databinding.FragmentManagementBinding;
import com.wizlif.wordtospeech.persistence.Vocabulary;
import com.wizlif.wordtospeech.ui.MainActivity;
import com.wizlif.wordtospeech.ui.VocabularyViewModel;
import com.wizlif.wordtospeech.ui._base.BaseFragment;
import com.wizlif.wordtospeech.utils.Filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class ManagementFragment extends BaseFragment<FragmentManagementBinding, VocabularyViewModel> implements VocabularyItemListener {

    private FragmentManagementBinding binding;
    private VocabularyViewModel viewModel;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_management;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = getViewDataBinding();

        binding.rv.setHasFixedSize(true);
        VocabulariesAdapter adapter = new VocabulariesAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        binding.rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rv.getContext(),
                linearLayoutManager.getOrientation());
        binding.rv.addItemDecoration(dividerItemDecoration);
        binding.rv.setAdapter(adapter);

        subscribeObservers();
    }

    @Override
    public VocabularyViewModel getViewModel() {
        viewModel = new ViewModelProvider(getBaseActivity(), providerFactory).get(VocabularyViewModel.class);
        return viewModel;
    }

    private List<Vocabulary> vocabularies = new ArrayList<>();

    private void subscribeObservers() {

        viewModel.observeVocabulariesData().removeObservers(getViewLifecycleOwner());
        viewModel.observeVocabulariesData().observe(getViewLifecycleOwner(), vocabulariesResource -> {

            switch (vocabulariesResource.status) {

                case SUCCESS:
                    if (vocabulariesResource.data != null && vocabulariesResource.data.size() > 0) {
                        vocabularies = vocabulariesResource.data;
                        Collections.sort(vocabularies, (o1, o2) -> o1.text.compareTo(o2.text));
                        binding.setVocabularies(vocabularies);

                    }
                    break;
                case ERROR:
                    break;
                case LOADING:
                    break;
            }
        });
    }

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onDelete(Vocabulary vocabulary) {

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getBaseActivity())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(String.format(Locale.ENGLISH, "Delete %s ?", vocabulary.text))
                .setMessage(String.format(Locale.ENGLISH, "Do you want to delete %s.", vocabulary.text))
                .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    binding.setLoading(true);
                    binding.setMessage("Deleting " + vocabulary.text);
                    disposable.add(
                            viewModel.getVocabularyDataSource()
                                    .delete(vocabulary.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                                showSnack(String.format("%s Deleted", vocabulary.text), android.R.color.holo_green_dark);
                                                viewModel.deleteVocabulary(vocabulary);
                                                binding.setLoading(false);
                                            },
                                            error -> {
                                                Log.e("TAG", error.toString());
                                                binding.setLoading(false);
                                                showSnack(String.format("Failed to delete %s", vocabulary.text), android.R.color.holo_red_light);
                                            })
                    );
                    dialog.dismiss();
                })
                .addButton("NO", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    @Override
    public void onEdit(Vocabulary vocabulary) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        TextView tv = customLayout.findViewById(R.id.textView);
        tv.setText(String.format("Update %s", vocabulary.text));
        EditText et = customLayout.findViewById(R.id.et);
        et.setText(vocabulary.text);

        Button update = customLayout.findViewById(R.id.bt_update);
        Button cancel = customLayout.findViewById(R.id.bt_cancel);

        update.setOnClickListener(v -> {

            String newText = et.getText().toString().trim();
            if (newText.isEmpty()) {
                showSnack("New Value can not be empty", android.R.color.holo_red_light);
            } else {

                Vocabulary index = Filters.indexOfText(vocabularies, newText.toLowerCase());

                if (index != null) {
                    showSnack("Already Exists", android.R.color.holo_red_light);
                } else {

                    binding.setLoading(true);
                    binding.setMessage("Updating " + vocabulary.text);
                    disposable.add(
                            viewModel.getVocabularyDataSource()
                                    .update(vocabulary.id, newText)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                                showSnack(String.format("%s Updated to %s", vocabulary.text, newText), android.R.color.holo_green_dark);
                                                viewModel.updateVocabulary(vocabulary);
                                                binding.setLoading(false);
                                            },
                                            error -> {
                                                binding.setLoading(false);
                                                showSnack(String.format("Failed to update %s", vocabulary.text), android.R.color.holo_red_light);
                                            })
                    );

                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showSnack(String message, int color) {
        Snackbar snackBar = Snackbar.make(binding.getRoot(),
                message, Snackbar.LENGTH_LONG);

        snackBar.getView().setBackgroundColor(ContextCompat.getColor(requireActivity(), color));

        snackBar.show();
    }
}