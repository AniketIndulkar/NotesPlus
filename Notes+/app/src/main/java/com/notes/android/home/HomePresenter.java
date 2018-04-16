package com.notes.android.home;

import android.content.Context;

import com.notes.android.database.AppDb;
import com.notes.android.models.DateComparator;
import com.notes.android.models.NoteModel;
import com.notes.android.utilities.AppConstants;
import com.notes.android.utilities.SharedPrefManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by aniketindukar on 17/03/18.
 */

public class HomePresenter {
    HomeView homeView;

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
    }

    void showProgressCircle() {
        homeView.showCircularProgressView();
    }

    void getNotesFromDb(Context mContext) {
        List<NoteModel> noteDataList;
        noteDataList = AppDb.getAppDatabase(mContext).notesDao().getAll();
        homeView.hideCircularProgress();
        if (noteDataList != null && noteDataList.size() > 0) {
            homeView.hideNoData();
            SharedPrefManager manager = new SharedPrefManager(mContext);
            if (manager.isContains(AppConstants.SORT_TYPE)) {
                if (manager.getStringFromPref(AppConstants.SORT_TYPE).equalsIgnoreCase(AppConstants.SORT_CREATE)) {
                    Collections.sort(noteDataList, new DateComparator(true));
                } else {
                    Collections.sort(noteDataList, new DateComparator(false));
                }
            } else {
                Collections.sort(noteDataList, new DateComparator(true));
            }


            homeView.populateNotesRV(noteDataList);
        } else {
            homeView.showNoDataView();
        }
    }
}
