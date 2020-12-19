package com.example.e_parking.fragments;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_parking.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapLocationFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Context mContext;
    TextView tvAddress;
    SharedPreferences preferences;
    Button btok;
    Location mLastLocation;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient = null;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    static boolean counter = true;
    AutoCompleteTextView mAutocompleteTextView_to_loc;
//    private PlaceArrayAdapter mPlaceArrayAdapter;
    public static String curlocation;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private Animation animShow, animHide;
    String latitude, longitude;
    Geocoder geocoder;
    ImageView imgSearch;
    double lat = 0, lng = 0;
    int seach_flag = 0;

    public MapLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_location, container, false);
        counter = true;
        mContext = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        imgSearch = (ImageView) view.findViewById(R.id.imgSearch);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        /*lat = Double.valueOf(preferences.getString("diary_txt_lat","0"));
        lng = Double.valueOf(preferences.getString("diary_txt_lon","0"));*/

      /*  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("diary_txt_lat");
        editor.remove("diary_txt_lon");
        editor.remove("Edit_Edit_diary_txt_lat");
        editor.remove("Edit_diary_txt_lon");
        editor.commit();*/


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Initialise(view);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            onStop();
            try {
                mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                        //.addApi(Places.GEO_DATA_API)
                        .build();
            } catch (Exception e) {
                Log.e("Map_Catch: ", e.toString());
            }
        }


        mAutocompleteTextView_to_loc = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView_to_loc);
        final String str = preferences.getString("diary_txt_shootaddress", "");

        mAutocompleteTextView_to_loc.setText(str);
        mAutocompleteTextView_to_loc.setThreshold(3);
        mAutocompleteTextView_to_loc.setOnItemClickListener(mAutocompleteClickListenerDestination);
        //mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        //mAutocompleteTextView_to_loc.setAdapter(mPlaceArrayAdapter);
        geocoder = new Geocoder(mContext, Locale.getDefault());
        /*et_flat=(EditText)findViewById(R.id.et_flat);
        et_address=(EditText)findViewById(R.id.et_address);
        et_landmark=(EditText)findViewById(R.id.et_landmark);
        et_city=(EditText)findViewById(R.id.et_city);
        et_zip=(EditText)findViewById(R.id.et_zip);*/
        //initPopup();

        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorePreferences();
                onStop();
                getFragmentManager().beginTransaction().replace(R.id.frame, new NewParkingFragment(),
                        "ok").commit();

             /*   if(! (tvAddress.getText().toString().trim().equals("getting address...") || tvAddress.getText().toString().trim().equals(""))){
                    StorePreferences();
                    onStop();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new DiaryentryFragment(),"ok").commit();
                }else {
                    Toast.makeText(getActivity(),"Search for a Valid Loction of Shoot in the above Seach box",Toast.LENGTH_LONG);
                }*/
            }
        });
        if (!str.equals("")) {
            getRegionLatLng(str);
        }

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                if (!mAutocompleteTextView_to_loc.getText().toString().trim().equals("")) {
                    getRegionLatLng(mAutocompleteTextView_to_loc.getText().toString().trim());
                } else {
                    //showToast("No Location Found", Toast.LENGTH_SHORT);
                }

            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("diary_StayHere", "True").commit();
                onStop();
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().beginTransaction().replace(R.id.frame, new NewParkingFragment(), "ok").commit();
                    return true;
                }
                //getFragmentManager().beginTransaction().replace(R.id.content_nav, new OrderFragment(),"ok").addToBackStack(null).commit();
                return false;
            }
        });

        return view;
    }//OnCreate

    private void getRegionLatLng(String str) {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(str, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
                if (seach_flag == 0) {
                    seach_flag++;
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f));
                }
            } else {
                ShowDialogNoLatLngOnly();
            }
        } catch (IOException e) {
            Log.e("Exp", e.toString());
        }
    }//getRegionLatLng

    private void ShowDialogNoLatLngOnly() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Location Not Found, Try Again To Search.");

        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();

        alertDialogObject.setOnShowListener(new DialogInterface.OnShowListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                // this not working because multiplying white background (e.g. Holo Light) has no effect

                final Drawable negativeButtonDrawable = getResources().getDrawable(R.drawable.ic_menu_camera);
                final Drawable positiveButtonDrawable = getResources().getDrawable(R.drawable.ic_menu_gallery);
                final int positiveButtonDrawabletextback = getResources().getColor(R.color.bgColor);
                final int negativeButtonDrawabletextback = getResources().getColor(R.color.colorAccent);
                final int positiveButtonDrawabletext = getResources().getColor(R.color.colorAccent);
                final int negativeButtonDrawabletext = getResources().getColor(R.color.bgColor);
                if (Build.VERSION.SDK_INT >= 16) {
                    positiveButton.setBackground(positiveButtonDrawable);
                    positiveButton.setBackgroundColor(positiveButtonDrawabletextback);
                    positiveButton.setTextColor(positiveButtonDrawabletext);
                } else {
                    positiveButton.setBackgroundDrawable(positiveButtonDrawable);
                    positiveButton.setBackgroundColor(positiveButtonDrawabletextback);
                    positiveButton.setTextColor(positiveButtonDrawabletext);
                }

                positiveButton.invalidate();
            }
        });
        alertDialogObject.show();

    }//ShowDialogNoLatLngOnly

    private void StorePreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("diary_txt_shootloc", tvAddress.getText().toString().trim());

        editor.putString("diary_txt_lat", latitude);
        editor.putString("diary_txt_lon", longitude);

        editor.commit();

    }//StorePreferences

    private void Initialise(View view) {
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "lane.ttf");
        tvAddress = (TextView) view.findViewById(R.id.uber_tvAddress);
        tvAddress.setTypeface(custom_font);
        btok = (Button) view.findViewById(R.id.btok);
        btok.setTypeface(custom_font);

    }//Initialise


    private AdapterView.OnItemClickListener mAutocompleteClickListenerDestination = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            //final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            //final String placeId = String.valueOf(item.placeId);
            //curlocation = String.valueOf(item.description);
            //Log.i(LOG_TAG, "Selected: " + item.description);
            //PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            //placeResult.setResultCallback(mUpdatePlaceDetailsCallbackDestination);
            //Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);


            /*Intent i=new Intent(HomeActivity.this,FindRideFragment.class);
            i.putExtra("DEST",dest);*/
        }

    };
    /*private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackDestination = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            LatLng mlatlng = place.getLatLng();

            String[] parts;
            String part1, part2;
            parts = mlatlng.toString().split(":");
            part1 = parts[0];
            part2 = parts[1];

            String[] str1 = part2.split(",");
            String s1 = str1[0];
            String s2 = str1[1];

            String[] s3 = s1.split("\\(");
            String[] s4 = s2.split("\\)");

            LatLng India = new LatLng(Double.parseDouble(s3[1]), Double.parseDouble(s4[0]));
            // mMap.addMarker(new MarkerOptions().position(India).title("Marker in " + curlocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(India));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            mMap.getMaxZoomLevel();

        }

    };*/

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        checkGpsIsOn();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void stopAutoManage() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.stopAutoManage(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Tag", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {

            //GlobalClass.printLog(getActivity(), "-------------------else:-------");
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //googleMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f));

            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    new ReverseGeocodingTask().execute(mMap.getCameraPosition().target);
                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


    }

    @Override
    public void onLocationChanged(Location loc) {
        // TODO Auto-generated method stub
        if (loc == null)
            return;
        //Toast.makeText(getActivity(),"On Lat: "+loc.getLatitude()+"\n On Lng: "+loc.getLongitude(),Toast.LENGTH_SHORT).show();

      /* if (markerCurre == null) {
            markerCurre = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.droplocation_icon))
                    .title("Your Current Position")
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude())));*/
        if (counter) {
            counter = false;/*
            editor.putString("diary_txt_lat", latitude);
            editor.putString("diary_txt_lon", longitude);*/

            //Show Current Location of Device

            if (lat != 0 && lng != 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16.0f));
            }


        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);  // LocationListener
            //mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        } else {
            //checkGps();
            Log.i("Location","Location is Null");
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        //mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        double _latitude, _longitude;

        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            _latitude = params[0].latitude;
            _longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(_latitude, _longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                addressText = returnedAddress.getAddressLine(0);
            }
            //..... Old Method
