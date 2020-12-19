package com.example.e_parking.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.other.Const;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int REQ_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    ArrayList<HashMap<String, ParkingPlace>> parkings;
    ArrayList<ParkingPlace> parkingList;
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        Bundle bundle = getArguments();
        if (bundle != null) {
            ParkingPlace parkingPlace = bundle.getParcelable(Const.PARKING);
            if (parkingPlace == null) {
                ArrayList<ParkingPlace> parkingList = bundle.getParcelableArrayList(Const.PARKING);
                for (ParkingPlace place : parkingList) {
                    String location = place.getLocation();
                    double lat = Double.parseDouble(location.split(",")[0].trim());
                    double lang = Double.parseDouble(location.split(",")[1].trim());
                    setMarker(lat,lang,place.getTitle());
                }
            } else {
                String location = parkingPlace.getLocation();
                double lat = Double.parseDouble(location.split(",")[0].trim());
                double lang = Double.parseDouble(location.split(",")[1].trim());
                setMarker(lat,lang,parkingPlace.getTitle());
            }
        }else{
            readParking();
        }
        setMyLocation();

    }

    private void readParking() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Const.PARKING).child(FirebaseAuth.getInstance().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkings=new ArrayList<>();
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                parkingList=new ArrayList<>();
                for (DataSnapshot ds :iterable) {
                    String key=ds.getKey();
                    ParkingPlace parkingPlace=ds.getValue(ParkingPlace.class);
                    HashMap<String,ParkingPlace> hashMap=new HashMap<>();
                    hashMap.put(key,parkingPlace);
                    parkings.add(hashMap);
                    parkingList.add(parkingPlace);
                    String location = parkingPlace.getLocation();
                    double lat = Double.parseDouble(location.split(",")[0].trim());
                    double lang = Double.parseDouble(location.split(",")[1].trim());
                    Marker marker=setMarker(lat,lang,parkingPlace.getTitle());
                    marker.setTag(key);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_LOCATION);
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            setMarker(location.getLatitude(),location.getLongitude(),"My Location");
                        }
                    }
                });
    }

    private Marker setMarker(double lat, double lang, String title) {
        LatLng sydney = new LatLng(lat, lang);
        Marker marker=mMap.addMarker(new MarkerOptions().position(sydney).title(title));
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return marker;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQ_LOCATION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                setMyLocation();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag()!=null){
            String key=marker.getTag().toString();
            //Toast.makeText(getContext(), marker.getTag().toString(), Toast.LENGTH_SHORT).show();
            Bundle bundle=new Bundle();
            bundle.putString(Const.KEY,key);
            Fragment fragment=new ReservationFragment();
            fragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame,fragment)
                    .addToBackStack(MapFragment.class.getName())
                    .commit();
            return true;
        }
        return false;
    }
}
