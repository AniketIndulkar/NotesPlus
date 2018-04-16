package com.notes.android.note;

import android.graphics.Bitmap;
import android.net.Uri;

import com.notes.android.models.ToDoModel;

import java.io.File;
import java.util.List;

/**
 * Created by Aniket on 23-03-2018.
 */

public interface NoteView {


    void addToDo(ToDoModel todo);

    void setDate(String dateSring);

    void exitActivity();

    void undoText();

    void redoText();

    void openCamera();

    void openGallery();

    void showBottomSheet();

    boolean bottomSheetStatus();

    void loadImageRv(List<Uri> bitmapList);

    void setPassword(String password);

    void showReminderIcon();

    void hideReminderIcon();

    void showLockIcon();

    void hideLockIcon();

    void pdfGenerated(File pdfFile);

    void startHomeActivity();

}