//            if (addresses != null && addresses.size() > 0) {
//                Address returnedAddress = addresses.get(0);
//                String country = returnedAddress.getCountryName();
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    if (returnedAddress.getMaxAddressLineIndex() == (i - 1)) {
//                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
//                    } else {
//                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
//                    }
//                }
//                addressText = strReturnedAddress.toString() + country;
//                // Log.w("My Current loction address", "" + strReturnedAddress.toString());
//            }

            return addressText + ":" + _latitude + ":" + _longitude;
        }

        @Override
        protected void onPostExecute(String addressText) {
            //final String result=addressText;
            String parts[];
            parts = addressText.split(":");
            final String result = parts[0];
            latitude = parts[1];
            longitude = parts[2];
            try {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result.equals(""))
                            tvAddress.setText("getting address...");
                        //GlobalClass.printLog(getActivity(), "-----result-----------------" + result);
                        tvAddress.setText(result);
                    }
                });
            } catch (Exception e) {
                Log.e("Uri_Exception ", e.toString());
            }

        }
    }//ReverseGeocodingTask class

    private void checkGpsIsOn() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        if (mGoogleApiClient != null) {
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }//checkGpsIsOn


   /* public void checkGps() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.gps_dialog);
        dialog.setTitle("GPS");
        dialog.setCancelable(false);


        Button gpsSettings = (Button) dialog.findViewById(R.id.gps_sett_button);
        gpsSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });

        Button gpsCancel = (Button) dialog.findViewById(R.id.gps_cancel_button);
        gpsCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onStop();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new DiaryentryFragment(), "ok").commit();
                //finish();
            }
        });
        dialog.show();
    }*///checkGps

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
