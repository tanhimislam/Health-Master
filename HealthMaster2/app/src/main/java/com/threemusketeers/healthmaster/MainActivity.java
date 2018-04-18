package com.threemusketeers.healthmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewNavBarName;
    private TextView textViewNavBarEmail;

    private TextView textViewMainName;
    private TextView textViewMainAge;
    private TextView textViewMainHeight;
    private TextView textViewMainWeight;
    private TextView textViewMainSex;
    private TextView textViewMainBmi;
    private TextView textViewMainCondition;

    private Button buttonMainPlan;
    private Button buttonMainReminder;
    private Button buttonMainNutrients;
    private Button buttonMainFoodExchange;

    private DatabaseHelper myDb;

    public static int food_plan;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewMainName = (TextView) findViewById(R.id.textViewMainName);
        textViewMainAge = (TextView) findViewById(R.id.textViewMainAge);
        textViewMainHeight = (TextView) findViewById(R.id.textViewMainHeight);
        textViewMainWeight = (TextView) findViewById(R.id.textViewMainWeight);
        textViewMainSex = (TextView) findViewById(R.id.textViewMainSex);
        textViewMainBmi = (TextView) findViewById(R.id.textViewMainBmi);
        textViewMainCondition = (TextView) findViewById(R.id.textViewMainCondition);

        buttonMainPlan = (Button) findViewById(R.id.buttonMainPlan);
        buttonMainReminder = (Button) findViewById(R.id.buttonMainReminder);
        buttonMainNutrients = (Button) findViewById(R.id.buttonMainNutrients);
        buttonMainFoodExchange = (Button) findViewById(R.id.buttonMainFoodExchange);

        firebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();

        MenuItem tools = menu.findItem(R.id.others);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);

        navigationView.setNavigationItemSelectedListener(this);

        myDb = new DatabaseHelper(this);

        View header = navigationView.getHeaderView(0);
        textViewNavBarEmail = (TextView) header.findViewById(R.id.textViewNavBarEmail);
        textViewNavBarName = (TextView) header.findViewById(R.id.textViewNavBarName);

        textViewNavBarEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        ViewAll();

        buttonMainPlan.setOnClickListener(this);
        buttonMainReminder.setOnClickListener(this);
        buttonMainNutrients.setOnClickListener(this);
        buttonMainFoodExchange.setOnClickListener(this);
    }

    private void ViewAll() {

        int checkEntry = 0;

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            finish();
            startActivity(new Intent(this, userinfo.class));
            return;
        }

        while (res.moveToNext()) {
            if (res.getString(0).equals(firebaseAuth.getCurrentUser().getEmail())) {
                textViewNavBarName.setText(res.getString(1));
                textViewMainName.setText(res.getString(1));
                textViewMainAge.setText(res.getString(2));
                textViewMainSex.setText(res.getString(3));
                float height = Float.parseFloat(res.getString(4));
                textViewMainHeight.setText(String.valueOf(height));
                float weight = Float.parseFloat(res.getString(5));

                textViewMainWeight.setText(new DecimalFormat("##.##").format(weight));

                textViewMainBmi.setText(res.getString(7));
                checkEntry = 1;
            }
        }

        if (checkEntry == 0) {
            finish();
            startActivity(new Intent(this, userinfo.class));
            return;
        }

        if (Integer.parseInt(textViewMainBmi.getText().toString()) < 20) {
            textViewMainCondition.setTextColor(Color.YELLOW);
            textViewMainCondition.setText("UNDERWEIGHT");
        } else if (Integer.parseInt(textViewMainBmi.getText().toString()) >= 20 && Integer.parseInt(textViewMainBmi.getText().toString()) < 25) {
            textViewMainCondition.setTextColor(Color.GREEN);
            textViewMainCondition.setText("NORMAL");
        } else {
            textViewMainCondition.setTextColor(Color.RED);
            textViewMainCondition.setText("OVERWEIGHT");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangla);

        TextView my_text = (TextView) findViewById(R.id.my_text);

        TextView tv1 = new TextView(MainActivity.this);
        tv1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/text.ttf"));
        tv1.setText("Sample text");


        TextView tv2 = new TextView(MainActivity.this);
        tv2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/cursif.ttf"));
        tv2.setText("Cursif text");

        my_text.addView(tv1);
        my_text.addView(tv2);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            firebaseAuth.signOut();
            cancelAlarms();
            finish();
            startActivity(new Intent(this, login.class));
            return true;
        } else if (id == R.id.action_like) {
            String facebookUrl = "https://www.facebook.com/health.master.bd";
            try {
                int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/354892291556599")));
                }
            } catch (Exception e) {
                // Facebook is not installed. Open the browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
        } else if (id == R.id.action_rate) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_aboutUs) {
            finish();
            startActivity(new Intent(this, AboutUs.class));
        } else if (id == R.id.nav_reminder) {
            finish();
            startActivity(new Intent(this, reminder.class));
        } else if (id == R.id.nav_hospital) {
            finish();
            startActivity(new Intent(this, MapsActivity.class));
        } else if (id == R.id.nav_nutrition) {
            if (isOnline()) {
                finish();
                startActivity(new Intent(this, FoodNutrition.class));
            } else {
                Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_LONG).setActionTextColor(Color.GREEN).
                        setAction("TURN ON", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivityForResult(settingsIntent, 9003);
                            }
                        }).show();
            }
        } else if (id == R.id.nav_exit) {
            System.exit(0);
        } else if (id == R.id.nav_profile) {
            finish();
            startActivity(new Intent(this, Profile.class));
        } else if (id == R.id.nav_plan) {
            ViewPlan();
            finish();
            startActivity(new Intent(this, FoodPlan.class));
        } else if (id == R.id.nav_exchange) {
            finish();
            startActivity(new Intent(this, FoodExchange.class));
        } else if (id == R.id.nav_tips) {
            if (!isOnline()) {
                Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_LONG).setActionTextColor(Color.GREEN).
                        setAction("TURN ON", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivityForResult(settingsIntent, 9003);
                            }
                        }).show();
            } else {
                finish();
                startActivity(new Intent(this, ImageListView.class));
            }
        } else if (id == R.id.nav_references) {
            finish();
            startActivity(new Intent (this, References.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonMainPlan) {
            ViewPlan();
            finish();
            startActivity(new Intent(this, FoodPlan.class));
        } else if (view == buttonMainReminder) {
            finish();
            startActivity(new Intent(this, reminder.class));
        } else if (view == buttonMainNutrients) {
            if (isOnline()) {
                finish();
                startActivity(new Intent(this, FoodNutrition.class));
            } else {
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setActionTextColor(Color.GREEN).
                        setAction("TURN ON", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivityForResult(settingsIntent, 9003);
                            }
                        }).show();
            }
        } else if (view == buttonMainFoodExchange) {
            finish();
            startActivity(new Intent(this, FoodExchange.class));
        }
    }

    private void cancelAlarms() {
        Intent intent;
        PendingIntent pendingIntent;
        AlarmManager alarmManager;

        intent = new Intent(getApplicationContext(), NotificationBreakfast.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 702, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        editor.putInt("BreakfastHour", 123123).apply();
        editor.putInt("BreakfastMinute", 123123).apply();

        intent = new Intent(getApplicationContext(), NotificationLunch.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 701, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        editor.putInt("LunchHour", 123123).apply();
        editor.putInt("LunchMinute", 123123).apply();

        intent = new Intent(getApplicationContext(), NotificationSnacks.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 703, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        editor.putInt("SnacksHour", 123123).apply();
        editor.putInt("SnacksMinute", 123123).apply();

        intent = new Intent(getApplicationContext(), NotificationDinner.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 704, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        editor.putInt("DinnerHour", 123123).apply();
        editor.putInt("DinnerMinute", 123123).apply();
    }

    private void ViewPlan() {
        if (Integer.parseInt(textViewMainAge.getText().toString()) >= 10 && Integer.parseInt(textViewMainAge.getText().toString()) < 15) {
            food_plan = R.layout.activity_fp_age10to15;
        } else if (Integer.parseInt(textViewMainAge.getText().toString()) >= 15 && Integer.parseInt(textViewMainAge.getText().toString()) < 18) {
            food_plan = R.layout.activity_fp_age15to18;
        } else if (Integer.parseInt(textViewMainAge.getText().toString()) > 50) {
            food_plan = R.layout.activity_fp_oldage;
        } else {
            if (Integer.parseInt(textViewMainBmi.getText().toString()) < 20) {
                if (Objects.equals(textViewMainSex.getText().toString().toUpperCase(), "MALE")) {
                    food_plan = R.layout.activity_fp_lowbmi_male;
                } else if (Objects.equals(textViewMainSex.getText().toString().toUpperCase().toUpperCase(), "FEMALE")) {
                    food_plan = R.layout.activity_fp_lowbmi_female;
                }
            } else if (Integer.parseInt(textViewMainBmi.getText().toString()) >= 20 && Integer.parseInt(textViewMainBmi.getText().toString()) < 25) {
                if (Objects.equals(textViewMainSex.getText().toString().toUpperCase(), "MALE")) {
                    food_plan = R.layout.activity_fp_standardbmi_male;
                } else if (Objects.equals(textViewMainSex.getText().toString().toUpperCase().toUpperCase(), "FEMALE")) {
                    food_plan = R.layout.activity_fp_standardbmi_female;
                }
            } else {
                if (Objects.equals(textViewMainSex.getText().toString().toUpperCase(), "MALE")) {
                    food_plan = R.layout.activity_fp_highbmi_male;
                } else if (Objects.equals(textViewMainSex.getText().toString().toUpperCase().toUpperCase(), "FEMALE")) {
                    food_plan = R.layout.activity_fp_highbmi_female;
                }
            }
        }
    }
}
