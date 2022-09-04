package com.example.upcyclecommunity.mypage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;

    private EditText email_et;
    private EditText password_et;
    private EditText password_check_et;
    private Button signUp_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth == null)
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();

        email_et = findViewById(R.id.activity_signup_email_editText);
        password_et = findViewById(R.id.activity_signup_password_editText);
        password_check_et = findViewById(R.id.activity_signup_password_check_editText);
        signUp_btn = findViewById(R.id.activity_signup_signUp_button);

        signUp_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            String password_check = password_check_et.getText().toString();
            if(password.equals(password_check)){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, getString(R.string.activity_signup_create_account_success), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(this, getString(R.string.activity_signup_create_account_fail), Toast.LENGTH_SHORT).show();
                    }
                        });
            }
            else{
                Toast.makeText(this, getString(R.string.activity_signup_password_is_not_same), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
