package com.gocorona.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gocorona.AdminActivity;
import com.gocorona.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    EditText ed_email, ed_password;
    Button btn_signin, btn_admin;
    TextView tv_signup, tv_error;

    String email, password;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setTitle("Volunteer SignIn");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        context = this;
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            Log.e("user", "Already Signed In");
            startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            finish();
        }
        findviews();

    }

    public void findviews() {
        ed_email = findViewById(R.id.ed_email);
        ed_password = findViewById(R.id.ed_psw);

        btn_signin = findViewById(R.id.btn_signin);
        tv_signup = findViewById(R.id.tv_signup);
        btn_admin = findViewById(R.id.btn_admin);
        tv_error = findViewById(R.id.tv_error);
    }

    public void getValues() {
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
    }

    public boolean checkValues() {
        if (TextUtils.isEmpty(email)) {
            ed_email.setError("Invalid Email");
            tv_error.setText("Error: " + "Invalid Email");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
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
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Signed in successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                            finish();
                        } else {
                            hidekeyboard();
                            tv_error.setText("Error: " + task.getException().getMessage());
                            tv_error.setVisibility(View.VISIBLE);
                            //  Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                tv_error.setText("Error: Invalid Credentials");
                tv_error.setVisibility(View.VISIBLE);
                //  Toast.makeText(context, "Invalid Data", Toast.LENGTH_LONG).show();
            }
        } else if (v == tv_signup) {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        } else if (v == btn_admin) {
            startActivity(new Intent(getApplicationContext(), AdminActivity.class));
        }
    }

    public void hidekeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}