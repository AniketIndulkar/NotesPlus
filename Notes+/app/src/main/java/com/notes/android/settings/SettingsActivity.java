package com.notes.android.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.notes.android.R;
import com.notes.android.utilities.Utils;

public class SettingsActivity extends AppCompatActivity implements SettingsView, View.OnClickListener {
    private TextView tvFontSizeHeder, tvFontSize, tvFontSizeSubHeading, tvSortingHeader, tvSortingSubHeader,
            tvSortingType, tvShare, tvAboutUs, tvRateUs, tvOpenSourceLib, tvBackup, tvRestore;
    private ImageView ivBack;
    private SettingsPresenter settingsPresenter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        intitializeView();
        settingsPresenter = new SettingsPresenter(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadAds();
            }
        }).start();

    }

    private void intitializeView() {
        tvFontSizeHeder = (TextView) findViewById(R.id.tvFontSizeHeder);
        tvFontSize = (TextView) findViewById(R.id.tvFontSize);
        tvFontSizeSubHeading = (TextView) findViewById(R.id.tvFontSizeSubHeading);
        tvSortingHeader = (TextView) findViewById(R.id.tvSortingHeader);
        tvSortingSubHeader = (TextView) findViewById(R.id.tvSortingSubHeader);
        tvSortingType = (TextView) findViewById(R.id.tvSortingType);
        tvShare = (TextView) findViewById(R.id.tvShare);
        tvAboutUs = (TextView) findViewById(R.id.tvAboutUs);
        tvRateUs = (TextView) findViewById(R.id.tvRateUs);
        tvOpenSourceLib = (TextView) findViewById(R.id.tvOpenSourceLib);
        tvRestore = (TextView) findViewById(R.id.tvRestore);
        tvBackup = (TextView) findViewById(R.id.tvBackup);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        tvFontSizeHeder.setOnClickListener(this);
        tvFontSizeSubHeading.setOnClickListener(this);
        tvSortingHeader.setOnClickListener(this);
        tvSortingSubHeader.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvAboutUs.setOnClickListener(this);
        tvRateUs.setOnClickListener(this);
        tvOpenSourceLib.setOnClickListener(this);
        tvBackup.setOnClickListener(this);
        tvRestore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvFontSizeHeder:
                settingsPresenter.ShowFontSizeSelectDialog(SettingsActivity.this);
                break;
            case R.id.tvFontSizeSubHeading:
                settingsPresenter.ShowFontSizeSelectDialog(SettingsActivity.this);
                break;
            case R.id.tvSortingHeader:
                settingsPresenter.showSortingSelectDialog(SettingsActivity.this);
                break;
            case R.id.tvSortingSubHeader:
                settingsPresenter.showSortingSelectDialog(SettingsActivity.this);
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvAboutUs:
                settingsPresenter.rateUs(SettingsActivity.this, "https://play.google.com/store/apps/developer?id=AndroidVoyage");
                break;
            case R.id.tvShare:
                settingsPresenter.shareNotesPlus(SettingsActivity.this);
                break;
            case R.id.tvRateUs:
                settingsPresenter.rateUs(SettingsActivity.this, "https://play.google.com/store/apps/details?id=com.notes.android");
                break;
            case R.id.tvOpenSourceLib:
                settingsPresenter.showOpenSourceLibUsed(SettingsActivity.this);
                break;
            case R.id.tvRestore:
                Toast.makeText(this, "BackEnd is under improvement  \n we apologize for inconvenience", Toast.LENGTH_LONG).show();
                break;
            case R.id.tvBackup:
                Toast.makeText(this, "BackEnd is under improvement  \n we apologize for inconvenience", Toast.LENGTH_LONG).show();
                break;
        }

    }


    @Override
    public void setFontSize(int fontSize) {
        tvFontSize.setText(fontSize + "");
    }

    @Override
    public void setSortType(String sortType) {
        tvSortingType.setText(sortType);
    }


    private void loadAds() {
        //To load Ads
        if (Utils.isNetworkConnected(SettingsActivity.this) && Utils.isConnected()) {//to check internet connectivity
            //Initialize Admob sdk
            MobileAds.initialize(this, "ca-app-pub-4397804709549987~1961712358");

            //To load banner ads
            mAdView = (AdView) findViewById(R.id.adView);
            final AdRequest adRequest = new AdRequest.Builder()
                    .build();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdView.loadAd(adRequest);
                }
            });
        }
    }
}
