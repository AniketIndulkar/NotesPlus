package com.notes.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.notes.android.utilities.Utils;

/**
 * Created by aniketindukar on 17/03/18.
 */

public class NotesActivity extends AppCompatActivity {

     public Utils utils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        utils=new Utils();
    }
}

