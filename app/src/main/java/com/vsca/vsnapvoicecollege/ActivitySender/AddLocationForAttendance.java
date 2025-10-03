package com.vsca.vsnapvoicecollege.ActivitySender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vsca.vsnapvoicecollege.Adapters.LocationsHistoryAdapter;
import com.vsca.vsnapvoicecollege.Interfaces.GPSStatusListener;
import com.vsca.vsnapvoicecollege.Interfaces.LocationLatLongListener;
import com.vsca.vsnapvoicecollege.Model.StaffBiometricLocationRes;
import com.vsca.vsnapvoicecollege.R;
import com.vsca.vsnapvoicecollege.Repository.RestClient;
import com.vsca.vsnapvoicecollege.Utils.CommonUtil;
import com.vsca.vsnapvoicecollege.Utils.GPSStatusReceiver;
import com.vsca.vsnapvoicecollege.Utils.LocationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationForAttendance extends AppCompatActivity implements GPSStatusListener, LocationLatLongListener, View.OnClickListener {

    private int locationRequestCode = 1000;
    private GPSStatusReceiver gpsStatusReceiver;
    String SchoolID = "", StaffID = "";

    ImageView gifImageView;
    EditText txtLocationName, txtMeters;
    TextView lblAddress, lbllatLong, btnPickLocation, btnAddLocation, btnEnableLocation;
    LinearLayout lnrAddressLayout;

    TextView lblLocationTitle, lblAddressTitle, lblDistanceTitle, lblLateLongTitle, lblNote, btnViewLocations;

    FrameLayout frmAddLocationLayout;
    RelativeLayout rytGPSRedirect, rytProgressBar, rytParent;

    double current_latitude, current_longitude;
    String Address = "";
    Spinner spinnerMetres;
    String[] Metres = {"20", "30", "40",
            "50", "60", "70", "80", "90", "100", "Custom"};

    String SelectedSpinnerDistance = "";
    private PopupWindow locationHistoryPopup;

    public LocationsHistoryAdapter mAdapter;

    public List<StaffBiometricLocationRes.BiometricLoationData> locationsList = new ArrayList<StaffBiometricLocationRes.BiometricLoationData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_location_for_attendance);

        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(true);

        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");

        gifImageView = (ImageView) findViewById(R.id.gifImageView);
        txtLocationName = (EditText) findViewById(R.id.txtLocationName);
        txtMeters = (EditText) findViewById(R.id.txtMeters);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lbllatLong = (TextView) findViewById(R.id.lbllatLong);
        btnPickLocation = (TextView) findViewById(R.id.btnPickLocation);
        btnAddLocation = (TextView) findViewById(R.id.btnAddLocation);
        btnEnableLocation = (TextView) findViewById(R.id.btnEnableLocation);
        btnViewLocations = (TextView) findViewById(R.id.btnViewLocations);

        lnrAddressLayout = (LinearLayout) findViewById(R.id.lnrAddressLayout);
        frmAddLocationLayout = (FrameLayout) findViewById(R.id.frmAddLocationLayout);
        rytGPSRedirect = (RelativeLayout) findViewById(R.id.rytGPSRedirect);
        rytProgressBar = (RelativeLayout) findViewById(R.id.rytProgressBar);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        spinnerMetres = (Spinner) findViewById(R.id.spinnerMetres);

        lblLocationTitle = (TextView) findViewById(R.id.lblLocationTitle);
        lblLocationTitle.setTypeface(null, Typeface.BOLD);
        lblAddressTitle = (TextView) findViewById(R.id.lblAddressTitle);
        lblAddressTitle.setTypeface(null, Typeface.BOLD);
        lblDistanceTitle = (TextView) findViewById(R.id.lblDistanceTitle);
        lblDistanceTitle.setTypeface(null, Typeface.BOLD);
        lblLateLongTitle = (TextView) findViewById(R.id.lblLateLongTitle);
        lblLateLongTitle.setTypeface(null, Typeface.BOLD);
        lblNote = (TextView) findViewById(R.id.lblNote);
        lblNote.setTypeface(null, Typeface.BOLD);

        btnPickLocation.setOnClickListener(this);
        btnAddLocation.setOnClickListener(this);
        btnEnableLocation.setOnClickListener(this);
        btnViewLocations.setOnClickListener(this);

        txtMeters.setEnabled(false);
        ArrayAdapter ad = new ArrayAdapter(this, R.layout.spinner_textview, Metres);
        ad.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerMetres.setAdapter(ad);
        spinnerMetres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SelectedSpinnerDistance = Metres[position];
                if (Metres[position].equals("Custom")) {
                    txtMeters.setText("");
                    txtMeters.setEnabled(true);
                } else {
                    txtMeters.setText(Metres[position] + " Meters");
                    txtMeters.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Load the GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.map_location) // Replace with your GIF resource
                .into(gifImageView);


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
        gpsStatusReceiver = new GPSStatusReceiver(this);
        registerReceiver(gpsStatusReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        getPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gpsStatusReceiver);
        Log.d("onPause", "onPause");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CommonUtil.isGPSEnabled(AddLocationForAttendance.this)) {
                        frmAddLocationLayout.setVisibility(View.VISIBLE);
                        rytGPSRedirect.setVisibility(View.GONE);
                        getCurentLocation();
                    } else {
                        rytGPSRedirect.setVisibility(View.VISIBLE);
                        frmAddLocationLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    private void addLocationAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!this.isFinishing()) mProgressDialog.show();

        String distance = "";
        if (SelectedSpinnerDistance.equals("Custom")) {
            distance = txtMeters.getText().toString();
        } else {
            distance = SelectedSpinnerDistance;
        }

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("CollegeId", SchoolID);
        jsonObjectSchool.addProperty("userId", StaffID);
        jsonObjectSchool.addProperty("location", txtLocationName.getText().toString());
        jsonObjectSchool.addProperty("latitude", current_latitude);
        jsonObjectSchool.addProperty("longitude", current_longitude);
        jsonObjectSchool.addProperty("distance", distance);
        Log.d("location_add_request", jsonObjectSchool.toString());
        Call<JsonArray> call = RestClient.Companion.getApiInterfaces().addBiometricLocation(jsonObjectSchool);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Biometric:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Location:Res", response.body().toString());

                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            if (status == 1) {
                                Toast.makeText(AddLocationForAttendance.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddLocationForAttendance.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.e("Excep", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnPickLocation) {
            getPermissions();
        } else if (id == R.id.btnAddLocation) {
            if (!txtLocationName.getText().toString().equals("") && !String.valueOf(current_latitude).equals("") && !String.valueOf(current_longitude).equals("") && !txtMeters.getText().toString().trim().equals("")) {

                String distance = "";
                if (SelectedSpinnerDistance.equals("Custom")) {
                    distance = txtMeters.getText().toString();
                } else {
                    distance = SelectedSpinnerDistance;
                }

                int distanceCheck = Integer.parseInt(distance);
                if (distanceCheck >= 10) {
                    addConfirmationAlert();
                } else {
                    Toast.makeText(AddLocationForAttendance.this, "Distance should be minimum 10 Meters", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddLocationForAttendance.this, "Please enter all the required fields", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.btnEnableLocation) {
            AddLocationForAttendance.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else if (id == R.id.btnViewLocations) {
            Intent inVoice = new Intent(this, ViewExistingLocations.class);
            inVoice.putExtra("SCHOOL_ID", SchoolID);
            inVoice.putExtra("STAFF_ID", StaffID);
            startActivity(inVoice);
        }

    }

    private void addConfirmationAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLocationForAttendance.this);
        alertDialog.setTitle("Add location !!");
        alertDialog.setMessage("Are you sure you want to add this location?");
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                addLocationAPI();
            }
        });
        alertDialog.setPositiveButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void getPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            if (CommonUtil.isGPSEnabled(this)) {
                rytGPSRedirect.setVisibility(View.GONE);
                frmAddLocationLayout.setVisibility(View.VISIBLE);
                getCurentLocation();
            } else {
                rytGPSRedirect.setVisibility(View.VISIBLE);
                frmAddLocationLayout.setVisibility(View.GONE);

            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurentLocation() {
        LocationHelper call = new LocationHelper(AddLocationForAttendance.this, this, "new");
        call.getFreshLocation(AddLocationForAttendance.this);
        rytProgressBar.setVisibility(View.VISIBLE);
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // You can get other details like city, state, etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    @Override
    public void onGPSStatusChanged(boolean isGPSEnabled) {
        getPermissions();
    }

    @Override
    public void onLocationReturn(double latitude, double longitude, String type) {

        Log.d("lat_long", latitude + "_" + longitude);
        rytProgressBar.setVisibility(View.GONE);

        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {

            Log.d("latitude", String.valueOf(latitude));
            Log.d("longitude", String.valueOf(longitude));

            current_latitude = latitude;
            current_longitude = longitude;

            lbllatLong.setText(latitude + " - " + longitude);
            String address = getAddressFromLatLng(latitude, longitude);
            Address = address;
            lblAddress.setText(address);

            lnrAddressLayout.setVisibility(View.VISIBLE);
            btnAddLocation.setVisibility(View.VISIBLE);
            btnPickLocation.setVisibility(View.GONE);

            Log.d("Address:", address);

        } else {
            lnrAddressLayout.setVisibility(View.GONE);
            btnAddLocation.setVisibility(View.GONE);
            btnPickLocation.setVisibility(View.VISIBLE);
            Toast.makeText(AddLocationForAttendance.this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
        }

    }
}