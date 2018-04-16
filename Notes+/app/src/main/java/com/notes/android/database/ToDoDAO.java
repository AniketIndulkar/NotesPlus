package com.notes.android.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.notes.android.models.NoteModel;
import com.notes.android.models.ToDoModel;

import java.util.List;

/**
 * Created by aniketindukar on 01/04/18.
 */

@Dao
public interface ToDoDAO {

    @Query("SELECT * FROM ToDo")
    List<ToDoModel> getAll();

    @Query("SELECT * FROM ToDo WHERE NoteId =:noteId")
    List<ToDoModel> getToDoById(int noteId);

    @Insert
    void insertData(ToDoModel noteData);


    @Query("SELECT * FROM ToDo where NoteId = :note_id")
    List<ToDoModel> findForNote(int note_id);

    @Query("UPDATE ToDo SET ToDoGoal =:todoGoal ,isDone = :isDone WHERE NoteId = :noteId AND ToDoId =:todoID")
    void updateToDo(String todoGoal, boolean isDone, int noteId ,int todoID);

    @Query("DELETE FROM ToDo WHERE NoteId = :noteId")
    void deleteAll(int noteId);
}
