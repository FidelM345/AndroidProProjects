package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Log_id extends AppCompatActivity {

    EditText login_email, login_password;
    Button login,register;
    TextView forget_password;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_id);

        login_password = findViewById(R.id.login_password);
        login= findViewById(R.id.btn_login);

        progressBar= findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(login_password.getText())) {


                    mfirestore.collection("Admin_table").document(mAuth.getCurrentUser().getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.getResult().exists()) {


                                        String Log_id=task.getResult().getString("log_id");

                                        if(Log_id.equals(login_password.getText().toString())){


                                            gotoMain();



                                        }else {

                                            Toast.makeText(Log_id.this, "Access Denied the Admin id entered is invalid", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);

                                        }


                                    }


                                }
                            });




                }else{


                    Toast.makeText(Log_id.this, "Please enter Admin CODE in the field to proceed", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }







            }
        });
    }


    private void gotoMain() {
        Intent intent=new Intent(Log_id.this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
