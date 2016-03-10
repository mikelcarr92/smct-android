package com.twopicode.smct.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.twopicode.smct.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/****************************************
 * Created by michaelcarr on 17/11/15.
 ****************************************/
public class FiltersActivity extends AppCompatActivity {

    public static final String LOCATION = "FiltersActivity.LOCATION";
    public static final String DIRECTORS = "FiltersActivity.DIRECTORS";

    public static final String ARGS_DIRECTORS = "FiltersActivity.ARGS_DIRECTORS";

    private Spinner mLocationsSpinner;
    private ListView mDirectorsListView;
    private ArrayAdapter<String> mLocationsArrayAdapter;
    private ArrayAdapter<String> mDirectorsArrayAdapter;
    private ArrayList<String> mDirectors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationsSpinner = (Spinner) findViewById(R.id.activity_filters_spinner);
        mDirectorsListView = (ListView) findViewById(R.id.activity_filters_listView);

        List<String> locations = Arrays.asList(getResources().getStringArray(R.array.venues));
        populateDirectors(savedInstanceState);

        mLocationsArrayAdapter = new ArrayAdapter<>(FiltersActivity.this,
                android.R.layout.simple_spinner_dropdown_item, locations);

        mDirectorsArrayAdapter = new ArrayAdapter<>(FiltersActivity.this,
                android.R.layout.simple_list_item_multiple_choice, mDirectors);

        mLocationsSpinner.setAdapter(mLocationsArrayAdapter);
        mDirectorsListView.setAdapter(mDirectorsArrayAdapter);

        mDirectorsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        setSelections(savedInstanceState);
    }

    @SuppressWarnings("all")
    private void populateDirectors(Bundle savedInstanceState) {
        mDirectors = new ArrayList<>();

        if (getIntent().hasExtra(ARGS_DIRECTORS) &&
                getIntent().getSerializableExtra(ARGS_DIRECTORS) != null) {
                mDirectors.addAll((ArrayList<String>) getIntent().getSerializableExtra(ARGS_DIRECTORS));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("all")
    private void setSelections(Bundle savedInstanceState) {

        if (getIntent().getExtras() != null) {

            mLocationsSpinner.setSelection(getIntent().getExtras().getInt(LOCATION, 0));

            if ((ArrayList<String>)getIntent().getSerializableExtra(DIRECTORS) != null) {
                for (String selectedFD : (ArrayList<String>) getIntent().getSerializableExtra(DIRECTORS)) {
                    mDirectorsListView.setItemChecked(getIndexOfDirector(selectedFD), true);
                }
            }

        }
    }

    private int getIndexOfDirector(String directorName) {
        for (String name : mDirectors)
            if (name.equals(directorName))
                return mDirectors.indexOf(name);

        return 0;
    }

    private ArrayList<String> getSelectedFDs() {

        ArrayList<String> selectedFDs = new ArrayList<>();
        SparseBooleanArray sba = mDirectorsListView.getCheckedItemPositions();

        for (int i = 0; i < mDirectorsArrayAdapter.getCount(); i++) {
            if (sba.get(i)) {
                selectedFDs.add(mDirectors.get(i));
            }
        }

        return selectedFDs;
    }

    @Override
    public void finish() {

        int location = mLocationsSpinner.getSelectedItemPosition();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(LOCATION, location);
        returnIntent.putExtra(DIRECTORS, getSelectedFDs());
        setResult(Activity.RESULT_OK, returnIntent);

        super.finish();
    }
}