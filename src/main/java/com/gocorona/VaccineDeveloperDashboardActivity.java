package com.gocorona;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gocorona.model.Volunteer;
import com.gocorona.user.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class VaccineDeveloperDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;

    Context context;

    TextView tv_total_volunteers, tv_total_pos, tv_vcc_rate, tv_pos_vcc, tv_pos_unvcc, tv_logout, tv_single_rate, tv_half_rate;
    ArrayList<Volunteer> volunteerList = new ArrayList<>();

    int MIN_THRESHOLD = 10;

    PieChartView chart1, chart2;
    ArrayList chart1_data, chart2_data;

    DecimalFormat f = new DecimalFormat("##.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_developer_dashboard);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vaccine Developer Dashboard");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getColor(R.color.red2)));

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        context = this;
        findviews();
        setChart();
        getDataFromAPI();

    }

    public void findviews() {
        tv_logout = findViewById(R.id.tv_logout);
        tv_total_volunteers = findViewById(R.id.tv_total_voluntree);
        tv_total_pos = findViewById(R.id.tv_total_pos);
        tv_vcc_rate = findViewById(R.id.tv_vcc_rate);
        tv_pos_vcc = findViewById(R.id.tv_pos_vcc);
        tv_pos_unvcc = findViewById(R.id.tv_pos_unvcc);

        tv_single_rate = findViewById(R.id.tv_single_rate);
        tv_half_rate = findViewById(R.id.tv_half_rate);
        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_logout) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
    }

    public void updateUI() {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        double total_voluntree = 0, total_pos = 0;
        double vcc_rate = 0, total_pos_vcc = 0, total_pos_unvcc = 0, single_vcc_rate = 0, half_vcc_rate = 0;


        double single_vcc_dose = 0, half_vcc_dose = 0, single_unvcc_dose = 0, half_unvcc_dose = 0;

        total_voluntree = volunteerList.size();

        for (int i = 0; i < volunteerList.size(); i++) {
            if (volunteerList.get(i).getInfected().equals("Positive")) {
                total_pos = total_pos + 1;
                if (volunteerList.get(i).getVaccine().equals("A")) {
                    total_pos_unvcc = total_pos_unvcc + 1;
                    if (volunteerList.get(i).getDose().equals("1")) {
                        single_unvcc_dose = single_unvcc_dose + 1;
                    } else {
                        half_unvcc_dose = half_unvcc_dose + 1;
                    }
                } else {
                    total_pos_vcc = total_pos_vcc + 1;
                    if (volunteerList.get(i).getDose().equals("1")) {
                        single_vcc_dose = single_vcc_dose + 1;
                    } else {
                        half_vcc_dose = half_vcc_dose + 1;
                    }
                }
            }
        }

        Log.e("single_unvcc_dose", single_unvcc_dose + "");
        Log.e("single_vcc_dose", single_vcc_dose + "");

        Log.e("half_unvcc_dose", half_unvcc_dose + "");
        Log.e("half_vcc_dose", half_vcc_dose + "");

        if (total_pos > MIN_THRESHOLD) {
            tv_pos_vcc.setText(total_pos_vcc + "");
            tv_pos_unvcc.setText(total_pos_unvcc + "");

            vcc_rate = ((total_pos_unvcc - total_pos_vcc) / total_pos_unvcc) * 100;
            Log.e("vaccine_efficacy_rate", vcc_rate + "");

            single_vcc_rate = ((single_unvcc_dose - single_vcc_dose) / single_unvcc_dose) * 100;
            Log.e("single_vcc_rate", single_vcc_rate + "");

            half_vcc_rate = ((half_unvcc_dose - half_vcc_dose) / half_unvcc_dose) * 100;
            Log.e("half_vcc_rate", half_vcc_rate + "");

            tv_vcc_rate.setText(f.format(vcc_rate) + " %");

            tv_pos_vcc.setText(format.format(total_pos_vcc) + "");
            tv_pos_unvcc.setText(format.format(total_pos_unvcc) + "");

            tv_single_rate.setText(f.format(single_vcc_rate) + " %");
            tv_half_rate.setText(f.format(half_vcc_rate) + " %");


        }

        chart1_data = new ArrayList<>();
        chart1_data.add(new SliceValue((float) total_voluntree, Color.CYAN).setLabel("Total Voluntrees"));
        chart1_data.add(new SliceValue((float) total_pos, Color.GREEN).setLabel("Vaccine Positive"));
        chart1_data.add(new SliceValue((float) total_pos_vcc, Color.YELLOW).setLabel("Placebo Positive"));
        chart1_data.add(new SliceValue((float) total_pos_unvcc, Color.BLACK).setLabel("Placebo Positive"));

        chart2_data = new ArrayList<>();
        chart2_data.add(new SliceValue((float) total_voluntree, Color.RED).setLabel("Total Voluntrees"));
        chart2_data.add(new SliceValue((float) total_pos_vcc, Color.BLUE).setLabel("Vaccine Positive"));
        chart2_data.add(new SliceValue((float) total_pos_unvcc, Color.MAGENTA).setLabel("Placebo Positive"));

        setChart();

        tv_total_volunteers.setText(format.format(total_voluntree) + "");
        tv_total_pos.setText(format.format(total_pos) + "");
    }

    public void setChart() {

        PieChartData chartData = new PieChartData(chart1_data);
        chartData.setHasLabels(true);
        chart1.setPieChartData(chartData);

        PieChartData chartData2 = new PieChartData(chart2_data);
        chartData2.setHasLabels(true);
        chart2.setPieChartData(chartData2);
    }


    public void getDataFromAPI() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loding DashBoard...");
        progressDialog.show();

        String url = "https://firestore.googleapis.com/v1/projects/gocorona-ba478/databases/(default)/documents/Volunteers?key=%20AIzaSyDUYg4BPWy6yA2xzEWfIvqlzOeqIuT0FK8";

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("json data", response.toString());
                try {
                    JSONObject mainJsonObject = new JSONObject(response);
                    JSONArray jsonArray = mainJsonObject.getJSONArray("documents");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject fieldObject = jsonObject.getJSONObject("fields");

                        Volunteer volunteer = new Volunteer();

                        JSONObject emailObject = fieldObject.getJSONObject("email");
                        volunteer.setEmail(emailObject.getString("stringValue"));

                        JSONObject nameObject = fieldObject.getJSONObject("name");
                        volunteer.setFullName(nameObject.getString("stringValue"));

                        JSONObject ageObject = fieldObject.getJSONObject("age");
                        volunteer.setAge(ageObject.getString("stringValue"));

                        JSONObject addressObject = fieldObject.getJSONObject("address");
                        volunteer.setAddress(addressObject.getString("stringValue"));

                        JSONObject genderObject = fieldObject.getJSONObject("gender");
                        volunteer.setGender(genderObject.getString("stringValue"));

                        JSONObject healthObject = fieldObject.getJSONObject("health");
                        volunteer.setHealthCondition(healthObject.getString("stringValue"));

                        JSONObject vaccineObject = fieldObject.getJSONObject("vaccine");
                        volunteer.setVaccine(vaccineObject.getString("stringValue"));

                        JSONObject doseObject = fieldObject.getJSONObject("dose");
                        volunteer.setDose(doseObject.getString("stringValue"));

                        JSONObject infectedObject = fieldObject.getJSONObject("infected");
                        volunteer.setInfected(infectedObject.getString("stringValue"));

                        volunteerList.add(volunteer);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error:", e.getMessage());
                }

                Log.e("volunteerList:", volunteerList.size() + "");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                updateUI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}