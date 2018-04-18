package com.threemusketeers.healthmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class reminder extends AppCompatActivity {

    private Switch switchBreakFast;
    private TextView textViewBreakFast;
    private Switch switchLunch;
    private TextView textViewLunch;
    private Switch switchSnacks;
    private TextView textViewSnacks;
    private Switch switchDinner;
    private TextView textViewDinner;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private int hour, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchBreakFast = (Switch) findViewById(R.id.switchBreakFast);
        textViewBreakFast = (TextView) findViewById(R.id.textViewBreakFast);

        switchLunch = (Switch) findViewById(R.id.switchLunch);
        textViewLunch = (TextView) findViewById(R.id.textViewLunch);

        switchSnacks = (Switch) findViewById(R.id.switchSnacks);
        textViewSnacks = (TextView) findViewById(R.id.textViewSnacks);

        switchDinner = (Switch) findViewById(R.id.switchDinner);
        textViewDinner = (TextView) findViewById(R.id.textViewDinner);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        //Initializing Breakfast Time
        hour = sharedPreferences.getInt("BreakfastHour", 123123);
        minute = sharedPreferences.getInt("BreakfastMinute", 123123);

        if (hour != 123123) {
            if (Integer.toString(minute).length() < 2) {
                textViewBreakFast.setText(+hour + ":0" + minute + " AM");
            } else {
                textViewBreakFast.setText(+hour + ":" + minute + " AM");
            }
            switchBreakFast.setChecked(true);
        }


        //Initializing Lunch Time
        hour = sharedPreferences.getInt("LunchHour", 123123);
        minute = sharedPreferences.getInt("LunchMinute", 123123);

        if (hour != 123123) {
            if (hour > 12) hour = hour - 12;
            if (Integer.toString(minute).length() < 2) {
                textViewLunch.setText(+hour + ":0" + minute + " PM");
            } else {
                textViewLunch.setText(+hour + ":" + minute + " PM");
            }
            switchLunch.setChecked(true);
        }


        //Initializing Snacks Time
        hour = sharedPreferences.getInt("SnacksHour", 123123);
        minute = sharedPreferences.getInt("SnacksMinute", 123123);

        if (hour != 123123) {
            if (hour > 12) hour = hour - 12;
            if (Integer.toString(minute).length() < 2) {
                textViewSnacks.setText(+hour + ":0" + minute + " PM");
            } else {
                textViewSnacks.setText(+hour + ":" + minute + " PM");
            }
            switchSnacks.setChecked(true);
        }


        //Initializing Dinner Time
        hour = sharedPreferences.getInt("DinnerHour", 123123);
        minute = sharedPreferences.getInt("DinnerMinute", 123123);

        if (hour != 123123) {
            if (hour > 12) hour = hour - 12;
            if (Integer.toString(minute).length() < 2) {
                textViewDinner.setText(+hour + ":0" + minute + " PM");
            } else {
                textViewDinner.setText(+hour + ":" + minute + " PM");
            }
            switchDinner.setChecked(true);
        }


        switchBreakFast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isDataSet;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDataSet = false;
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    final int minute = mcurrentTime.get(Calendar.MINUTE);
                    final TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(reminder.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 6 || selectedHour > 9 || (selectedHour == 9 && selectedMinute > 0)) {
                                    Toast.makeText(getApplicationContext(), "Please select breakfast time between 6 AM - 9 AM. Recommended time is between 7 AM - 8 AM.", Toast.LENGTH_LONG).show();
                                    switchBreakFast.setChecked(false);
                                    return;
                                }
                            isDataSet = true;
                            editor.putInt("BreakfastHour", selectedHour).apply();
                            editor.putInt("BreakfastMinute", selectedMinute).apply();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                                calendar.add(Calendar.DATE, 1);
                            }
                            Intent intent = new Intent(getApplicationContext(), NotificationBreakfast.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 702, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            if (Integer.toString(selectedMinute).length() < 2) {
                                textViewBreakFast.setText(+selectedHour + ":0" + selectedMinute + " AM");
                            } else
                                textViewBreakFast.setText(+selectedHour + ":" + selectedMinute + " AM");
                        }
                    }, hour, minute, false);

                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                    mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface arg0) {
                            if (!isDataSet) {
                                switchBreakFast.setChecked(false);
                                isDataSet = false;
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), NotificationBreakfast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 702, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    editor.putInt("BreakfastHour", 123123).apply();
                    editor.putInt("BreakfastMinute", 123123).apply();
                    textViewBreakFast.setText("Off");
                }
            }
        });


        switchLunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isDataSet;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDataSet = false;
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    final int minute = mcurrentTime.get(Calendar.MINUTE);
                    final TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(reminder.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 12 || selectedHour > 15 || (selectedHour == 15 && selectedMinute > 0)) {
                                Toast.makeText(getApplicationContext(), "Please select lunch time between 12 PM - 3 PM. Recommended time is between 12:30PM - 2 PM.", Toast.LENGTH_LONG).show();
                                switchLunch.setChecked(false);
                                return;
                            }
                            isDataSet = true;
                            editor.putInt("LunchHour", selectedHour).apply();
                            editor.putInt("LunchMinute", selectedMinute).apply();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                                calendar.add(Calendar.DATE, 1);
                            }
                            Intent intent = new Intent(getApplicationContext(), NotificationLunch.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 701, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            if (selectedHour > 12) selectedHour = selectedHour - 12;
                            if (Integer.toString(selectedMinute).length() < 2) {
                                textViewLunch.setText(+selectedHour + ":0" + selectedMinute + " PM");
                            } else
                                textViewLunch.setText(+selectedHour + ":" + selectedMinute + " PM");
                        }
                    }, hour, minute, false);

                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                    mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface arg0) {
                            if (!isDataSet) {
                                switchLunch.setChecked(false);
                                isDataSet = false;
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), NotificationLunch.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 701, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    editor.putInt("LunchHour", 123123).apply();
                    editor.putInt("LunchMinute", 123123).apply();
                    textViewLunch.setText("Off");
                }
            }
        });


        switchSnacks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isDataSet;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDataSet = false;
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    final int minute = mcurrentTime.get(Calendar.MINUTE);
                    final TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(reminder.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 16 || selectedHour > 18 || (selectedHour == 18 && selectedMinute > 0)) {
                                Toast.makeText(getApplicationContext(), "Please select snacks time between 4 PM - 6 PM. Recommended time is between 4:30 PM - 5.30 PM.", Toast.LENGTH_LONG).show();
                                switchSnacks.setChecked(false);
                                return;
                            }
                            isDataSet = true;
                            editor.putInt("SnacksHour", selectedHour).apply();
                            editor.putInt("SnacksMinute", selectedMinute).apply();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                                calendar.add(Calendar.DATE, 1);
                            }
                            Intent intent = new Intent(getApplicationContext(), NotificationSnacks.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 703, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            if (selectedHour > 12) selectedHour = selectedHour - 12;
                            if (Integer.toString(selectedMinute).length() < 2) {
                                textViewSnacks.setText(+selectedHour + ":0" + selectedMinute + " PM");
                            } else
                                textViewSnacks.setText(+selectedHour + ":" + selectedMinute + " PM");
                        }
                    }, hour, minute, false);

                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                    mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface arg0) {
                            if (!isDataSet) {
                                switchSnacks.setChecked(false);
                                isDataSet = false;
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), NotificationSnacks.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 703, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    editor.putInt("SnacksHour", 123123).apply();
                    editor.putInt("SnacksMinute", 123123).apply();
                    textViewSnacks.setText("Off");
                }
            }
        });


        switchDinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isDataSet;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDataSet = false;
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    final int minute = mcurrentTime.get(Calendar.MINUTE);
                    final TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(reminder.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 19 || selectedHour > 22 || (selectedHour == 22 && selectedMinute > 30)) {
                                Toast.makeText(getApplicationContext(), "Please select dinner time between 7 PM - 10:30 PM. Recommended time is between 7:30 PM - 9 PM.", Toast.LENGTH_LONG).show();
                                switchDinner.setChecked(false);
                                return;
                            }
                            isDataSet = true;
                            editor.putInt("DinnerHour", selectedHour).apply();
                            editor.putInt("DinnerMinute", selectedMinute).apply();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                                calendar.add(Calendar.DATE, 1);
                            }
                            Intent intent = new Intent(getApplicationContext(), NotificationDinner.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 704, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            if (selectedHour > 12) selectedHour = selectedHour - 12;
                            if (Integer.toString(selectedMinute).length() < 2) {
                                textViewDinner.setText(+selectedHour + ":0" + selectedMinute + " PM");
                            } else
                                textViewDinner.setText(+selectedHour + ":" + selectedMinute + " PM");
                        }
                    }, hour, minute, false);

                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                    mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface arg0) {
                            if (!isDataSet) {
                                switchDinner.setChecked(false);
                                isDataSet = false;
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(getApplicationContext(), NotificationDinner.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 704, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    editor.putInt("DinnerHour", 123123).apply();
                    editor.putInt("DinnerMinute", 123123).apply();
                    textViewDinner.setText("Off");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
}



