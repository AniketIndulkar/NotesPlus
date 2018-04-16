package com.notes.android;

import android.app.Application;
import android.os.StrictMode;



/**
 * Created by Akshay on 6/24/2017.
 */

public class NotesApplication extends Application {

    private String TAG="NotesApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
