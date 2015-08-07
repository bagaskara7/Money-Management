package com.andronomy.moneymanagement.Activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andronomy.moneymanagement.Adapters.DatabaseAdapter;
import com.andronomy.moneymanagement.Adapters.HistoryTransferAdapter;
import com.andronomy.moneymanagement.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.rey.material.widget.TextView;

import java.util.Locale;


public class HistoryTransfer extends ActionBarActivity {
    InterstitialAd mInterstitialAd;
    private static DatabaseAdapter dbAdapter;
    private RecyclerView mRecyclerView;
    private HistoryTransferAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transfer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DatabaseAdapter(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                finish();
            }
        });

        Locale.setDefault(new Locale(dbAdapter.getActiveLang()));

        mRecyclerView = (RecyclerView) findViewById(R.id.content);
        mAdapter = new HistoryTransferAdapter(this, dbAdapter.getTransferData());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (mAdapter.getItemCount() == 0) {
            RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.container);
            TextView tv = new TextView(this);
            tv.applyStyle(R.style.TextAppearance_AppCompat_Medium);
            tv.setText(getString(R.string.no_data_recycler));
            tv.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            tv.setGravity(Gravity.CENTER);
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.app_bar);
            tv.setLayoutParams(params);
            layout1.addView(tv);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_history_transfer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                requestNewInterstitial();
                NavUtils.navigateUpFromSameTask(this);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            super.onBackPressed();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}
