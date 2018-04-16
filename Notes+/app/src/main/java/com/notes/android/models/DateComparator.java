package com.notes.android.models;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by appstartmac1 on 31/12/16.
 */

public class DateComparator implements Comparator<NoteModel> {

    private boolean isCreatedSort;

    public DateComparator(boolean isCreatedSort) {
        this.isCreatedSort = isCreatedSort;
    }

    @Override
    public int compare(NoteModel note1, NoteModel note2) {
        long note1Date, note2Date;

        if (isCreatedSort) {
            note1Date = note1.getCreatedAt();
            note2Date = note2.getCreatedAt();
        } else {
            note1Date = note1.getLastEditDate();
            note2Date = note2.getLastEditDate();
        }


        if (note1Date == note2Date) {
            return 0;
        } else if (note1Date > note2Date) {
            return -1;
        } else {
            return 1;
        }
    }
}
