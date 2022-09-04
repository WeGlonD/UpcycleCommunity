package com.example.upcyclecommunity.mypage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Acts;
import com.example.upcyclecommunity.database.Database;
import com.example.upcyclecommunity.database.User;
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

        Database db = new Database();
        User user = new User();

        signUp_btn.setOnClickListener(view -> {
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            String password_check = password_check_et.getText().toString();

            if(password.equals(password_check)){
                user.create(email, password, new Acts() {
                    @Override
                    public void ifSuccess(Object task) {
                        User.Data data = new User.Data();

                    }

                    @Override
                    public void ifFail(Object task) {

                    }
                });
            }
            else{
                Toast.makeText(this, getString(R.string.activity_signup_password_is_not_same), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
