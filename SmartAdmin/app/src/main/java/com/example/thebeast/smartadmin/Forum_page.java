package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Forum_page extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button post_button;
    EditText post_description, post_title;
    ImageView post_image,back_btn;
    Uri mainImageUri = null;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    StorageReference firebaseStorage;
    FirebaseAuth mAuth;
    Bitmap compressedImageBitmap;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    MypostFragment mypostFragment;
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_page);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.forum_toolbar);
        setSupportActionBar(toolbar);


        bottomNavigationView =findViewById(R.id.bottom_navigation);


        homeFragment=new HomeFragment();
        mypostFragment=new MypostFragment();
        replaceFragment(homeFragment);//loading the home fragment in main activity

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //the listener handles the bottom navigation button clicks
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.all_posts:
                        replaceFragment(homeFragment);

                        return true;

                    case R.id.my_post:
                        replaceFragment(mypostFragment);
                        return true;


                    default:
                        return false;
                }


            }
        });


        floatingActionButton=findViewById(R.id.add_post);
        floatingActionButton.setOnClickListener(this);

        back_btn=findViewById(R.id.id_backmain);


        back_btn.setOnClickListener(this);



    }

    private  void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_post:
                Intent intent=new Intent(Forum_page.this,Forum_post.class);
                startActivity(intent);
                break;

            case R.id.id_backmain:
                Intent intent1=new Intent(Forum_page.this,MainActivity.class);
                startActivity(intent1);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(Forum_page.this,MainActivity.class);
        startActivity(intent);
    }
}

