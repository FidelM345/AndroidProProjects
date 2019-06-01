package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class Web_Post extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    Button post_button;
    EditText post_url,post_title;

    ImageView post_image;

    Uri mainImageUri = null;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    StorageReference firebaseStorage;
    FirebaseAuth mAuth;
    Bitmap compressedImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web__post);


        firebaseStorage = FirebaseStorage.getInstance().getReference();

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.forum_toolbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("The Beast");

        post_button = findViewById(R.id.post_web);

        post_title = findViewById(R.id.web_title);
        post_url = findViewById(R.id.web_url);

        post_image = findViewById(R.id.web_image);

        progressBar = findViewById(R.id.web_progress);

        progressBar.setVisibility(View.INVISIBLE);

        post_image.setOnClickListener(this);
        post_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_image:
                //do the following when image is clicked
                aurtherImagePicker(); //method used for picking images easily
                break;
            case R.id.post_web:
                forumPublish();
                break;

        }

    }

    private void aurtherImagePicker() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(Web_Post.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                // if the permission has been denied allow user to request for the permission
                ActivityCompat.requestPermissions(Web_Post.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4,3)
                        //.setMinCropResultSize(512,512)
                        .start(this);
            }
        }else{
            //do the following if the android OS is less than mashmellow or android 6.0
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
    }

    private void forumPublish() {

        final String title=post_title.getText().toString().trim();
        final String website_url =post_url.getText().toString().trim();


        final String current_user_id=mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(website_url)&&mainImageUri !=null) {

            progressBar.setVisibility(View.VISIBLE);

            final String randomBlogName= UUID.randomUUID().toString();//generates random strings

            final StorageReference storageReference=firebaseStorage.child("FirstAidWeb_images/firstaidweb_photo").child(randomBlogName+".jpg");
            storageReference.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                    if(task.isSuccessful()){

                        final String download_uri=task.getResult().getDownloadUrl().toString();

                        File actualImageFile=new File(mainImageUri.getPath());


                        try {
                            compressedImageBitmap = new Compressor(Web_Post.this)
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

                        UploadTask thumbfilepath=firebaseStorage.child("FirstAidWeb_images/firstaidweb_thumbs").child(randomBlogName+".jpg").putBytes(data);

                        thumbfilepath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                if(task.isSuccessful()){


                                    //after the image thumbnail has been uploaded successfully to the cloud storage
                                    //it publishes the contents to the firestore cloud storage
                                    forumDatabasepublish(task,title,website_url,current_user_id,download_uri);



                                }
                                else{
                                    String exception=task.getException().getMessage();

                                    Toast.makeText(Web_Post.this, "Thumb Error is: "+exception, Toast.LENGTH_LONG).show();


                                }

                            }
                        });



                    }
                    else{
                        String exception=task.getException().getMessage();

                        Toast.makeText(Web_Post.this, "Image Error is: "+exception, Toast.LENGTH_LONG).show();


                    }


                }
            });

        }else{

            Toast.makeText(this, "Please ensure you have selected an Image, and that all fields are not empty", Toast.LENGTH_LONG).show();

        }


    }

    private long timeStamp() {
        Long timestamp=System.currentTimeMillis()/1000;
        return timestamp;
    }


    private void forumDatabasepublish(@NonNull Task<UploadTask.TaskSnapshot> task, String title, String weburl, String user_id, String download_uri) {

        String thumbnail_uri=task.getResult().getDownloadUrl().toString();
        //if thumbnail has been uploaded successfully do the following
        Map<String,Object> contents=new HashMap<>();
        contents.put("title",title);
        contents.put("weburl",weburl);
        contents.put("user_id",user_id);
        contents.put("imageUri",download_uri);
        contents.put("thumbUri",thumbnail_uri);
        contents.put("timestamp",timeStamp());

        firestore.collection("FirstAidWeb_Posts").add(contents).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Web_Post.this, "Your content has been posted successfully ", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(Web_Post.this,Web_View.class);
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.INVISIBLE);


                }else{
                    String exception=task.getException().getMessage();

                    Toast.makeText(Web_Post.this, "Fire store Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage. getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri= result.getUri();
                post_image.setImageURI(mainImageUri);
            }  else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "Cropper error"+error, Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                //we use an array of index number 0 because only one permission ise being requested
                //this method is called so that one the permission is granted for the first time then the code on the else block will execute immediately
                //helps yo not to preee the button twice inorder to make the phone call
                aurtherImagePicker();

            }else {

                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }


}
