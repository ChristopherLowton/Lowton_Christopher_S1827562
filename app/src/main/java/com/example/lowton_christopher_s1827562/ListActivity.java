package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity  extends AppCompatActivity {
    //Christopher Lowton - S1827562
    ListView listView;
    private String result = "";
    private LinkedList<Item> items = new LinkedList<Item>();
    private String currentIncidents_urlSource="https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String roadworks_urlSource="https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworks_urlSource="https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String[] urlSources;
    private int type = 2;
    private TextView emptyText;
    private ProgressBar spinner;
    private int year = -1;
    private int month = -1;
    private int day = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle("Planned Roadworks");

        emptyText = (TextView)findViewById(R.id.empty);

        listView = (ListView)findViewById(R.id.listView);
        listView.setEmptyView(emptyText);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item selected_item = (Item) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ListActivity.this, ItemActivity.class);

                intent.putExtra("Item", selected_item);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        type = intent.getIntExtra("type", 2);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);

        switch (type) {
            case 0:
                urlSources = new String[] { currentIncidents_urlSource };
                getSupportActionBar().setTitle("Current Incidents");
                break;
            case 1:
                urlSources = new String[] { roadworks_urlSource };
                getSupportActionBar().setTitle("Roadworks");
                break;
            case 2:
                urlSources = new String[] { plannedRoadworks_urlSource };
                getSupportActionBar().setTitle("Planned Roadworks");
                break;
            case 3:
                urlSources = new String[] { currentIncidents_urlSource, roadworks_urlSource, plannedRoadworks_urlSource };
                getSupportActionBar().setTitle("All Roadworks");
                break;
        }

        String query = "";
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }

        startProgress(query);
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
                intent.putExtra("type", type);
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

            case R.id.datePicker:
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        startActivity(intent);
        return true;
    }

    private LinkedList<Item> searchData(String query) {
        query = query.toLowerCase();
        LinkedList<Item> matched_items = new LinkedList<Item>();
        for (Item item : items) {
            if (item.getTitle().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getDescription().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getLink().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getGeorssPoint().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getAuthor().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getComments().toLowerCase().contains(query)) {
                matched_items.add(item);
            } else if (item.getPubDate().toLowerCase().contains(query)) {
                matched_items.add(item);
            }
        }
        return matched_items;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LinkedList<Item> dateFilterData(int year, int month, int day) {
        month = month + 1;
        LocalDate searchDate = LocalDate.parse(Integer.toString(year) + " " + Integer.toString(month) + " " + Integer.toString(day), DateTimeFormatter.ofPattern("yyyy M d"));
        LinkedList<Item> matched_items = new LinkedList<Item>();
        for (Item item : items) {
            if ((item.getStartDate().isBefore(searchDate.atStartOfDay()) && item.getEndDate().isAfter(searchDate.atStartOfDay()))
                    || item.getStartDate().isEqual(searchDate.atStartOfDay()) || item.getEndDate().isEqual(searchDate.atStartOfDay())) {
                matched_items.add(item);
            }
        }
        return matched_items;
    }

    public void startProgress(String query)
    {
        new Thread(new Task(this, urlSources, query)).start();
    }

    private class Task implements Runnable {
        //Christopher Lowton - S1827562
        private Context context;
        private String[] urls;
        private String query;

        public Task(Context context, String[] urls, String query) {
            this.context = context;
            this.urls = urls;
            this.query = query;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag", "in run");

            items = new LinkedList<Item>();

            for (String url : urls) {
                try {
                    Log.e("MyTag", "in try");
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    Log.e("MyTag", "after ready");

                    emptyText.setText("Reading raw input...");

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

                emptyText.setText("Parsing xml...");

                Parser parser = new Parser();
                items.addAll(parser.parseXmlString(result));
            }

            if (this.query.isEmpty() == false) {
                emptyText.setText("Searching data...");
                items = searchData(this.query);
            }

            if (year > 2000 && month > 0 && day > 0) {
                items = dateFilterData(year, month, day);
            }

            //Sort list so the user can easily find the listing they might be looking for
            Collections.sort(items);

            ListActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    if (items.size() > 0) {
                        ArrayList<String> titles = new ArrayList<String>();
                        for (Item item : items) {
                            titles.add(item.getTitle());
                        }

                        ItemAdapter adapter = new ItemAdapter(context, R.layout.list_item, items);
                        listView.setAdapter(adapter);
                    } else {
                        emptyText.setText("No results found");
                    }
                    spinner.setVisibility(View.GONE);
                }
            });
        }
    }
}


