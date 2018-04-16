package com.notes.android.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.notes.android.NotesActivity;
import com.notes.android.R;
import com.notes.android.adapters.NoteRVAdapter;
import com.notes.android.database.AppDb;
import com.notes.android.models.NoteModel;
import com.notes.android.note.Note;
import com.notes.android.settings.SettingsActivity;
import com.notes.android.utilities.AppConstants;
import com.notes.android.utilities.SharedPrefManager;
import com.notes.android.utilities.Utils;

import java.util.List;

public class HomeActivity extends NotesActivity implements HomeView, NoteRVAdapter.RVClickevents, View.OnClickListener {

    private HomePresenter homePresenter;
    private RelativeLayout relativeProgress, relativeNoData;
    private RecyclerView rvNotes;
    private ImageView ivViewType, ivSettings;
    private TextView txtAppName;
    private boolean isGrid;
    private SharedPrefManager sharedPrefManager;
    private NoteRVAdapter notesAdapter;
    private AdView mAdView;
    private boolean didAdLoad = false, didInterstitialAdLoad = false;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);


        homePresenter = new HomePresenter(this);

        initializeView();
        getValuesFromPref();

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadAds();
            }
        }).start();

        homePresenter.showProgressCircle();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notesActivity = new Intent(HomeActivity.this, Note.class);
                startActivity(notesActivity);
            }
        });
    }

    private void loadAds() {
        //To load Ads
        if (Utils.isNetworkConnected(HomeActivity.this) && Utils.isConnected()) {//to check internet connectivity
            didAdLoad = true;//to indicate ad loaded

            //Initialize Admob sdk
            MobileAds.initialize(this, "ca-app-pub-4397804709549987~1961712358");

            //To load banner ads
            mAdView = (AdView) findViewById(R.id.adView);
            final AdRequest adRequest = new AdRequest.Builder()
                    .build();

//            To load interstitial ads
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-4397804709549987/4217174754");
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    didInterstitialAdLoad = true;
                    onBackPressed();
                }

            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdView.loadAd(adRequest);
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        homePresenter.getNotesFromDb(HomeActivity.this);
    }

    private void getValuesFromPref() {
        sharedPrefManager = new SharedPrefManager(HomeActivity.this);
        if (sharedPrefManager.getStringFromPref(AppConstants.VIEW_TYPE).equalsIgnoreCase(AppConstants.STRING_DEFAULT)) {
            sharedPrefManager.addStringToPref(AppConstants.VIEW_TYPE, AppConstants.STRING_GRID);
            isGrid = true;
        } else {
            if (sharedPrefManager.getStringFromPref(AppConstants.VIEW_TYPE).equalsIgnoreCase(AppConstants.STRING_GRID)) {
                isGrid = true;
            } else {
                isGrid = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDb.destroyInstance();
    }

    private void initializeView() {
        relativeProgress = (RelativeLayout) findViewById(R.id.relativeProgress);
        relativeNoData = (RelativeLayout) findViewById(R.id.relativeNoData);
        rvNotes = (RecyclerView) findViewById(R.id.rvNotes);

        //Images
        ivViewType = (ImageView) findViewById(R.id.ivViewType);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);

        //TextView
        txtAppName = (TextView) findViewById(R.id.txtAppName);

        ivViewType.setOnClickListener(this);
        ivSettings.setOnClickListener(this);
    }

    @Override
    public void showCircularProgressView() {
        relativeProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCircularProgress() {
        relativeProgress.setVisibility(View.GONE);
    }

    @Override
    public void populateNotesRV(List<NoteModel> notes) {
        if (notes != null) {
            notesAdapter = new NoteRVAdapter(HomeActivity.this);
            notesAdapter.setNoteDataList(notes);
            notesAdapter.setRvClickevents(this);
            rvNotes.setAdapter(notesAdapter);
            setRVViewType(isGrid);
            if (didAdLoad) {
                ViewGroup.MarginLayoutParams marginLayoutParams =
                        (ViewGroup.MarginLayoutParams) rvNotes.getLayoutParams();
                marginLayoutParams.setMargins(0, 0, 0, 130);
                rvNotes.setLayoutParams(marginLayoutParams);
            }
        }
    }

    @Override
    public void showNoDataView() {
        rvNotes.setVisibility(View.GONE);
        relativeNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoData() {
        rvNotes.setVisibility(View.VISIBLE);
        relativeNoData.setVisibility(View.GONE);
    }

    @Override
    public void onClick(NoteModel clickedData) {
        if (clickedData.isLocked()) {
            askForPassword(clickedData);
        } else {
            navigateToNotes(clickedData);
        }

    }

    private void navigateToNotes(NoteModel clickedData) {
        Intent noteIntent = new Intent(HomeActivity.this, Note.class);
        noteIntent.putExtra(AppConstants.NOTE_ID, clickedData.getNoteId());
        startActivity(noteIntent);
    }


    private void askForPassword(final NoteModel clickedData) {
        final Dialog passworDialog = new Dialog(HomeActivity.this);
        passworDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passworDialog.setContentView(R.layout.ask_password);
        passworDialog.getWindow().setBackgroundDrawable(null);
        passworDialog.setCancelable(true);
        passworDialog.show();

        final EditText etPassword = (EditText) passworDialog.findViewById(R.id.et_ask_password);
        Button ok = (Button) passworDialog.findViewById(R.id.btn_ask_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passworDialog.dismiss();
                if (etPassword.getText().toString().equals(clickedData.getPassword())) {
                    navigateToNotes(clickedData);
                } else {
                    Toast.makeText(HomeActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivViewType:
                isGrid = isGrid ? false : true;
                if (isGrid) {
                    ivViewType.setImageResource(R.drawable.ic_list_view);
                    sharedPrefManager.addStringToPref(AppConstants.VIEW_TYPE, AppConstants.STRING_GRID);
                } else {
                    ivViewType.setImageResource(R.drawable.ic_grid);
                    sharedPrefManager.addStringToPref(AppConstants.VIEW_TYPE, AppConstants.STRING_LIST);
                }
                setRVViewType(isGrid);
                break;
            case R.id.ivSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    public void setRVViewType(boolean isGrid) {
        if (isGrid) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            rvNotes.setLayoutManager(staggeredGridLayoutManager);
        } else {
            rvNotes.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        }

    }

    @Override
    public void onBackPressed() {
        if (Utils.isNetworkConnected(HomeActivity.this) && Utils.isConnected()) {//to check internet connectivity
            if (didInterstitialAdLoad) {//to check add shown before
                finish();
            } else {
                if (mInterstitialAd != null) {
                    if (mInterstitialAd.isLoaded()) {// to check ad loaded
                        mInterstitialAd.show();// to show ads
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        } else {
            finish();
        }

    }
}
