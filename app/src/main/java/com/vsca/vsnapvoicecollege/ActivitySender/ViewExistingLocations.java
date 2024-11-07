package com.vsca.vsnapvoicecollege.ActivitySender;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vsca.vsnapvoicecollege.Adapters.LocationsHistoryAdapter;
import com.vsca.vsnapvoicecollege.Interfaces.LocationClick;
import com.vsca.vsnapvoicecollege.Model.StaffBiometricLocationRes;
import com.vsca.vsnapvoicecollege.R;
import com.vsca.vsnapvoicecollege.Repository.RestClient;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewExistingLocations extends AppCompatActivity {

    public LocationsHistoryAdapter mAdapter;
    String SchoolID = "", StaffID = "";
    public List<StaffBiometricLocationRes.BiometricLoationData> locationsList = new ArrayList<StaffBiometricLocationRes.BiometricLoationData>();
    RecyclerView recyleLocations;
    private PopupWindow editPopup;
    ConstraintLayout constParent;
    TextView lblNoRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_history_popup);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
//
//        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Locations");
//        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
//        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");

        ImageView gifImage = (ImageView) findViewById(R.id.gifImage);
        TextView lblTitle = (TextView) findViewById(R.id.lblTitle);
        recyleLocations = (RecyclerView) findViewById(R.id.recyleLocations);
        constParent = (ConstraintLayout) findViewById(R.id.constParent);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);

        Glide.with(this)
                .asGif()
                .load(R.drawable.map_location) // Replace with your GIF resource
                .into(gifImage);

        lblTitle.setTypeface(null, Typeface.BOLD);
        getExistingViewLocations();
    }

    private void getExistingViewLocations() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        Call<StaffBiometricLocationRes> call = RestClient.apiInterfaces.getExistingViewLocations(SchoolID);
        call.enqueue(new Callback<StaffBiometricLocationRes>() {
            @Override
            public void onResponse(Call<StaffBiometricLocationRes> call, Response<StaffBiometricLocationRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("locations:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("locations_res", data);
                        Log.d("locations_res", response.body().toString());
                        locationsList.clear();
                        if (response.body().getStatus() == 1) {
                            recyleLocations.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            locationsList = response.body().getData();
                            mAdapter = new LocationsHistoryAdapter(locationsList, ViewExistingLocations.this, new LocationClick() {
                                @Override
                                public void onItemClick(StaffBiometricLocationRes.BiometricLoationData item, Boolean isEdit) {
                                    if (isEdit) {
                                        editPopup(item);
                                    } else {
                                        deleteAlert(item);
                                    }
                                }
                            });
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewExistingLocations.this);
                            recyleLocations.setLayoutManager(mLayoutManager);
                            recyleLocations.setItemAnimator(new DefaultItemAnimator());
                            recyleLocations.setAdapter(mAdapter);
                            recyleLocations.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            recyleLocations.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());
                            lblNoRecords.setTypeface(null, Typeface.BOLD);

                        }
                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.che), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
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

    private void editPopup(StaffBiometricLocationRes.BiometricLoationData item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_location_popup, null);
        editPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        editPopup.setContentView(layout);
        constParent.post(new Runnable() {
            public void run() {
                editPopup.showAtLocation(constParent, Gravity.CENTER, 0, 0);
            }
        });

        EditText txtLocationName = (EditText) layout.findViewById(R.id.txtLocationName);
        EditText txtDistance = (EditText) layout.findViewById(R.id.txtDistance);
        TextView btnUpdate = (TextView) layout.findViewById(R.id.btnUpdate);
        TextView btnCancel = (TextView) layout.findViewById(R.id.btnCancel);
        txtLocationName.setText(item.getLocation());
        txtDistance.setText(item.getDistance() + " Meters");
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPopup.dismiss();
                String distance = txtDistance.getText().toString().replaceAll("[^0-9]", ""); // Keep only numbers
                Log.d("en_distance",distance);
                int distanceCheck = Integer.parseInt(distance);
                if (distanceCheck >= 10) {
                    Log.d("en_distance","true");
                    updateLocationAPI(String.valueOf(distanceCheck),txtLocationName.getText().toString(),item.getId());
                } else {
                    Toast.makeText(ViewExistingLocations.this, "Distance should be minimum 10 Meters", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPopup.dismiss();
            }
        });
    }

    private void updateLocationAPI(String distance,String locationName,int id) {
        final ProgressDialog mProgressDialog = new ProgressDialog(ViewExistingLocations.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!ViewExistingLocations.this.isFinishing()) mProgressDialog.show();

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("id", id);
        jsonObjectSchool.addProperty("location", locationName);
        jsonObjectSchool.addProperty("distance", distance);
        jsonObjectSchool.addProperty("userId", StaffID);
        Log.d("location_update_request", jsonObjectSchool.toString());
        Call<JsonObject> call = RestClient.apiInterfaces.updateLocation(jsonObjectSchool);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Biometric:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Location:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 1) {
                            getExistingViewLocations();
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.e("Excep", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }

    private void deleteAlert(StaffBiometricLocationRes.BiometricLoationData item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewExistingLocations.this);
        alertDialog.setTitle("Delete location !!");
        alertDialog.setMessage("Are you sure you want to delete this location?");
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                removeLocationApi(item);
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

    private void removeLocationApi(StaffBiometricLocationRes.BiometricLoationData item) {
        final ProgressDialog mProgressDialog = new ProgressDialog(ViewExistingLocations.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (!ViewExistingLocations.this.isFinishing()) mProgressDialog.show();

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("CollegeId", SchoolID);
        jsonObjectSchool.addProperty("locationId", item.getId());
        Log.d("location_remove_request", jsonObjectSchool.toString());
        Call<JsonObject> call = RestClient.apiInterfaces.removeLocation(jsonObjectSchool);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Biometric:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Location:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 1) {
                            getExistingViewLocations();
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.e("Excep", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }
}