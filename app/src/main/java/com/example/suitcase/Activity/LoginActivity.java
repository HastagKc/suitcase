package com.example.suitcase.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.suitcase.Database.Database;
import com.example.suitcase.R;

public class LoginActivity extends AppCompatActivity {
    private EditText Eemail, Epassword;
    private Button Blogin;
    private TextView Bsignup;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Database db = new Database(this);
        Eemail = findViewById(R.id.inputEmail);
        Epassword = findViewById(R.id.inputPassword);
        Blogin = findViewById(R.id.btn);
        Bsignup = findViewById(R.id.losignup);

        //change notification color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.loginRegisterColor));
        }

        Blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Converted edittext to string
                email = Eemail.getText().toString();
                password = Epassword.getText().toString();


                //if condition for null value in edittext
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter username and Password", Toast.LENGTH_SHORT).show();
                }

                //Email pattern and password length checked
                else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    //created boolean variable for pop up message when login success or not
                    boolean i = db.checkemailandpassword(email, password);
                    if (i == true) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent ilogin = new Intent(LoginActivity.this, DashboardActivity.class);
                        // ilogin.putExtra("Email", email);
                        getemail();
                        startActivity(ilogin);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                    }
                }


                //if email pattern is wrong show this message
                else {
                    Toast.makeText(LoginActivity.this, "Please re-enter your email ", Toast.LENGTH_LONG).show();
                    Eemail.setError(" Valid email is required");
                }
            }
        });

        Bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    public void getemail() {
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.putString("email", email);
        Ed.apply();
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

}