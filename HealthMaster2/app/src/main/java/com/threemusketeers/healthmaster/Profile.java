package com.threemusketeers.healthmaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private EditText textViewName;
    private EditText textViewAge;
    private TextView textViewSex;
    private TextView textViewHeight;
    private EditText textViewWeight;
    private TextView textViewBMI;
    private TextView textViewCalorie;
    private ImageView imageViewProfile;

    private Button buttonSave;
    private Button buttonCancel;

    private DatabaseHelper mydb;
    private FirebaseAuth firebaseAuth;

    private int selectedFeet;
    private float selectedInch;

    private String userChoosenTask;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewName = (EditText) findViewById(R.id.editTextName1);
        textViewAge = (EditText) findViewById(R.id.editTextAge1);
        textViewSex = (TextView) findViewById(R.id.editTextSex1);
        textViewHeight = (TextView) findViewById(R.id.editTextHeight1);
        textViewWeight = (EditText) findViewById(R.id.editTextWeight1);
        textViewBMI = (TextView) findViewById(R.id.editTextBmi1);
        textViewCalorie = (TextView) findViewById(R.id.editTextCalorie1);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        mydb = new DatabaseHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();

        ViewAll();

        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        imageViewProfile.setOnClickListener(this);
        textViewHeight.setOnClickListener(this);
        textViewSex.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        /* intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640); */
        try {
            //intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent, "Choose Image from"), SELECT_FILE);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Picture"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                //picUri = data.getData(); // changed
                //performCrop(); //changed
                onCaptureImageResult(data);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bm = getRoundedShape(bm);

        /* Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bm = extras.getParcelable("data");
            bm = getRoundedShape(bm); */
        imageViewProfile.setImageBitmap(bm);
        //}
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        // TODO Auto-generated method stub
        int targetWidth = 250;
        int targetHeight = 250;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);
        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        /* File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } */
        thumbnail = getRoundedShape(thumbnail);
        imageViewProfile.setImageBitmap(thumbnail);
    }


    public void ViewAll() {
        Cursor res = mydb.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No information found", Toast.LENGTH_LONG).show();
            return;
        }

        while (res.moveToNext()) {
            if (res.getString(0).equals(firebaseAuth.getCurrentUser().getEmail())) {
                textViewName.setText(res.getString(1));
                textViewAge.setText(res.getString(2));
                textViewSex.setText(res.getString(3));
                float height = Float.parseFloat(res.getString(4));
                textViewHeight.setText(String.valueOf(height));
                if (res.getBlob(6) != null) {
                    Bitmap bp = DbBitmapUtility.getImage(res.getBlob(6));
                    imageViewProfile.setImageBitmap(bp);
                }
                textViewBMI.setText(res.getString(7));
                float weight = Float.parseFloat(res.getString(5));
                int calorie = (int) weight * 35;

                textViewWeight.setText(new DecimalFormat("##.##").format(weight));

                textViewCalorie.setText(String.valueOf(calorie));
            }
        }
    }

    public void getInch() {
        final CharSequence inch[] = new CharSequence[]{"0", ".5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Inches");
        builder.setItems(inch, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                selectedInch = Float.parseFloat(inch[which].toString());
                float height = (float) (((selectedFeet * 12) + selectedInch) * 2.54);
                textViewHeight.setText(String.valueOf(height));
            }
        });
        builder.show();
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

    @Override
    public void onClick(View view) {
        if (view == buttonSave) {

            if (textViewName.getText().length() == 0 || textViewAge.getText().length() == 0 || textViewWeight.getText().length() == 0) {
                Snackbar.make(view, "Fill up all the fields", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (Float.parseFloat(textViewWeight.getText().toString().trim()) < 10 || Float.parseFloat(textViewWeight.getText().toString().trim()) > 500) {
                Toast.makeText(this, "Invalid Weight. Range is between 10kg to 500kg", Toast.LENGTH_LONG).show();
                return;
            }

            float height = Float.parseFloat(textViewHeight.getText().toString());
            int bmi = (int) (Float.parseFloat(textViewWeight.getText().toString()) / ((height * height) / 10000));
            textViewBMI.setText(String.valueOf(bmi));

            int calorie = (int) (Float.parseFloat(textViewWeight.getText().toString()) * 35);
            textViewCalorie.setText(String.valueOf(calorie));


            byte[] photo;

            Bitmap bitmap = ((BitmapDrawable) imageViewProfile.getDrawable()).getBitmap();
            if (bitmap == null) {
                photo = null;
            } else {
                photo = DbBitmapUtility.getBytes(bitmap);
            }


            mydb.UpdateData(firebaseAuth.getCurrentUser().getEmail(), textViewName.getText().toString(), Integer.parseInt(textViewAge.getText().toString()),
                    textViewSex.getText().toString(), Float.parseFloat(textViewHeight.getText().toString()), Float.parseFloat(textViewWeight.getText().toString()),
                    photo, Integer.parseInt(textViewBMI.getText().toString()));

            Snackbar.make(view, "Profile Updated", Snackbar.LENGTH_SHORT).show();


        } else if (view == buttonCancel) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else if (view == textViewHeight) {

            final CharSequence feet[] = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Feet");
            builder.setItems(feet, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    selectedFeet = Integer.parseInt(feet[which].toString());
                    getInch();
                }
            });
            builder.show();
        } else if (view == textViewSex) {
            final CharSequence gender[] = new CharSequence[]{"Male", "Female", "Others"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Gender");
            builder.setItems(gender, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    textViewSex.setText(gender[which]);
                }
            });
            builder.show();
        } else if (view == imageViewProfile) {
            final CharSequence ImageOption[] = new CharSequence[]{"Take Picture", "Choose from Gallery", "Delete"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Image");
            builder.setItems(ImageOption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    boolean result = Utility.checkPermission(Profile.this);
                    if (ImageOption[which].equals("Take Picture")) {
                        userChoosenTask = "Take Picture";
                        if (result)
                            cameraIntent();
                    } else if (ImageOption[which].equals("Choose from Gallery")) {
                        userChoosenTask = "Choose from Gallery";
                        if (result)
                            galleryIntent();
                    } else if (ImageOption[which].equals("Delete")) {
                        imageViewProfile.setImageResource(R.drawable.profile_update);
                    }
                }
            });
            builder.show();
        }
    }
}



