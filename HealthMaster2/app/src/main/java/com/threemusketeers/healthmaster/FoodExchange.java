package com.threemusketeers.healthmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;

public class FoodExchange extends AppCompatActivity implements View.OnClickListener {

    private TableRow tableRowCarbohydrates;
    private TableRow tableRowProtein;
    private TableRow tableRowFat;
    private TableRow tableRowPulses;
    private TableRow tableRowMilk;
    private TableRow tableRowVegetables;
    private TableRow tableRowLeaves;
    private TableRow tableRowFruits;

    public static int foodExchangeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_exchange);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableRowCarbohydrates = (TableRow) findViewById(R.id.tableRowCarbohydrates);
        tableRowProtein = (TableRow) findViewById(R.id.tableRowProtein);
        tableRowFat = (TableRow) findViewById(R.id.tableRowFat);
        tableRowPulses = (TableRow) findViewById(R.id.tableRowPulses);
        tableRowMilk = (TableRow) findViewById(R.id.tableRowMilk);
        tableRowVegetables = (TableRow) findViewById(R.id.tableRowVegetables);
        tableRowLeaves = (TableRow) findViewById(R.id.tableRowLeaves);
        tableRowFruits = (TableRow) findViewById(R.id.tableRowFruits);

        tableRowCarbohydrates.setOnClickListener(this);
        tableRowProtein.setOnClickListener(this);
        tableRowFat.setOnClickListener(this);
        tableRowPulses.setOnClickListener(this);
        tableRowMilk.setOnClickListener(this);
        tableRowVegetables.setOnClickListener(this);
        tableRowLeaves.setOnClickListener(this);
        tableRowFruits.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //User clicked home, do whatever you want
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == tableRowCarbohydrates) {
            foodExchangeDetails = R.layout.activity_food_exchange_carbohydrates;
        } else if (view == tableRowProtein) {
            foodExchangeDetails = R.layout.activity_food_exchange_protein;
        } else if (view == tableRowFat) {
            foodExchangeDetails = R.layout.activity_food_exchange_fat;
        } else if (view == tableRowPulses) {
            foodExchangeDetails = R.layout.activity_food_exchange_pulses;
        } else if (view == tableRowMilk) {
            foodExchangeDetails = R.layout.activity_food_exchange_milk;
        } else if (view == tableRowVegetables) {
            foodExchangeDetails = R.layout.activity_food_exchange_vegetables;
        } else if (view == tableRowLeaves) {
            foodExchangeDetails = R.layout.activity_food_exchange_leaves;
        } else if (view == tableRowFruits) {
            foodExchangeDetails = R.layout.activity_food_exchange_fruits;
        }
        finish();
        startActivity(new Intent(this, FoodExchangeDetails.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
