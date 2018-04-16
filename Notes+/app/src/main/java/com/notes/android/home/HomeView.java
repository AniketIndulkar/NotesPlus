package com.notes.android.home;

import com.notes.android.models.NoteModel;

import java.util.List;

/**
 * Created by aniketindukar on 17/03/18.
 */

public interface HomeView {

    void showCircularProgressView();

    void hideCircularProgress();

    void populateNotesRV(List<NoteModel> notes);

    void showNoDataView();

    void hideNoData();

}
