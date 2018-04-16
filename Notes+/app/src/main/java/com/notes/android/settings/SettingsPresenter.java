package com.notes.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.notes.android.R;
import com.notes.android.utilities.AppConstants;
import com.notes.android.utilities.SharedPrefManager;

public class SettingsPresenter {
    SettingsView settingsView;
    SharedPrefManager sharedPrefManager;

    public SettingsPresenter(SettingsView settingsView) {
        this.settingsView = settingsView;
        sharedPrefManager = new SharedPrefManager(((SettingsActivity) settingsView));
        if (sharedPrefManager.isContains(AppConstants.FONT_SIZE)) {
            String fontSize = sharedPrefManager.getStringFromPref(AppConstants.FONT_SIZE);
            if (!fontSize.equalsIgnoreCase("")) {
                if (Integer.parseInt(fontSize) > 0) {
                    settingsView.setFontSize(Integer.parseInt(fontSize));
                }
            }
        } else {
            sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 16 + "");
            this.settingsView.setFontSize(16);
        }

        if (!sharedPrefManager.isContains(AppConstants.SORT_TYPE)) {
            sharedPrefManager.addStringToPref(AppConstants.SORT_TYPE, AppConstants.SORT_CREATE);
        }

        this.settingsView.setSortType(sharedPrefManager.getStringFromPref(AppConstants.SORT_TYPE));
    }

    public void ShowFontSizeSelectDialog(final Context mContext) {
        final Dialog selectionDialog = new Dialog(mContext);
        selectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        selectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
        View view = LayoutInflater.from(mContext).inflate(R.layout.selection_font_size_dialog, null);
        selectionDialog.setContentView(view);
        if (!selectionDialog.isShowing())
            selectionDialog.show();
        view.findViewById(R.id.txt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(14);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 14 + "");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(16);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 16 + "");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(18);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 18 + "");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(20);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 20 + "");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(22);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 22 + "");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setFontSize(24);
                sharedPrefManager.addStringToPref(AppConstants.FONT_SIZE, 24 + "");
                selectionDialog.dismiss();
            }
        });
    }

    public void showSortingSelectDialog(Context mContext) {
        final Dialog selectionDialog = new Dialog(mContext);
        selectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        selectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
        View view = LayoutInflater.from(mContext).inflate(R.layout.selection_font_size_dialog, null);
        selectionDialog.setContentView(view);

        ((TextView) view.findViewById(R.id.txtHeader)).setText("Select Sorting Type");
        ((TextView) view.findViewById(R.id.txt1)).setText("Date of creation");
        ((TextView) view.findViewById(R.id.txt2)).setText("Date last Edited");
        view.findViewById(R.id.txt3).setVisibility(View.GONE);
        view.findViewById(R.id.txt4).setVisibility(View.GONE);
        view.findViewById(R.id.txt5).setVisibility(View.GONE);
        view.findViewById(R.id.txt6).setVisibility(View.GONE);


        view.findViewById(R.id.txt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setSortType("Date of creation");
                sharedPrefManager.addStringToPref(AppConstants.SORT_TYPE, AppConstants.SORT_CREATE);
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setSortType("Date last Edited");
                sharedPrefManager.addStringToPref(AppConstants.SORT_TYPE, AppConstants.SORT_DATE);
                selectionDialog.dismiss();
            }
        });

        if (!selectionDialog.isShowing())
            selectionDialog.show();
    }


    public void shareNotesPlus(Context mContext) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Find Notes+ app at link below");
        share.putExtra(Intent.EXTRA_TEXT, "Find Notes+ app at link below \n https://play.google.com/store/apps/details?id=com.notes.android");
        mContext.startActivity(Intent.createChooser(share, "Share Notes+"));
    }

    public void rateUs(Context mContext, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }

    public void showOpenSourceLibUsed(final Context mContext) {
        final Dialog selectionDialog = new Dialog(mContext);
        selectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        selectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
        View view = LayoutInflater.from(mContext).inflate(R.layout.selection_font_size_dialog, null);
        selectionDialog.setContentView(view);

        ((TextView) view.findViewById(R.id.txtHeader)).setText("Open Source Libraries");
        ((TextView) view.findViewById(R.id.txt1)).setText("Philliphsu - BottomSheetPickers");
        ((TextView) view.findViewById(R.id.txt2)).setText("iText - itextG");
        ((TextView) view.findViewById(R.id.txt3)).setText("bumptech - glide");
        view.findViewById(R.id.txt4).setVisibility(View.GONE);
        view.findViewById(R.id.txt5).setVisibility(View.GONE);
        view.findViewById(R.id.txt6).setVisibility(View.GONE);


        view.findViewById(R.id.txt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs(mContext,"https://github.com/philliphsu/BottomSheetPickers");
                selectionDialog.dismiss();
            }
        });
        view.findViewById(R.id.txt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs(mContext,"https://developers.itextpdf.com/itextg-android");
                selectionDialog.dismiss();
            }
        });

        view.findViewById(R.id.txt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs(mContext,"https://github.com/bumptech/glide");
                selectionDialog.dismiss();
            }
        });

        if (!selectionDialog.isShowing())
            selectionDialog.show();

    }


    public void showSignInDialog(Context mContext){
        final Dialog signInDialog = new Dialog(mContext);
        signInDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        signInDialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
        View view = LayoutInflater.from(mContext).inflate(R.layout.sign_in_layout, null);
        signInDialog.setContentView(view);

        if (!signInDialog.isShowing())
            signInDialog.show();
    }
}
