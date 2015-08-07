package com.andronomy.moneymanagement.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.andronomy.moneymanagement.Adapters.DatabaseAdapter;
import com.andronomy.moneymanagement.CustomMarkerView;
import com.andronomy.moneymanagement.Lists.Devices;
import com.andronomy.moneymanagement.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.rey.material.app.Dialog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnChartValueSelectedListener {
    private float balance;
    private MainActivity context = this;
    private InterstitialAd mInterstitialAd;
    private DatabaseAdapter mDbAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserSawDrawer = false;
    private static final String FIRST_TIME = "first_time";
    private String versionName = "";
    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbAdapter = new DatabaseAdapter(this);
        setLanguage();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDrawer();
        initializeChart();
        initializePieChart();

        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tv = (TextView) findViewById(R.id.drawer_headline);
        tv.setText(getResources().getString(R.string.version) + " " + versionName);

        float incomes = mDbAdapter.getTransaction("i");
        float expenses = mDbAdapter.getTransaction("o");
        balance = incomes - expenses;
        NumberFormat numFormat = NumberFormat.getCurrencyInstance();
        ((DecimalFormat) numFormat).setDecimalFormatSymbols(setCurrency());

        TextView itv = (TextView) findViewById(R.id.income_number_text);
        itv.setText(numFormat.format(incomes));

        TextView etv = (TextView) findViewById(R.id.outcome_number_text);
        etv.setText(numFormat.format(expenses));

        TextView tvt = (TextView) findViewById(R.id.drawer_headline_top);
        tvt.setText(getString(R.string.headline_hello_str) + " " + Devices.getDeviceName() + " " + getString(R.string.headline_user_str));

        TextView tvm = (TextView) findViewById(R.id.drawer_headline_middle);
        tvm.setText(getResources().getString(R.string.cur_balance) + " " + numFormat.format(balance));
        if (balance < 0) {
            tvm.setTextColor(getResources().getColor(R.color.error_color));
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 300);
            }
        });

        findViewById(R.id.income_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddIncomeActivity.class));
            }
        });

        findViewById(R.id.outcome_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddOutcomeActivity.class));
            }
        });
    }

    private void initializePieChart() {
        mChart = (PieChart) findViewById(R.id.piechart);
        mChart.setUsePercentValues(true);
        mChart.setDescription(getString(R.string.month_statistics_chart));

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

//        mChart.setUnit(" €");
//        mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        NumberFormat numFormat = NumberFormat.getCurrencyInstance();
        float incomes = mDbAdapter.getTransaction("i");
        float expenses = mDbAdapter.getTransaction("o");
        balance = incomes - expenses;
        mChart.setCenterText(getString(R.string.my_balance) + ":\n" + numFormat.format(balance));

        setData();

        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setEnabled(false);

        mChart.setNoDataText("");
    }

    private void setData() {
        ArrayList<Entry> yVals1 = mDbAdapter.getPieChart();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other

        ArrayList<String> xVals = mDbAdapter.getPieChartTitle(this);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void initializeDrawer() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        NavigationView mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
    }

    private void initializeChart() {
        LineChart mChart = (LineChart) findViewById(R.id.chart);

        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        mChart.setMarkerView(mv);
        mChart.setDescriptionColor(getResources().getColor(R.color.colorTextSecondary));
        mChart.setDescription(getString(R.string.month_statistics_chart));
        mChart.getAxisRight().setEnabled(false);
        mChart.setDrawGridBackground(true);
        mChart.setHighlightEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setPinchZoom(false);
        mChart.setPinchZoom(true);

        YAxis leftAxis = mChart.getAxisLeft();
        XAxis bottomAxis = mChart.getXAxis();

        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawAxisLine(true);
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setAvoidFirstLastClipping(true);

        Legend l = mChart.getLegend();
        int dayOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 1; i <= dayOfMonth; i++) {
            xVals.add(i + "");
        }

        ArrayList<Entry> yValues = mDbAdapter.getChart("i");
        ArrayList<Entry> yValues2 = mDbAdapter.getChart("o");

        LineDataSet set1 = new LineDataSet(yValues, getResources().getStringArray(R.array.tabs)[0]);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setLineWidth(2.5f);
        set1.setCircleSize(4f);
        LineDataSet set2 = new LineDataSet(yValues2, getResources().getStringArray(R.array.tabs)[1]);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setLineWidth(2.5f);
        set2.setCircleSize(4f);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        dataSets.get(0).setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        dataSets.get(0).setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        dataSets.get(1).enableDashedLine(10, 10, 0);
        dataSets.get(1).setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        dataSets.get(1).setCircleColor(ColorTemplate.VORDIPLOM_COLORS[4]);

        LineData data = new LineData(xVals, dataSets);
        mChart.setData(data);
        mChart.animateXY(1500, 1500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent;
        int id = menuItem.getItemId();

        if (id == R.id.home_menu) {
            hideDrawer();
        }

        if (id == R.id.accounts_menu) {
            intent = new Intent(this, AccountsActivity.class);
            overridePendingTransition(R.animator.back_anim_in, R.animator.back_anim);
            startActivity(intent);
        }

        if (id == R.id.history_menu) {
            intent = new Intent(this, HistoryActivity.class);
            overridePendingTransition(R.animator.back_anim_in, R.animator.back_anim);
            startActivity(intent);
        }

        if (id == R.id.settings_menu) {
            intent = new Intent(this, SettingsActivity.class);
            overridePendingTransition(R.animator.back_anim_in, R.animator.back_anim);
            startActivity(intent);
        }

        if (id == R.id.about_menu) {
            hideDrawer();

            final Dialog mDialog = new Dialog(this);
            mDialog.setContentView(R.layout.about_layout);
            mDialog.title(getString(R.string.about_title))
                    .positiveAction("Ok")
                    .positiveActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    })
                    .neutralAction(getString(R.string.visit_button))
                    .neutralActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("http://www.andronomy.com");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            mDialog.dismiss();
                        }
                    })
                    .show();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();
        else{
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void setLanguage() {
        String lang = (mDbAdapter.getActiveLang().isEmpty() || mDbAdapter.getActiveLang().equals("_")) ? "en_US" : mDbAdapter.getActiveLang();
        Locale locale = new Locale(lang);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
    }

    public DecimalFormatSymbols setCurrency() {
        Currency currency = Currency.getInstance(mDbAdapter.getActiveCurr());
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setCurrencySymbol(currency.getSymbol());
        dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');

        return dfs;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;

        Intent intent = new Intent(this, AddOutcomeActivity.class);
        intent.putExtra("exp_id", e.getXIndex());
        startActivity(intent);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}