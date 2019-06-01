package com.example.thebeast.smartadmin;
import android.app.DatePickerDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;




class Profile extends AppCompatActivity implements View.OnClickListener{

    StorageReference mstorageReference;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;
    EditText Fname, Lname,phone_no;




    Button Okbutton;

    ImageView circleImageView;

    Uri mainImageUri=null;
    boolean isChaanged = false;
    ProgressBar progressBar;
    String user_id;
    Bitmap compressedImageBitmap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase critical objects
        mstorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();

        Fname = findViewById(R.id.profile_fname);

        Lname = findViewById(R.id.profile_lname);

        phone_no = findViewById(R.id.profile_phoneno);




        Okbutton = findViewById(R.id.profile_btn_submit);
        circleImageView = findViewById(R.id.profile_circular);
        progressBar= findViewById(R.id.profile_progressBar);
        progressBar.setVisibility(View.INVISIBLE);




        circleImageView.setOnClickListener(this);



        Okbutton.setOnClickListener(this);





    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_circular:
                //allow user to select profile pic from phone memory
                profilePicPicker();

                break;


            case R.id.profile_btn_submit:

                if(isChaanged){ //becomes true when new image is selected
                    final String first_name=Fname.getText().toString().trim();
                    final String user_phoneno=phone_no.getText().toString().trim();
                    final String last_name=Lname.getText().toString().trim();


                    user_id=mAuth.getCurrentUser().getUid();



                    if (!TextUtils.isEmpty(first_name)&&!TextUtils.isEmpty(last_name)&&!TextUtils.isEmpty(user_phoneno)&& mainImageUri !=null){

                        progressBar.setVisibility(View.VISIBLE);

                        StorageReference image_path=mstorageReference.child("Profile_image").child(user_id+".jpg");

                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){
                                    final String main_profile_uri=task.getResult().getDownloadUrl().toString();
                                    storeProfileThumburi(first_name,last_name,user_phoneno,main_profile_uri);

                                }else {
                                    String exception=task.getException().getMessage();
                                    Toast.makeText(Profile.this, "Image Upload Error is: "+exception, Toast.LENGTH_LONG).show();
                                }

                            }
                        });


                    }else {
                        Toast.makeText(Profile.this, "Ensure all fields have been filled", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(Profile.this, "Please select image first by clicking image icon above to setup your account", Toast.LENGTH_LONG).show();


                }
                break;

        }
    }



    private void storeProfileThumburi(final String fname, final String lname, final String phoneno, final String main_profile_uri) {
        File actualImageFile=new File(mainImageUri.getPath());


        try {
            compressedImageBitmap = new Compressor(Profile.this)
                    //compressing image of high quality to a thumbnail bitmap for faster loading
                    .setMaxWidth(60)
                    .setMaxHeight(60)
                    .setQuality(5)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .compressToBitmap(actualImageFile);


        } catch (IOException e) {
            e.printStackTrace();
        }

        //compressing image of high quality to a thumbnail bitmap for faster loading
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask thumbfilepath=mstorageReference.child("Forum_images/forum_thumbs").child(user_id+".jpg").putBytes(data);

        thumbfilepath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    //after the image thumbnail has been uploaded successfully to the cloud storage
                    //it publishes the contents to the firestore cloud storage
                    storeFirestore(task,fname,lname,phoneno,main_profile_uri);//used for storing image and the user name in firebase
                }
                else{
                    String exception=task.getException().getMessage();
                    Toast.makeText(Profile.this, "Thumb Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String first_name, String last_name, String phoneno, String main_profile_uri) {

        Uri download_uri;
        download_uri=task.getResult().getDownloadUrl();
        progressBar.setVisibility(View.INVISIBLE);
        Map<String,Object> user_details=new HashMap<>();
        user_details.put("fname",first_name);
        user_details.put("lname",last_name);
        user_details.put("phone_no",phoneno);
        user_details.put("email",mAuth.getCurrentUser().getEmail());
        user_details.put("imageuri",main_profile_uri);
        user_details.put("thumburi",download_uri.toString());
        user_details.put("log_id",null);




        mfirestore.collection("Admin_table").document(user_id).set(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    gotMainActivity();

                }else{
                    String exception=task.getException().getMessage();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Profile.this, "Text Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    private void gotMainActivity() {
        Toast.makeText(this, "Your Account has been setup successfully", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Profile.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void profilePicPicker() {
        // the first if statement checks whether the user is running android Mash mellow and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(Profile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if the permission has been denied allow user to request for the permission

                ActivityCompat.requestPermissions(Profile.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                        .start(this);

            }
        } else {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4, 3)
                    .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                    .start(this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                circleImageView.setImageURI(mainImageUri);
                isChaanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }
    }









    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){


            case 1:

                if (grantResults.length>0){

                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){



                    }else if(grantResults[0]==PackageManager.PERMISSION_DENIED){

                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }
        }

    }






}

