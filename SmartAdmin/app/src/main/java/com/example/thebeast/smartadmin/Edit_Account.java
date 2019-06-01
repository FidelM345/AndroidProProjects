package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class Edit_Account extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    EditText Fname, Lname,phone_no;
    ImageView circleImageView;
    Button Okbutton;
    boolean imageSelected = false;
    String user_id;
    Uri mainImageUri=null;
    ProgressBar progressBar;
    StorageReference mstorageReference;
    Bitmap compressedImageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__account);

        //initializing firebase critical objects
        mstorageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        Fname = findViewById(R.id.profile_fname);
        Lname = findViewById(R.id.profile_lname);

        phone_no = findViewById(R.id.profile_phoneno);
        circleImageView = findViewById(R.id.profile_circular);
        Okbutton = findViewById(R.id.profile_btn_submit);

        progressBar= findViewById(R.id.profile_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        user_id=firebaseAuth.getCurrentUser().getUid();


        Okbutton.setOnClickListener(this);
        circleImageView.setOnClickListener(this);


        load_profile();

    }




    private void load_profile() {

        firestore.collection("Admin_table").document(firebaseAuth.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String fname = task.getResult().getString("fname");
                            String lname = task.getResult().getString("lname");

                            String image = task.getResult().getString("imageuri");
                            String PhoneNO = task.getResult().getString("phone_no");
                            String Thumb = task.getResult().getString("thumburi");


                            Fname.setText(fname);
                            Lname.setText(lname);

                            phone_no.setText(PhoneNO);



                            RequestOptions placeHolder = new RequestOptions();
                            placeHolder.placeholder(R.drawable.profile_placeholder);
                            Glide.with(Edit_Account.this).applyDefaultRequestOptions(placeHolder).load(image).thumbnail(
                                    //loads the thumbnail if the image has not loaded first
                                    Glide.with(Edit_Account.this).load(Thumb)
                            ).into(circleImageView);


                        }

                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.profile_btn_submit:

                 if(imageSelected){ //becomes true when new image is selected

                  profile_Imagecontent();

                     }
                     else{

                     profile_Content();
                 }


                break;

            case R.id.profile_circular:
                //allow user to select profile pic from phone memory
                profilePicPicker();

                break;

                default:
                    break;


        }




    }

    private void profile_Content() {


        final String first_name=Fname.getText().toString().trim();
        final String user_phoneno=phone_no.getText().toString().trim();
        final String last_name=Lname.getText().toString().trim();



        if (!TextUtils.isEmpty(first_name)&&!TextUtils.isEmpty(last_name)
                &&!TextUtils.isEmpty(user_phoneno)){
            progressBar.setVisibility(View.VISIBLE);
            Map<String,Object> user_details=new HashMap<>();
            user_details.put("fname",first_name);
            user_details.put("lname",last_name);
            user_details.put("phone_no",user_phoneno);


            firestore.collection("Admin_table").document(user_id).update(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        gotMainActivity();

                    }else{
                        String exception=task.getException().getMessage();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Edit_Account.this, "Text Error is: "+exception, Toast.LENGTH_LONG).show();
                    }

                }
            });


        }
        else {
            Toast.makeText(getApplicationContext(), "Ensure all fields have been filled", Toast.LENGTH_LONG).show();
        }


    }

    private void profile_Imagecontent() {
    //updates both the image and the text content

        final String first_name=Fname.getText().toString().trim();
        final String user_phoneno=phone_no.getText().toString().trim();
        final String last_name=Lname.getText().toString().trim();




        if (!TextUtils.isEmpty(first_name)&&!TextUtils.isEmpty(last_name)
                &&!TextUtils.isEmpty(user_phoneno)&& mainImageUri !=null){

            progressBar.setVisibility(View.VISIBLE);

            StorageReference image_path=mstorageReference.child("Profile_image").child(user_id+".jpg");

            image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){
                        final String main_profile_uri=task.getResult().getDownloadUrl().toString();
                        storeProfileThumburi(first_name,last_name,user_phoneno,main_profile_uri);
                    }

                    else {
                        String exception=task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Image Upload Error is: "+exception, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Ensure all fields have been filled", Toast.LENGTH_LONG).show();
        }


    }



    private void storeProfileThumburi(final String fname, final String lname, final String phoneno, final String main_profile_uri) {
        File actualImageFile=new File(mainImageUri.getPath());


        try {
            compressedImageBitmap = new Compressor(Edit_Account.this)
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
                    Toast.makeText(getApplicationContext(), "Thumb Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String first_name,
                                String last_name, String phoneno, String main_profile_uri) {

        Uri download_uri;
        download_uri=task.getResult().getDownloadUrl();
        progressBar.setVisibility(View.INVISIBLE);
        Map<String,Object> user_details=new HashMap<>();
        user_details.put("fname",first_name);
        user_details.put("lname",last_name);
        user_details.put("phone_no",phoneno);
        user_details.put("imageuri",main_profile_uri);
        user_details.put("thumburi",download_uri.toString());

        firestore.collection("Admin_table").document(user_id).update(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    gotMainActivity();

                }else{
                    String exception=task.getException().getMessage();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Edit_Account.this, "Text Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });



    }





    private void gotMainActivity() {
        Toast.makeText(this, "Your Account has been setup successfully", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Edit_Account.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void profilePicPicker() {
        // the first if statement checks whether the user is running android Mash mellow and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(Edit_Account.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if the permission has been denied allow user to request for the permission

                ActivityCompat.requestPermissions(Edit_Account.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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
                imageSelected = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }
    }



}
