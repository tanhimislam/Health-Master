package com.threemusketeers.healthmaster;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TipsDetails extends AppCompatActivity {

    private TextView textViewTipsTitle;
    private TextView textViewTipsDetails;
    private ImageView imageViewTipsImage;

    private int tipsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_details);

        textViewTipsTitle = (TextView) findViewById(R.id.textViewTipsTitle);
        textViewTipsDetails = (TextView) findViewById(R.id.textViewTipsDetails);
        imageViewTipsImage = (ImageView) findViewById(R.id.imageViewTipsImage);

        textViewTipsTitle.setText(getIntent().getExtras().getString("TITLE"));
        tipsNum = getIntent().getExtras().getInt(ImageListView.BITMAP_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new AsyncImage().execute();
    }

    private class AsyncImage extends AsyncTask<Void, Void, Bitmap> {

        ProgressDialog loading;
        Bitmap myBitmap;
        HttpURLConnection connection;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TipsDetails.this, "Loading...", "Please Wait...", true, true);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(GetAllImages.imageURLs[tipsNum]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                return myBitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                return null;
            } finally {
                connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(Bitmap myBitmap) {
            loading.dismiss();
            imageViewTipsImage.setImageBitmap(myBitmap);
            new AsyncText().execute();
        }
    }

    private class AsyncText extends AsyncTask<Void, Void, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(Void... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("http://bddroid.com/HealthMaster/detailstips.php?eid=" + String.valueOf(tipsNum + 1));

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }

            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(25000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we receive data
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("no rows")) {
                // Do some action if no data from database
                Toast.makeText(TipsDetails.this, "No entries found", Toast.LENGTH_LONG).show();

            } else {
                try {
                    result = result.replaceAll("<br/>", "\n").replaceAll("<br>", "\n").replaceAll("<p>", "\n\n").replaceAll("</p>", "");
                    textViewTipsDetails.setText(result);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
