package com.gocorona.user;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gocorona.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    EditText ed_email, ed_password, ed_name, ed_age, ed_address, ed_info;
    RadioGroup rg_gender;
    Button btn_signup;
    TextView tv_signin,tv_error;

    String email, password, name, gender, address, info, age;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String uID;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Volunteer SignUp");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        context = this;
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null) {
            Log.e("user", "Already Signed In");
            startActivity(new Intent(getApplicationContext(), VolunteerDashBoardActivity.class));
            finish();
        }
        findviews();
    }

    public void findviews() {
        ed_email = findViewById(R.id.ed_s_email);
        ed_password = findViewById(R.id.ed_s_psw);
        ed_name = findViewById(R.id.ed_s_name);
        ed_age = findViewById(R.id.ed_s_age);
        ed_address = findViewById(R.id.ed_s_add);
        ed_info = findViewById(R.id.ed_s_info);

        rg_gender = findViewById(R.id.rg_gender);
        btn_signup = findViewById(R.id.btn_signup);
        tv_signin = findViewById(R.id.tv_signin);
        tv_error = findViewById(R.id.tv_error);
    }

    public void getValues() {
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        name = ed_name.getText().toString().trim();
        age = ed_age.getText().toString().trim();
        address = ed_address.getText().toString().trim();
        info = ed_info.getText().toString().trim();

        int radioButtonID = rg_gender.getCheckedRadioButtonId();
        if (radioButtonID == R.id.rd_male) {
            gender = "Male";
        } else if (radioButtonID == R.id.rd_female) {
            gender = "female";
        } else if (radioButtonID == R.id.rd_other) {
            gender = "Other";
        } else {
            gender = "";
        }
    }

    public boolean checkValues() {
        getValues();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (TextUtils.isEmpty(email) || !email.matches(emailPattern)) {
            ed_email.setError("Invalid Email");
            tv_error.setText("Error: " + "Invalid Email");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            ed_password.setError("Size of Password should 6");
            tv_error.setText("Error: " + "Size of Password should 6");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            ed_name.setError("Invalid Name");
            tv_error.setText("Error: " + "Invalid Name");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            Snackbar.make(findViewById(android.R.id.content), "Select Gender", Snackbar.LENGTH_LONG).show();
            tv_error.setText("Error: " + "Select Gender");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (!TextUtils.isEmpty(age)) {
            int a = Integer.parseInt(age);
            if (a < 18) {
                ed_age.setError("Age must be above 18");
                tv_error.setText("Error: " + "Age must be above 18");
                tv_error.setVisibility(View.VISIBLE);
                return false;
            } else if (a > 120) {
                ed_age.setError("Invaldi Age");
                tv_error.setText("Error: " + "Invalid Age");
                tv_error.setVisibility(View.VISIBLE);
                return false;
            }
        } else {
            ed_age.setError("Invalid Age");
            tv_error.setText("Error: " + "Invalid Age");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            ed_address.setError("Invalid Address");
            tv_error.setText("Error: " + "Invalid Address");
            tv_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(info)) {
            info = "Not Provided";
        }

        tv_error.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v == btn_signup) {

            if (checkValues()) {

                Log.e("values", email + "-" + password + "-" + name + "-" + age + "-" + gender + "-" + address + "-" + info);

                dialog = new ProgressDialog(context);
                dialog.setMessage("Please Wait Signing You Up...");
                dialog.show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            uID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Volunteers").document(uID);

                            Map<String, Object> volunteer = new HashMap<>();
                            volunteer.put("email", email);
                            volunteer.put("name", name);
                            volunteer.put("gender", gender);
                            volunteer.put("age", age);
                            volunteer.put("address", address);
                            volunteer.put("health", info);
                            volunteer.put("vaccine", "No Update");
                            volunteer.put("dose", "No Update");
                            volunteer.put("infected", "No Update");

                            documentReference.set(volunteer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("signup", "success user:" + uID);
                                    Toast.makeText(context, "SignedUp Successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                                    finish();
                                }
                            });
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } else {
                            hidekeyboard();
                            Log.e("error", task.getException().getMessage());
                            tv_error.setText("Error: " + task.getException().getMessage());
                            tv_error.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
               // Toast.makeText(context, "Invalid Data", Toast.LENGTH_LONG).show();
                tv_error.setText("Error: Invalid Data");
                tv_error.setVisibility(View.VISIBLE);
            }
        } else if (v == tv_signin) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
    }
    public void hidekeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}