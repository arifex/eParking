package com.example.e_parking.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.activities.GalleryActivity;
import com.example.e_parking.activities.MapsActivity;
import com.example.e_parking.activities.NewParkingPlaceActivity;
import com.example.e_parking.adapter.ParkingAdapter;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.model.User;
import com.example.e_parking.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class ParkingListFragment extends Fragment {

    private static final String TAG = "ParkingListActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    RecyclerView recyclerView;
    Context context;
    ArrayList<HashMap<String, ParkingPlace>> parkings;
    ArrayList<ParkingPlace> parkingList;
    private boolean isOwner;
    private ProgressDialog pd;
    private DatabaseReference myRef;

    public ParkingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        context=getContext();
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        readParkingPlace();
        checkUser();
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void checkUser() {
        FirebaseDatabase.getInstance().getReference(Const.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        isOwner=!user.getUserType().equalsIgnoreCase(Const.USER);
                        ActivityCompat.invalidateOptionsMenu(getActivity());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void readParkingPlace() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Const.PARKING).child(FirebaseAuth.getInstance().getUid());
        pd= ProgressDialog.show(context,"","");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkings=new ArrayList<>();
                pd.dismiss();
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                parkingList=new ArrayList<>();
                for (DataSnapshot ds :iterable) {
                    String key=ds.getKey();
                    ParkingPlace parkingPlace=ds.getValue(ParkingPlace.class);
                    HashMap<String,ParkingPlace> hashMap=new HashMap<>();
                    hashMap.put(key,parkingPlace);
                    parkings.add(hashMap);
                    parkingList.add(parkingPlace);
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        ParkingAdapter adapter=new ParkingAdapter(parkings, new ParkingAdapter.OnParkingClickListener() {
            @Override
            public void onParkingClick(String key, ParkingPlace parkingPlace) {
                /*Intent intent=new Intent(context, MapsActivity.class);
                intent.putExtra(Const.PARKING,parkingPlace);
                startActivity(intent);*/
                Fragment fragment=new NewParkingFragment();
                Bundle bundle=new Bundle();
                bundle.putString(Const.KEY,key);
                bundle.putParcelable(Const.PARKING,parkingPlace);
                fragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame,fragment)
                        .addToBackStack(ParkingListFragment.class.getName())
                        .commit();
            }

            @Override
            public void onGalleryClick(String key, ParkingPlace parkingPlace) {
                Intent intent=new Intent(context, GalleryActivity.class);
                intent.putExtra(Const.KEY,key);
                startActivity(intent);
            }

            @Override
            public void onParkingLocation(String key, ParkingPlace parkingPlace) {
                Fragment fragment=new MapFragment();
                Bundle bundle=new Bundle();
                bundle.putParcelable(Const.PARKING,parkingPlace);
                fragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame,fragment)
                        .commit();
            }

            @Override
            public void onParkingRemove(String key, ParkingPlace parkingPlace) {
                new AlertDialog.Builder(context)
                        .setTitle("Remove Parking")
                        .setMessage("Are you sure want to remove parking?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeParking(key);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void removeParking(String key) {
        pd= ProgressDialog.show(context,"","");
        myRef.child(key).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.i(TAG,task.getException().toString());
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.parking_menu, menu);
        if(isOwner){

        }else{
            MenuItem item = menu.findItem(R.id.action_new);
            item.setVisible(false);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment=null;
        if(item.getItemId()==R.id.action_all){
           /* Intent intent=new Intent(this, MapsActivity.class);
            intent.putParcelableArrayListExtra(Const.PARKING,parkingList);
            startActivity(intent);*/
           //Bundle bundle=new Bundle();
           //bundle.putParcelableArrayList(Const.PARKING,parkingList);
           fragment=new MapFragment();
           //fragment.setArguments(bundle);

        }else if(item.getItemId()==R.id.action_new){
            /*Intent intent=new Intent(this, NewParkingPlaceActivity.class);
            startActivity(intent);*/
            fragment=new NewParkingFragment();
        }
        if(fragment!=null){
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame,fragment)
                    .addToBackStack(ParkingListFragment.class.getName())
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                break;
        }
    }
}
