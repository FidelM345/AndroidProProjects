package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText login_email, login_password;
    Button login,register;
    TextView forget_password;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login= findViewById(R.id.btn_login);
        register= findViewById(R.id.btn_register);
        progressBar= findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        login.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_login:

                methodLogin();

                break;

            case R.id.btn_register:

                methodRegister();
                    break;

                    default:
                        return;
        }

    }

    private void methodRegister() {
        Intent intent=new Intent(Login.this,Register.class);
        startActivity(intent);

    }

    private void methodLogin() {

        String email = login_email.getText().toString().trim();
        String pass = login_password.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        progressBar.setVisibility(View.INVISIBLE);
                        gotoMain();

                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, "Login error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }


                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void gotoMain() {
        Intent intent=new Intent(Login.this,Log_id.class);
        startActivity(intent);
        finish();

    }
}
