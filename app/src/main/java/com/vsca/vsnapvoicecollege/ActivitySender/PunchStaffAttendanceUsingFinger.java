package com.vsca.vsnapvoicecollege.ActivitySender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vsca.vsnapvoicecollege.Adapters.AttendanceReportsAdapter;
import com.vsca.vsnapvoicecollege.Adapters.PunchHistoryAdapter;
import com.vsca.vsnapvoicecollege.Interfaces.GPSStatusListener;
import com.vsca.vsnapvoicecollege.Interfaces.LocationLatLongListener;
import com.vsca.vsnapvoicecollege.Interfaces.ViewPunchHistoryListener;
import com.vsca.vsnapvoicecollege.Model.PunchHistoryRes;
import com.vsca.vsnapvoicecollege.Model.StaffAttendanceBiometricReportRes;
import com.vsca.vsnapvoicecollege.Model.StaffBiometricLocationRes;
import com.vsca.vsnapvoicecollege.Model.monthsModel;
import com.vsca.vsnapvoicecollege.R;
import com.vsca.vsnapvoicecollege.Repository.RestClient;
import com.vsca.vsnapvoicecollege.Utils.CommonUtil;
import com.vsca.vsnapvoicecollege.Utils.GPSStatusReceiver;
import com.vsca.vsnapvoicecollege.Utils.LocationDistanceCalculator;
import com.vsca.vsnapvoicecollege.Utils.LocationHelper;
import com.vsca.vsnapvoicecollege.Utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PunchStaffAttendanceUsingFinger extends AppCompatActivity implements GPSStatusListener, LocationLatLongListener, View.OnClickListener {
    private int locationRequestCode = 1000;
    private GPSStatusReceiver gpsStatusReceiver;
    RelativeLayout rytGPSRedirect, rytParent, rytPresentlayout, rytAddLocation, rytMarkAttendanceSceen, rytAttendanceHistorySceen, rytProgressBar, rytEnableFingerPrint;
    TextView btnPresent, lblErrorMessage, btnEnableLocation, btnMarkAttendance, btnAttendanceHistory, btnDatePicker,lblNoRecords;
    private PopupWindow authenticatealertpopupWindow, enableBiometricPopup;
    private BiometricPrompt biometricPrompt;
    Boolean ifBiometricAvailable = false;
    private Switch enableSwitch;
    RecyclerView recycleAttendanceReports;

    public AttendanceReportsAdapter mAdapter;
    public PunchHistoryAdapter punchHistoryAdapter;

    RelativeLayout rytYears,rytMonths;
    Spinner spinnerYears,spinnerMonths;

    int currentYear = 0;
    String[] Years = new String[20];

    Boolean isMarkAttendnaceScreen = true;
    String latitudeToStopCalling = "";
    String langitudeToStopCalling = "";

    Boolean ifFingerPrintCancelled = false;
    private PopupWindow viewPunchHistoryPopup;

    public List<StaffBiometricLocationRes.BiometricLoationData> locationsList = new ArrayList<StaffBiometricLocationRes.BiometricLoationData>();
    public List<PunchHistoryRes.PunchHistoryData> punchTimingList = new ArrayList<PunchHistoryRes.PunchHistoryData>();
    public List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> attendanceReportsList = new ArrayList<StaffAttendanceBiometricReportRes.BiometriStaffReportData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_attendance_finger_print);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Mark Attendance");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        rytGPSRedirect = (RelativeLayout) findViewById(R.id.rytGPSRedirect);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        rytPresentlayout = (RelativeLayout) findViewById(R.id.rytPresentlayout);
        rytAddLocation = (RelativeLayout) findViewById(R.id.rytAddLocation);
        rytMarkAttendanceSceen = (RelativeLayout) findViewById(R.id.rytMarkAttendanceSceen);
        rytAttendanceHistorySceen = (RelativeLayout) findViewById(R.id.rytAttendanceHistorySceen);
        rytProgressBar = (RelativeLayout) findViewById(R.id.rytProgressBar);
        rytEnableFingerPrint = (RelativeLayout) findViewById(R.id.rytEnableFingerPrint);
        btnPresent = (TextView) findViewById(R.id.btnPresent);
        lblErrorMessage = (TextView) findViewById(R.id.lblErrorMessage);
        btnEnableLocation = (TextView) findViewById(R.id.btnEnableLocation);
        btnMarkAttendance = (TextView) findViewById(R.id.btnMarkAttendance);
        btnAttendanceHistory = (TextView) findViewById(R.id.btnAttendanceHistory);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);
        recycleAttendanceReports = (RecyclerView) findViewById(R.id.recycleAttendanceReports);

        rytYears = (RelativeLayout) findViewById(R.id.rytYears);
        rytMonths = (RelativeLayout) findViewById(R.id.rytMonths);
        spinnerYears = (Spinner) findViewById(R.id.spinnerYears);
        spinnerMonths = (Spinner) findViewById(R.id.spinnerMonths);

        enableSwitch = (Switch) findViewById(R.id.enableSwitch);

        btnEnableLocation.setOnClickListener(this);
        btnPresent.setOnClickListener(this);
        btnMarkAttendance.setOnClickListener(this);
        btnAttendanceHistory.setOnClickListener(this);
        rytAddLocation.setOnClickListener(this);

        if (CommonUtil.INSTANCE.getMenu_writeMarkAttendance().equals("1")) {
            rytAddLocation.setVisibility(View.VISIBLE);
        } else {
            rytAddLocation.setVisibility(View.GONE);
        }

        Boolean isEnabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
        if (isEnabled) {
            enableSwitch.setChecked(true);
        } else {
            enableSwitch.setChecked(false);
        }
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Boolean enabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
                if (!enabled) {
                    enableLocalFingerPrint();
                }
                //enableSwitch.setText("FingerPrint");
            } else {
                Boolean enabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
                if (enabled) {
                    showFingerPrintDisablepopup();
                }
                //enableSwitch.setText("FingerPrint");
            }
        });


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                rytEnableFingerPrint.setVisibility(View.VISIBLE);
                ifBiometricAvailable = true;

                Log.d("BIOMETRIC_STATUS", "Biometric features are available and the user has enrolled biometric credentials");
                // Biometric features are available and the user has enrolled biometric credentials
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                ifBiometricAvailable = false;
                rytEnableFingerPrint.setVisibility(View.GONE);

                Log.d("BIOMETRIC_STATUS", "No biometric hardware available on this device");
                // No biometric hardware available on this device
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                ifBiometricAvailable = false;
                rytEnableFingerPrint.setVisibility(View.GONE);

                Log.d("BIOMETRIC_STATUS", "Biometric hardware is currently unavailable");
                // Biometric hardware is currently unavailable
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                ifBiometricAvailable = false;
                rytEnableFingerPrint.setVisibility(View.GONE);

                Log.d("BIOMETRIC_STATUS", "No biometric data enrolled; prompt the user to set up biometrics");
                // No biometric data enrolled; prompt the user to set up biometrics
                break;
        }


    }

    private void loadYearsSpinner() {
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        Years[0] = String.valueOf(currentYear);
        for(int i=1 ; i<20 ;i++){
            currentYear = currentYear - 1;
            Years[i] = String.valueOf(currentYear);
        }


        ArrayAdapter ad = new ArrayAdapter(this, R.layout.spinner_textview, Years);
        ad.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerYears.setAdapter(ad);
        spinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("SelectedYear",Years[position]);
                loadMonthsSpinner(Years[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadMonthsSpinner(String selectedYear) {
        Calendar calendar = Calendar.getInstance();
        int currentMonthIndex = calendar.get(Calendar.MONTH); // 0 = January, 11 = December
        // Get all month names
        String[] monthNames = new DateFormatSymbols().getMonths();

        List<String> monthList = new ArrayList<>();
        monthList.add(monthNames[currentMonthIndex]);

        monthsModel data ;
        List<monthsModel> monthDataList = new ArrayList<>();
        data = new monthsModel(currentMonthIndex,monthNames[currentMonthIndex]);
        monthDataList.add(data);
        // Add the rest of the months
        for (int i = 0; i < monthNames.length; i++) {
            if (i != currentMonthIndex) {
                monthList.add(monthNames[i]);
                data = new monthsModel(i,monthNames[i]);
                monthDataList.add(data);
            }
        }

        ArrayAdapter ad1 = new ArrayAdapter(this, R.layout.spinner_textview, monthList);
        ad1.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerMonths.setAdapter(ad1);
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String monthID = "";
                int ID = monthDataList.get(position).getID();
                if(ID > 8){
                    monthID = selectedYear+ "-"+String.valueOf(ID+1);
                }
                else {
                    monthID = selectedYear+ "-"+"0"+String.valueOf(ID+1);
                }
                getBiometricAttendanceReport(monthID);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showFingerPrintDisablepopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PunchStaffAttendanceUsingFinger.this);
        alertDialog.setTitle("Disable Fingerprint !!");
        alertDialog.setMessage("Are you sure you want to disable fingerprint authentication?");
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                enableSwitch.setChecked(false);
                SharedPreference.putBiometricEnabled(PunchStaffAttendanceUsingFinger.this, false);
            }
        });
        alertDialog.setPositiveButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Boolean enabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
                if (enabled) {
                    enableSwitch.setChecked(true);
                } else {
                    enableSwitch.setChecked(false);
                }
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void enableLocalFingerPrint() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.biometric_permission_enable_popup, null);
        enableBiometricPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        enableBiometricPopup.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                enableBiometricPopup.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });
        TextView btnAllow = (TextView) layout.findViewById(R.id.btnAllow);
        TextView btnSkip = (TextView) layout.findViewById(R.id.btnSkip);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.putBiometricSkip(PunchStaffAttendanceUsingFinger.this, false);
                SharedPreference.putBiometricEnabled(PunchStaffAttendanceUsingFinger.this, true);
                enableSwitch.setChecked(true);
                enableBiometricPopup.dismiss();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.putBiometricSkip(PunchStaffAttendanceUsingFinger.this, true);
                enableSwitch.setChecked(false);
                enableBiometricPopup.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBiometricPopup.dismiss();
                Boolean isEnabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
                if (isEnabled) {
                    enableSwitch.setChecked(true);
                }
                else {
                    enableSwitch.setChecked(false);
                }
            }
        });
    }

    private void getLocationPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            if (CommonUtil.isGPSEnabled(this)) {
                rytGPSRedirect.setVisibility(View.GONE);
                getCurentLocation();
            } else {
                rytGPSRedirect.setVisibility(View.VISIBLE);
                lblErrorMessage.setVisibility(View.GONE);
                rytPresentlayout.setVisibility(View.GONE);

            }
        }
    }

    private void enableBiometric() {
        Boolean isEnab = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
        Boolean isBiometricSkip = SharedPreference.getBiometricSkip(PunchStaffAttendanceUsingFinger.this);

        if (isBiometricSkip) {
            putAttendanceDataAPI(false);
        } else {
            if (!isEnab && ifBiometricAvailable) {
                enableLocalFingerPrint();
            } else {
                Boolean isEnabled = SharedPreference.getBiometricEnabled(PunchStaffAttendanceUsingFinger.this);
                if (isEnabled) {
                    authenticatStart();
                } else {
                    putAttendanceDataAPI(false);
                }
            }
        }
    }

    private void authenticatStart() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(PunchStaffAttendanceUsingFinger.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

//               if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
//                }

                if (ifBiometricAvailable) {
                    if (authenticatealertpopupWindow != null) {
                        if (authenticatealertpopupWindow.isShowing()) {
                            authenticatealertpopupWindow.dismiss();
                        }
                    }
                    againAuthenticatePopup();
                }

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                putAttendanceDataAPI(true);
                // Handle successful authentication here
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentications")
                .setSubtitle("Mark attendance using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }


    private void getStaffLocations(double current_latitude, double current_longitude) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("userId", CommonUtil.INSTANCE.getMemberId());
        jsonObjectSchool.addProperty("CollegeId", CommonUtil.INSTANCE.getCollegeId());

        Log.d("biometric_request", jsonObjectSchool.toString());
        Call<StaffBiometricLocationRes> call = RestClient.apiInterfaces.getStaffBiometricLocations(jsonObjectSchool);
        call.enqueue(new Callback<StaffBiometricLocationRes>() {
            @Override
            public void onResponse(Call<StaffBiometricLocationRes> call, Response<StaffBiometricLocationRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("locations:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("locations_res", response.body().toString());
                        locationsList.clear();
                        if (response.body().getStatus() == 1) {
                            locationsList = response.body().getData();
                            Boolean locationIsNearBy = false;
                            for (int i = 0; i < locationsList.size(); i++) {
                                Double staff_lat = Double.parseDouble(locationsList.get(i).getLatitude());
                                Double staff_long = Double.parseDouble(locationsList.get(i).getLongitude());
                                int distance = Integer.parseInt(locationsList.get(i).getDistance());
                                float resultsof = LocationDistanceCalculator.calculateDistance(current_latitude, current_longitude, staff_lat, staff_long);
                                Log.d("Distance in metres", "Distance between points: " + resultsof + " meters");
                                if (resultsof <= (float) distance) {
                                    locationIsNearBy = true;
                                    break;
                                }
                            }
                            if (locationIsNearBy) {
                                lblErrorMessage.setVisibility(View.GONE);
                                rytPresentlayout.setVisibility(View.VISIBLE);
                            } else {
                                rytPresentlayout.setVisibility(View.GONE);
                                lblErrorMessage.setVisibility(View.VISIBLE);
                            }
                        } else {
                            showAlertMessage(response.body().getMessage());
                        }
                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StaffBiometricLocationRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }
        });
    }
    private void getBiometricAttendanceReport(String monthID) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("UserId", CommonUtil.INSTANCE.getMemberId());
        jsonObjectSchool.addProperty("CollegeId", CommonUtil.INSTANCE.getCollegeId());
        jsonObjectSchool.addProperty("attendance_month", monthID);
        jsonObjectSchool.addProperty("attendance_dt", "");

        Log.d("biometric_request", jsonObjectSchool.toString());

        Call<StaffAttendanceBiometricReportRes> call = RestClient.apiInterfaces.getStaffBiometricAttendanceReport(jsonObjectSchool);
        call.enqueue(new Callback<StaffAttendanceBiometricReportRes>() {
            @Override
            public void onResponse(Call<StaffAttendanceBiometricReportRes> call, Response<StaffAttendanceBiometricReportRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("attendanceReport", response.body().toString());
                        attendanceReportsList.clear();
                        if (response.body().getStatus() == 1) {
                            recycleAttendanceReports.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            attendanceReportsList = response.body().getData();

                            mAdapter = new AttendanceReportsAdapter(attendanceReportsList, PunchStaffAttendanceUsingFinger.this,"", new ViewPunchHistoryListener() {
                                @Override
                                public void onItemClick(StaffAttendanceBiometricReportRes.BiometriStaffReportData item) {
                                    viewPunchHistoryPopup(item);
                                }
                            });
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PunchStaffAttendanceUsingFinger.this);
                            recycleAttendanceReports.setLayoutManager(mLayoutManager);
                            recycleAttendanceReports.setItemAnimator(new DefaultItemAnimator());
                            recycleAttendanceReports.setAdapter(mAdapter);
                            recycleAttendanceReports.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            recycleAttendanceReports.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());
                            lblNoRecords.setTypeface(null, Typeface.BOLD);

                        }
                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<StaffAttendanceBiometricReportRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }
        });
    }
    private void viewPunchHistory(StaffAttendanceBiometricReportRes.BiometriStaffReportData item, RecyclerView recycleHistory, TextView lblNoRecords) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("UserId", CommonUtil.INSTANCE.getMemberId());
        jsonObjectSchool.addProperty("CollegeId", CommonUtil.INSTANCE.getCollegeId());
        jsonObjectSchool.addProperty("fromdate", item.getDate());
        jsonObjectSchool.addProperty("todate", item.getDate());

        Log.d("biometric_request", jsonObjectSchool.toString());
        Call<PunchHistoryRes> call = RestClient.apiInterfaces.viewPunchHistory(jsonObjectSchool);
        call.enqueue(new Callback<PunchHistoryRes>() {


            @Override
            public void onResponse(Call<PunchHistoryRes> call, Response<PunchHistoryRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("punchHistoryReport", response.body().toString());
                        punchTimingList.clear();
                        if (response.body().getStatus() == 1)
                        {
                            recycleHistory.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            punchTimingList = response.body().getData();
                            punchHistoryAdapter = new PunchHistoryAdapter(punchTimingList.get(0).getTimings(), PunchStaffAttendanceUsingFinger.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PunchStaffAttendanceUsingFinger.this);
                            recycleHistory.setLayoutManager(mLayoutManager);
                            recycleHistory.setItemAnimator(new DefaultItemAnimator());
                            recycleHistory.setAdapter(punchHistoryAdapter);
                            recycleHistory.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            punchHistoryAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            recycleHistory.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());

                        }
                    }
                }
                catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PunchHistoryRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }
        });
    }

    private void viewPunchHistoryPopup(StaffAttendanceBiometricReportRes.BiometriStaffReportData item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.punch_history_popup, null);
        viewPunchHistoryPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        viewPunchHistoryPopup.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                viewPunchHistoryPopup.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        TextView lblTitle = (TextView) layout.findViewById(R.id.lblTitle);
        TextView lblNoRecords = (TextView) layout.findViewById(R.id.lblNoRecords);
        RecyclerView recycleHistory = (RecyclerView) layout.findViewById(R.id.recycleHistory);
        lblTitle.setTypeface(null, Typeface.BOLD);
        lblNoRecords.setTypeface(null, Typeface.BOLD);

        viewPunchHistory(item,recycleHistory,lblNoRecords);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPunchHistoryPopup.dismiss();
            }
        });
    }

    private void putAttendanceDataAPI(Boolean isFingerprint) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!this.isFinishing()) mProgressDialog.show();

        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String manufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;
        String fullDeviceInfo = manufacturer + " " + deviceModel;
        Log.d("fullDeviceInfo",fullDeviceInfo);

        int punch_type = 0;
        if (isFingerprint) {
            punch_type = 2;
        } else {
            punch_type = 1;
        }

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("UserId", String.valueOf(CommonUtil.INSTANCE.getMemberId()));
        jsonObjectSchool.addProperty("staff_or_student", "staff");
        jsonObjectSchool.addProperty("CollegeId", String.valueOf(CommonUtil.INSTANCE.getCollegeId()));
        jsonObjectSchool.addProperty("deviceId", deviceId);
        jsonObjectSchool.addProperty("punch_type", punch_type);
        jsonObjectSchool.addProperty("device_model", fullDeviceInfo);
        Log.d("biometric_request", jsonObjectSchool.toString());
        Call<JsonArray> call = RestClient.apiInterfaces.BiometricEntryforAttendance(jsonObjectSchool);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Log.d("Biometric:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Biometric:Res", response.body().toString());

                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            showAlertMessage(message);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }

    private void showAlertMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PunchStaffAttendanceUsingFinger.this);
        alertDialog.setTitle("Alert!!");
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void againAuthenticatePopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.authenticate_alert_popup, null);
        authenticatealertpopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        authenticatealertpopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                authenticatealertpopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView lblAuthenticate = (TextView) layout.findViewById(R.id.lblAuthenticate);
        lblAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticatealertpopupWindow.dismiss();
                authenticatStart();

            }
        });
    }

    private void redirectToEnableGPS() {
        PunchStaffAttendanceUsingFinger.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    @SuppressLint("MissingPermission")
    private void getCurentLocation() {
        LocationHelper call = new LocationHelper(PunchStaffAttendanceUsingFinger.this,this);
        call.getFreshLocation(PunchStaffAttendanceUsingFinger.this);
        rytProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        gpsStatusReceiver = new GPSStatusReceiver(this);
        registerReceiver(gpsStatusReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        Log.d("onResume", "onResume");
        getLocationPermissions();
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CommonUtil.isGPSEnabled(this)) {
                        rytGPSRedirect.setVisibility(View.GONE);
                        getCurentLocation();
                    }
                    else {
                        rytGPSRedirect.setVisibility(View.VISIBLE);
                        rytPresentlayout.setVisibility(View.GONE);
                        lblErrorMessage.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onGPSStatusChanged(boolean isGPSEnabled) {
        getLocationPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnableLocation:
                redirectToEnableGPS();
                break;

            case R.id.btnPresent:
                enableBiometric();
                break;

            case R.id.btnMarkAttendance:

                if (CommonUtil.INSTANCE.getMenu_writeMarkAttendance().equals("1")) {
                    rytAddLocation.setVisibility(View.VISIBLE);
                } else {
                    rytAddLocation.setVisibility(View.GONE);
                }

                latitudeToStopCalling = "";
                langitudeToStopCalling = "";
                isMarkAttendnaceScreen = true;
                btnMarkAttendance.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rect_yellow));
                btnAttendanceHistory.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_stroke_window_grey));
                btnMarkAttendance.setTextColor(ContextCompat.getColor(this, R.color.clr_white));
                btnAttendanceHistory.setTextColor(ContextCompat.getColor(this, R.color.clr_black));
                rytMarkAttendanceSceen.setVisibility(View.VISIBLE);
                rytAttendanceHistorySceen.setVisibility(View.GONE);
                getLocationPermissions();

                break;

            case R.id.btnAttendanceHistory:
                rytAddLocation.setVisibility(View.GONE);
                loadAttendanceHistory();

                break;

            case R.id.rytAddLocation:

                Intent inVoice = new Intent(PunchStaffAttendanceUsingFinger.this, AddLocationForAttendance.class);
                inVoice.putExtra("SCHOOL_ID", String.valueOf(CommonUtil.INSTANCE.getCollegeId()));
                inVoice.putExtra("STAFF_ID", String.valueOf(CommonUtil.INSTANCE.getMemberId()));
                startActivity(inVoice);

                break;

            default:
                break;
        }
    }
    private void loadAttendanceHistory() {
        isMarkAttendnaceScreen = false;
        loadYearsSpinner();
        btnMarkAttendance.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_stroke_window_grey));
        btnAttendanceHistory.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rect_yellow));
        btnMarkAttendance.setTextColor(ContextCompat.getColor(this, R.color.clr_black));
        btnAttendanceHistory.setTextColor(ContextCompat.getColor(this, R.color.clr_white));
        rytMarkAttendanceSceen.setVisibility(View.GONE);
        rytAttendanceHistorySceen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationReturn(double latitude, double longitude) {
        Log.d("lat_long",latitude + "_"+ longitude);
        rytProgressBar.setVisibility(View.GONE);
        if(isMarkAttendnaceScreen) {
            if(latitudeToStopCalling.equals("null") || langitudeToStopCalling.equals("null") || latitudeToStopCalling.equals("") || langitudeToStopCalling.equals("")) {
                latitudeToStopCalling = String.valueOf(latitude);
                langitudeToStopCalling = String.valueOf(longitude);
                getStaffLocations(latitude, longitude);
            }
        }
    }
}
