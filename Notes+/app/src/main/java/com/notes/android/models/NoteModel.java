package com.notes.android.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Notes")
public class NoteModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "NoteId")
    private int noteId;

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    @ColumnInfo(name = "NoteData")
    private String noteData;

    @ColumnInfo(name = "CreatedAt")
    private long createdAt;

    @ColumnInfo(name = "ImagesList")
    private String imagesList;

    @ColumnInfo(name = "IsLocked")
    private boolean isLocked = false;

    @ColumnInfo(name = "Password")
    private String password;

    @ColumnInfo(name = "Color")
    private String color;

    @ColumnInfo(name ="ReminderDate")
    private long reminderDate;

    @ColumnInfo(name ="LastEditDate")
    private long lastEditDate;

    public long getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(long lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public long getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(long reminderDate) {
        this.reminderDate = reminderDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagesList() {
        return imagesList;
    }

    public void setImagesList(String imagesList) {
        this.imagesList = imagesList;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNoteData() {

        return noteData;
    }

    public void setNoteData(String noteData) {
        this.noteData = noteData;
    }

    @Override
    public String toString() {
        return getNoteId() + " " + noteData + " " + createdAt  + " " + imagesList + " " + isLocked + " " + password;
    }
}
