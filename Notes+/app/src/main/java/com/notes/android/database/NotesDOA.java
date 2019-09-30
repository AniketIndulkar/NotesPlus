package com.notes.android.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.notes.android.models.NoteModel;

import java.util.List;

@Dao
public interface NotesDOA {

    @Query("SELECT * FROM Notes")
    List<NoteModel> getAll();

    //
    @Query("SELECT * FROM Notes where NoteId = :NoteId")
    NoteModel findById(int NoteId);


    @Query("SELECT * FROM Notes ORDER BY NoteId DESC")
    List<NoteModel> getLastEntry();

    @Query("SELECT COUNT(*) from Notes")
    int countUsers();

    @Insert
    void insertAll(NoteModel... notes);

    @Insert
    void insertData(NoteModel noteData);

    @Delete()
    void delete(NoteModel noteModel);

    @Query("UPDATE Notes SET NoteData = :noteData,ImagesList =:imageList , Password =:password,IsLocked =:isLocked ,ReminderDate =:reminderDate,LastEditDate =:lastEditDate WHERE NoteId =:NoteId")
    void updateNoteData(String noteData, String imageList, String password, boolean isLocked, int NoteId,long reminderDate ,long lastEditDate);
}
