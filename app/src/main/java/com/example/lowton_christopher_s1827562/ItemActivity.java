package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemActivity extends AppCompatActivity {
    //Christopher Lowton - S1827562
    private Item item;
    private Integer type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_item);
        getSupportActionBar().setTitle("Roadworks Details");

        TextView rawData = (TextView) findViewById(R.id.rawData);

        Intent intent = getIntent();
        this.item = (Item)intent.getSerializableExtra("Item");
        this.type = intent.getIntExtra("type", 2);

        rawData.setText(item.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
