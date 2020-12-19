package com.example.e_parking.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.other.Const;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        ParkingPlace parkingPlace=getIntent().getParcelableExtra(Const.PARKING);
        if(parkingPlace==null){
            ArrayList<ParkingPlace> parkingList=getIntent().getParcelableArrayListExtra(Const.PARKING);
            for (ParkingPlace place :  parkingList) {
                setMarker(place);
            }
        }else {
            setMarker(parkingPlace);
        }
    }

    private void setMarker(ParkingPlace parkingPlace) {
        String location = parkingPlace.getLocation();
        double lat = Double.parseDouble(location.split(",")[0].trim());
        double lang = Double.parseDouble(location.split(",")[1].trim());
        LatLng sydney = new LatLng(lat, lang);
        mMap.addMarker(new MarkerOptions().position(sydney).title(parkingPlace.getTitle()));
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
