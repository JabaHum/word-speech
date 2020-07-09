package com.wizlif.wordtospeech.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(indices = {@Index(value = {"vocabulary_text"},
        unique = true)})
public class Vocabulary {
    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "vocabulary_text")
    public String text;

    @Ignore
    public Vocabulary(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
    }

    public Vocabulary(@NonNull String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
