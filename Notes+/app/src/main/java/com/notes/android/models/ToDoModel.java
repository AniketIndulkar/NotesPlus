package com.notes.android.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aniketindukar on 01/04/18.
 */

@Entity(tableName = "ToDo")
public class ToDoModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ToDoId")
    private int todoId;

    @ColumnInfo(name = "ToDoGoal")
    private String todoGoal;

    @ColumnInfo(name = "isDone")
    private boolean isDone;

    @ColumnInfo(name = "NoteId")
    private int noteId;

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getTodoGoal() {
        return todoGoal;
    }

    public void setTodoGoal(String todoGoal) {
        this.todoGoal = todoGoal;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }


    @Override
    public String toString() {
        return todoGoal + " " + isDone + " " + noteId;
    }
}


