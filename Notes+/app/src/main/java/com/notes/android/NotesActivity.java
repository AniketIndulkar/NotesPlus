package com.notes.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

