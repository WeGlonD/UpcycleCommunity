package com.example.upcyclecommunity.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;

    private EditText email_et;
    private EditText password_et;
    private Button signUp_btn;
    private Button signIn_btn;
    private TextView helpPassword_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth == null)
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();


        email_et = findViewById(R.id.activity_login_email_editText);
        password_et = findViewById(R.id.activity_login_password_editText);
        signUp_btn = findViewById(R.id.activity_login_signUp_button);
        signIn_btn = findViewById(R.id.activity_login_signIn_button);
        helpPassword_tv = findViewById(R.id.activity_login_help_password_textView);

        signUp_btn.setOnClickListener(view -> {
            Intent it = new Intent(this, SignUpActivity.class);
            startActivity(it);
        });
        signIn_btn.setOnClickListener(view -> {
            if(mAuth != null){
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       Toast.makeText(this, getString(R.string.activity_login_login_success), Toast.LENGTH_LONG);
                       finish();
                   }
                   else{
                       Toast.makeText(this, getString(R.string.activity_login_login_fail), Toast.LENGTH_SHORT);
                   }
                });
            }
            else{
                Toast.makeText(this, getString(R.string.activity_login_login_fail), Toast.LENGTH_SHORT);
            }
        });

        helpPassword_tv.setOnClickListener(view -> {

        });
    }
}
