package com.gocorona.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gocorona.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Button btn_scan, btn_contine;

    LinearLayout ll_scan;
    TextView tv_vaccine, tv_dose;
    private IntentIntegrator qrScan;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        context = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Scan QR code");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        uID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("Volunteers").document(uID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.getString("vaccine").equals("No Update")){
                    startActivity(new Intent(getApplicationContext(), VolunteerDashBoardActivity.class));
                    finish();
                }
            }
        });

        findviews();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //scan have an error
            } else {
                //scan is successful
                Log.e("scan", result.getContents());
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());

                    Log.e("vaccine", jsonObject.getString("vaccine"));
                    Log.e("dose", jsonObject.getString("dose"));

                    ll_scan.setVisibility(View.VISIBLE);
                    tv_vaccine.setText(jsonObject.getString("vaccine"));
                    tv_dose.setText(jsonObject.getString("dose"));

                    Map<String, Object> volunteer = new HashMap<>();
                    volunteer.put("vaccine", jsonObject.getString("vaccine"));
                    volunteer.put("dose", jsonObject.getString("dose"));
                    volunteer.put("infected", "No Update");

                    fstore.collection("Volunteers")
                            .document(uID).set(volunteer, SetOptions.merge());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void findviews() {
        btn_scan = findViewById(R.id.btn_scan);
        btn_contine = findViewById(R.id.btn_continue);
        tv_vaccine = findViewById(R.id.tv_vaccine_group);
        tv_dose = findViewById(R.id.tv_dose);
        ll_scan = findViewById(R.id.ll_scan);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_scan) {
            qrScan = new IntentIntegrator(this);
            qrScan.initiateScan();
        } else if (v == btn_contine) {
            startActivity(new Intent(getApplicationContext(), VolunteerDashBoardActivity.class));
            finish();
        }
    }
}