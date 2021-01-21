package com.gocorona.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gocorona.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class VolunteerDashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_pos;
    TextView tv_logout, tv_result, tv_name;
    LinearLayout ll_report;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String uID;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dash_board);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Volunteer Dashboard");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        findviews();
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        uID = fAuth.getCurrentUser().getUid();

        context = this;

        DocumentReference documentReference = fstore.collection("Volunteers").document(uID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tv_name.setText("Hello, " + value.getString("name"));
                if (value.getString("infected").equals("No Update")) {
                    tv_result.setText("Not Uploaded");
                    ll_report.setVisibility(View.VISIBLE);
                } else {
                    tv_result.setText("You are tested positive");
                    ll_report.setVisibility(View.GONE);
                }
            }
        });
    }

    public void findviews() {
        btn_pos = findViewById(R.id.btn_pos);
        tv_name = findViewById(R.id.tv_v_name);
        tv_logout = findViewById(R.id.tv_logout);
        tv_result = findViewById(R.id.tv_result);
        ll_report = findViewById(R.id.ll_report);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        } else if (v == btn_pos) {
            Map<String, Object> volunteer = new HashMap<>();
            volunteer.put("infected", "Positive");
            fstore.collection("Volunteers")
                    .document(uID).set(volunteer, SetOptions.merge());
            ll_report.setVisibility(View.GONE);
        }
    }
}