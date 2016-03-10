package com.twopicode.smct.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twopicode.smct.R;
import com.twopicode.smct.result.DateResult;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/****************************************
 * Created by michaelcarr on 17/11/15.
 ****************************************/
public class ServiceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ARGS_DATE_RESULT = "ServiceDetailsActivity.ARGS_DATE_RESULT";
    private static final String DATE_PATTERN = "EEEE, MMMM dd hh:mm aa";

    private TextView mDateAndTimeTextView;
    private TextView mLocationTextView;
    private TextView mVenueTextView;
    private TextView mTypeTextView;
    private TextView mFuneralDirectorTextView;

    private DateResult mDateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        if (getIntent().getExtras() != null)
            mDateResult = (DateResult) getIntent().getSerializableExtra(ARGS_DATE_RESULT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mDateResult.getName());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDateAndTimeTextView = (TextView) findViewById(R.id.activity_service_time_and_date);
        mLocationTextView = (TextView) findViewById(R.id.activity_service_location);
        mVenueTextView = (TextView) findViewById(R.id.activity_service_venue);
        mTypeTextView = (TextView) findViewById(R.id.activity_service_type);
        mFuneralDirectorTextView = (TextView) findViewById(R.id.activity_service_director);

        setUpViews();
    }

    private void setUpViews() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_PATTERN).withLocale(Locale.ENGLISH);
        mDateAndTimeTextView.setText(mDateResult.getServiceDateTime().toString(dtf));
        mLocationTextView.setText(mDateResult.getLocation());
        mVenueTextView.setText("");
//        mTypeTextView.setText(mDateResult.getType()) always null with current data
        mTypeTextView.setText(mDateResult.getType());
        mFuneralDirectorTextView.setText(mDateResult.getDirector());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
