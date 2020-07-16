package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email,pass;
    Button btn_login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String txt_email = email.getText().toString();
//                        btn_login.setEnabled(false);
                        String txt_pass = pass.getText().toString();
                        if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_pass)){
                            Toast.makeText(LoginActivity.this,"All fields are required",Toast.LENGTH_LONG).show();
                        }
                        else{
                            auth.signInWithEmailAndPassword(txt_email,txt_pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful())
                                            {
                                                Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(LoginActivity.this,"Authentication Failed!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
        );
    }
}
