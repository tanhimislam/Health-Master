package com.threemusketeers.healthmaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class userinfo extends AppCompatActivity implements View.OnClickListener {

    private Uri picUri;

    private ImageView imageView;
    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextSex;
    private EditText editTextFeet;
    private EditText editTextInch;
    private EditText editTextWeight;
    private Button buttonDoneInfo;

    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int CROP_PIC = 27;

    String userEmail;
    String userChoosenTask;
    DatabaseHelper mydb;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new DatabaseHelper(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextSex = (EditText) findViewById(R.id.editTextSex);
        editTextFeet = (EditText) findViewById(R.id.editTextFeet);
        editTextInch = (EditText) findViewById(R.id.editTextInch);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        buttonDoneInfo = (Button) findViewById(R.id.buttonDoneInfo);


        firebaseAuth = FirebaseAuth.getInstance();

        editTextSex.setFocusable(false);
        editTextSex.setClickable(true);

        editTextFeet.setFocusable(false);
        editTextFeet.setClickable(true);

        editTextInch.setFocusable(false);
        editTextInch.setClickable(true);

        imageView.setOnClickListener(this);
        editTextSex.setOnClickListener(this);
        editTextFeet.setOnClickListener(this);
        editTextInch.setOnClickListener(this);
        buttonDoneInfo.setOnClickListener(this);
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
        imageView.setImageBitmap(bm);
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
        imageView.setImageBitmap(thumbnail);
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 640);
            cropIntent.putExtra("outputY", 640);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void insertDatabase() {
        //userEmail = signup.email;
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String sex = editTextSex.getText().toString().trim();
        String feet = editTextFeet.getText().toString().trim();
        String inch = editTextInch.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();

        if (name.length() == 0 || age.length() == 0 || sex.length() == 0 || feet.length() == 0 ||
                inch.length() == 0 || weight.length() == 0) {
            Toast.makeText(this, "Oops, some fields are missing. Please fill up all the fields", Toast.LENGTH_SHORT).show();
            return;
        }



        byte[] photo;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap == null) photo = null;
        else photo = DbBitmapUtility.getBytes(bitmap);

        float height = (float) (((Integer.parseInt(feet) * 12) + Float.parseFloat(inch)) * 2.54);

        int bmi = (int) (Float.parseFloat(weight) / ((height * height) / 10000));

        boolean isInserted = mydb.insertData(userEmail, name, Integer.parseInt(age), sex, height, Float.parseFloat(weight), photo, bmi);

        if (isInserted == true) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {

        if (view == imageView) {
            final CharSequence ImageOption[] = new CharSequence[]{"Take Picture", "Choose from Gallery", "Delete"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Image");
            builder.setItems(ImageOption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    boolean result = Utility.checkPermission(userinfo.this);
                    if (ImageOption[which].equals("Take Picture")) {
                        userChoosenTask = "Take Picture";
                        if (result)
                            cameraIntent();
                    } else if (ImageOption[which].equals("Choose from Gallery")) {
                        userChoosenTask = "Choose from Gallery";
                        if (result)
                            galleryIntent();
                    } else if (ImageOption[which].equals("Delete")) {
                        imageView.setImageResource(R.drawable.profile_update);
                    }
                }
            });
            builder.show();
        } else if (view == editTextSex) {
            final CharSequence gender[] = new CharSequence[]{"Male", "Female", "Others"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Gender");
            builder.setItems(gender, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    editTextSex.setText(gender[which]);
                }
            });
            builder.show();
        } else if (view == editTextFeet) {
            final CharSequence feet[] = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Feet");
            builder.setItems(feet, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    editTextFeet.setText(feet[which]);
                }
            });
            builder.show();
        } else if (view == editTextInch) {
            final CharSequence inch[] = new CharSequence[]{"0", ".5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Inches");
            builder.setItems(inch, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    editTextInch.setText(inch[which]);
                }
            });
            builder.show();
        } else if (view == buttonDoneInfo) {
            if (Float.parseFloat(editTextWeight.getText().toString().trim()) < 10 || Float.parseFloat(editTextWeight.getText().toString().trim()) > 500) {
                Toast.makeText(this, "Invalid Weight. Range is between 10kg to 500kg", Toast.LENGTH_LONG).show();
                return;
            }
            insertDatabase();
        }
    }

    @Override
    public void onBackPressed() {
        firebaseAuth.signOut();
        finish();
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //User clicked home, do whatever you want
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, login.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
