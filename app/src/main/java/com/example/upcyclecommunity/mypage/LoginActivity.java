package com.example.upcyclecommunity.mypage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;

public class LoginActivity extends AppCompatActivity {

    private EditText email_et;
    private EditText password_et;
    private Button signUp_btn;
    private Button signIn_btn;
    private TextView helpPassword_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_et = findViewById(R.id.activity_login_email_editText);
        password_et = findViewById(R.id.activity_login_password_editText);
        signUp_btn = findViewById(R.id.activity_login_signUp_button);
        signIn_btn = findViewById(R.id.activity_login_signIn_button);
        helpPassword_tv = findViewById(R.id.activity_login_help_password_textView);

        signUp_btn.setOnClickListener(view -> {

        });
        signIn_btn.setOnClickListener(view -> {

            finish();
        });

        helpPassword_tv.setOnClickListener(view -> {

        });
    }
}
