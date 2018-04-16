package com.notes.android.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.notes.android.models.NoteModel;
import com.notes.android.models.ToDoModel;

@Database(entities = {NoteModel.class, ToDoModel.class}, version = 1 ,exportSchema = false)
public abstract class AppDb extends RoomDatabase {

    private static AppDb INSTANCE;

    public abstract NotesDOA notesDao();

    public abstract ToDoDAO todoDao();

    public static AppDb getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDb.class, "NoteDB")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
