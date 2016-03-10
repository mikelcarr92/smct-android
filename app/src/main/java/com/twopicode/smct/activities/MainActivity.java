package com.twopicode.smct.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.twopicode.smct.Constants;
import com.twopicode.smct.GsonRequest;
import com.twopicode.smct.R;
import com.twopicode.smct.Util;
import com.twopicode.smct.result.DateResult;
import com.twopicode.smct.result.ServiceResult;
import com.twopicode.smct.ServicesListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    static final int SELECT_FILTERS_REQUEST = 1;

    private String mUrl = "https://api.myjson.com/bins/56mu7";
    private ArrayList<Object> mResults = new ArrayList<>();
    private ServicesListAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView mFilterInfoVenue;
    private TextView mFilterInfoFDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.activity_main_toolbar));

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);

        mFilterInfoVenue = (TextView) findViewById(R.id.activity_main_filter_info_venue);
        mFilterInfoFDs = (TextView) findViewById(R.id.activity_main_filter_info_fds);

        ListView listView = (ListView) findViewById(R.id.activity_main_listView);
        mListAdapter = new ServicesListAdapter(getApplicationContext(), mResults);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListAdapter.isDateResultItem(position)) {
                    Intent intent = new Intent(MainActivity.this, ServiceDetailsActivity.class);
                    intent.putExtra(ServiceDetailsActivity.ARGS_DATE_RESULT, (DateResult) mListAdapter.getItem(position));
                    startActivity(intent);
                }
            }
        });
        sendRequest();
        setInitialFilters();
    }

    private void setFilterInfoViews() {
        mFilterInfoVenue.setText(getFilterInfoVenue());
        mFilterInfoFDs.setText(getFilterInfoFDs());
    }

    private String getFilterInfoVenue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        int locationIndex = preferences.getInt(getString(R.string.pref_key_location),
                Constants.LOCATION_BOTH);

        String location;

        switch (locationIndex) {
            case Constants.LOCATION_WORONORA:
                location = getString(R.string.woronora);
                break;
            case Constants.LOCATION_EASTERN_SUBURBS:
                location = getString(R.string.eastern_suburbs);
                break;
            default:
                location = getString(R.string.all);
        }

        return String.format(getString(R.string.venue_filter_info), location);
    }

    private String getFilterInfoFDs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> selectedFDs = Util.fromJson(preferences.getString(getString(R.string.pref_key_director), null));
        if (selectedFDs == null || selectedFDs.size() == 0) {
            return String.format(getString(R.string.fd_filter_info), getString(R.string.all));
        } else {
            return String.format(getString(R.string.fd_filter_info),
                    String.format(getString(R.string.fd_filter_info_2),
                            Integer.toString(selectedFDs.size())));
        }
    }

    private void sendRequest() {
        final RequestQueue queue = Volley.newRequestQueue(this);
        GsonRequest<ServiceResult> gsonRequest = new GsonRequest<>(mUrl, ServiceResult.class, null,
                new Response.Listener<ServiceResult>() {
                    @Override
                    public void onResponse(ServiceResult response) {
                        if (response != null) updateResults(response.GetTwoPiResult);
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("DEV9", "network error");
                    }
                }
        );
        queue.add(gsonRequest);
    }

    private void updateResults(ArrayList<DateResult> results) {
        mResults.clear();
        if (results != null)
            mResults.addAll(results);
        mListAdapter.setAllItems(results);
        mSwipeLayout.setRefreshing(false);
        Toast.makeText(this, "List updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.activity_main_menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mListAdapter.setFilterText(s);
                mListAdapter.notifyDataSetChangedAndFilter();
                return false;
            }
        });

        return true;
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILTERS_REQUEST && resultCode == RESULT_OK) {
            Bundle result = data.getExtras();
            if (result != null) {
                setFilters(result.getInt(FiltersActivity.LOCATION, Constants.LOCATION_BOTH),
                        (ArrayList<String>)result.getSerializable(FiltersActivity.DIRECTORS));
            }
        }
    }

    private void setInitialFilters() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mListAdapter.setFilters(
                prefs.getInt(getString(R.string.pref_key_location), Constants.LOCATION_BOTH),
                Util.fromJson(prefs.getString(getString(R.string.pref_key_director), null)));
        setFilterInfoViews();
    }

    private void setFilters(int location, ArrayList<String> directors) {
        saveFiltersToPersistentStorage(location, directors);
        mListAdapter.setFilters(location, directors);
        mListAdapter.notifyDataSetChangedAndFilter();
        setFilterInfoViews();
    }

    private void saveFiltersToPersistentStorage(int location, ArrayList<String> directors) {
        SharedPreferences.Editor prefsEditor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();

        prefsEditor.putInt(getString(R.string.pref_key_location), location);
        prefsEditor.putString(getString(R.string.pref_key_director), Util.toJson(directors));
        prefsEditor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_menu_filter:
                startFiltersActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startFiltersActivity() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = new Intent(MainActivity.this, FiltersActivity.class);

        intent.putExtra(FiltersActivity.ARGS_DIRECTORS,
                ServiceResult.getUniqueFuneralDirectorsGeneric(mResults));

        intent.putExtra(FiltersActivity.DIRECTORS,
                Util.fromJson(preferences.getString(getString(R.string.pref_key_director),
                        null)));

        intent.putExtra(FiltersActivity.LOCATION,
                preferences.getInt(getString(R.string.pref_key_location),
                        Constants.LOCATION_BOTH));

        startActivityForResult(intent, SELECT_FILTERS_REQUEST);
    }

    /** Swipe view pull down **/
    @Override
    public void onRefresh() {
        sendRequest();
    }
}