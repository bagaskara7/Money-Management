package com.andronomy.moneymanagement.Activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andronomy.moneymanagement.Adapters.DatabaseAdapter;
import com.andronomy.moneymanagement.Adapters.HistoryAdapter;
import com.andronomy.moneymanagement.R;
import com.andronomy.moneymanagement.SlidingTabLayout;
import com.rey.material.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends ActionBarActivity {
    private static DatabaseAdapter dbAdapter;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DatabaseAdapter(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorPrimary);
            }
        });
        mTabs.setSelectedIndicatorColors(R.color.colorPrimary);
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_outcome, menu);
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
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;
        int icons[] = {R.drawable.ic_action_import, R.drawable.ic_action_export};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment = new MyFragment();
            myFragment.getInstance(position);
            return myFragment;
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }

    public static class MyFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private HistoryAdapter mAdapter;
        private int pos = 0;

        public MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            this.pos = position;
            return myFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.history_content_layout, container, false);
            mRecyclerView = (RecyclerView) layout.findViewById(R.id.content);

            if (this.pos == 0) {
                mAdapter = new HistoryAdapter(getActivity(), dbAdapter.getTransData("i"));
            } else {
                mAdapter = new HistoryAdapter(getActivity(), dbAdapter.getTransData("o"));
            }

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (mAdapter.getItemCount() == 0) {
                TextView tv = new TextView(getActivity());
                tv.applyStyle(R.style.TextAppearance_AppCompat_Medium);
                tv.setText(getString(R.string.no_data_recycler));
                tv.setTextColor(getResources().getColor(R.color.colorTextSecondary));
                tv.setGravity(Gravity.CENTER);
                mRecyclerView.addView(tv);
            }
            return layout;
        }
    }
}
