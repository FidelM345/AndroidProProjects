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

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText reg_email,reg_password,reg_confirm;
    Button reg_register;
    TextView login_btn;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        reg_email=findViewById(R.id.reg_email);
        reg_password=findViewById(R.id.reg_password);
        reg_confirm=findViewById(R.id.reg_confirm);
        reg_register=findViewById(R.id.reg_btn_reg);
        login_btn =findViewById(R.id.reg_btn_exist);
        progressBar =findViewById(R.id.reg_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        reg_register.setOnClickListener(this);
        login_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.reg_btn_reg:

                methodRegister();

                break;

            case R.id.reg_btn_exist:

                methodLogin();
                break;

            default:
                return;
        }
    }

    private void methodLogin() {
        Intent intent=new Intent(Register.this,Login.class);
        startActivity(intent);
    }

    private void methodRegister() {

        String email=reg_email.getText().toString().trim();
        String pass=reg_password.getText().toString().trim();
        String pass1=reg_confirm.getText().toString().trim();


        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass) &&!TextUtils.isEmpty(pass1)){

            if (pass.equals(pass1)){
                //checks whether the two entered password are the same


                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            gotoSetupActivity();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            String error=task.getException().getMessage();
                            Toast.makeText(Register.this, "Error: "+error, Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }else {
                Toast.makeText(this, "Please enter a Valid Password", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Please Ensure all the three text fields have been filled before " +
                    "pressing register button ", Toast.LENGTH_LONG).show();

        }
    }

    private void gotoSetupActivity() {
        Intent intent=new Intent(Register.this,Profile.class);
        startActivity(intent);
    }
}
