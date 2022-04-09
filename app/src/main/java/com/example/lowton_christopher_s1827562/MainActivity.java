package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback, LocationListener
{
    //Christopher Lowton - S1827562
    private LinkedList<Item> items;
    private String result;
    private String[] urlSources = new String[] { "https://trafficscotland.org/rss/feeds/currentincidents.aspx", "https://trafficscotland.org/rss/feeds/roadworks.aspx", "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx" };


    private GoogleMap map;

    protected LocationManager locationManager;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Roadworks");

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this::onMapReady);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public boolean showDatePickerDialog(MenuItem item) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra(SearchManager.QUERY, s);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, ListActivity.class);
        switch (item.getItemId()) {
            case R.id.action_allRoadworks:
                intent.putExtra("type", 3);
                break;

            case R.id.action_currentIncidents:
                intent.putExtra("type", 0);
                break;

            case R.id.action_roadworks:
                intent.putExtra("type", 1);
                break;

            case R.id.action_plannedRoadworks:
                intent.putExtra("type", 2);
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        startActivity(intent);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));

        findTraffic();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
                return;
        }
    }

    public void findTraffic()
    {
        new Thread(new Task(this, urlSources)).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LinkedList<Item> dateFilterData() {
        LocalDate searchDate = LocalDate.now();
        LinkedList<Item> matched_items = new LinkedList<Item>();
        for (Item item : items) {
            if ((item.getStartDate().isBefore(searchDate.atStartOfDay()) && item.getEndDate().isAfter(searchDate.atStartOfDay()))
                    || item.getStartDate().isEqual(searchDate.atStartOfDay()) || item.getEndDate().isEqual(searchDate.atStartOfDay())) {
                matched_items.add(item);
            }
        }
        return matched_items;
    }

    private class Task implements Runnable {
        //Christopher Lowton - S1827562
        private Context context;
        private String[] urls;

        public Task(Context context, String[] urls) {
            this.context = context;
            this.urls = urls;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            items = new LinkedList<Item>();

            for (String url : urls) {
                try {
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    //Use StringBuilder so memory is not reallocated as long string is build, greatly improves loading time
                    StringBuilder str = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        str.append(inputLine);
                        Log.e("MyTag", inputLine);
                    }
                    result = str.toString();
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception in run");
                }

                Parser parser = new Parser();
                items.addAll(parser.parseXmlString(result));
            }

            items = dateFilterData();

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    for (Item item : items) {
                        LatLng latlng = new LatLng(item.getLat(), item.getLng());
                        map.addMarker(new MarkerOptions().position(latlng).title(item.getTitle()));
                    }
                }
            });
        }
    }
}
