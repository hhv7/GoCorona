package com.gocorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    EditText ed_email, ed_password;
    Button btn_signin;

    String email, password;
    TextView tv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vaccine Developer SignIN");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        context = this;
        findviews();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void findviews() {
        ed_email = findViewById(R.id.ed_a_email);
        ed_password = findViewById(R.id.ed_a_psw);

        btn_signin = findViewById(R.id.btn_admin_signin);
        tv_error = findViewById(R.id.tv_error);
    }

    public void getValues() {
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
    }

    public boolean checkValues() {
        if (TextUtils.isEmpty(email)||!email.equals("admin")) {
            ed_email.setError("Invalid Email");
            tv_error.setText("Error: " + "Invalid Email");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(password)||!password.equals("admin")) {
            ed_password.setError("Invalid Password");
            tv_error.setText("Error: " + "Invalid Password");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }

        tv_error.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_signin) {
            getValues();
            if (checkValues()) {
                startActivity(new Intent(getApplicationContext(), VaccineDeveloperDashboardActivity.class));
                finish();
            }else{
                tv_error.setText("Error: " + "Invalid Credentials");
                tv_error.setVisibility(View.VISIBLE);
            }
        }
    }
}