package com.example.e_parking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_parking.adapter.ParkingAdapter;
import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.model.User;
import com.example.e_parking.other.Const;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ParkingListActivity extends AppCompatActivity {
    private static final String TAG = "ParkingListActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    RecyclerView recyclerView;
    Context context;
    ArrayList<HashMap<String, ParkingPlace>> parkings;
    ArrayList<ParkingPlace> parkingList;
    private boolean isOwner;
    //private FirebaseRecyclerAdapter<ParkingPlace, ParkingAdapter.ParkingViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        context=this;
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        readParkingPlace();
        checkUser();
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
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
                           invalidateOptionsMenu();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    private void readParkingPlace() {
       FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Const.PARKING).child(FirebaseAuth.getInstance().getUid());
       /*  FirebaseRecyclerOptions<ParkingPlace> options =
                new FirebaseRecyclerOptions.Builder<ParkingPlace>()
                        .setQuery(myRef, ParkingPlace.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<ParkingPlace, ParkingAdapter.ParkingViewHolder>(options) {
            @Override
            public ParkingAdapter.ParkingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_row_item, parent, false);
                return new ParkingAdapter.ParkingViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ParkingAdapter.ParkingViewHolder holder, final int position, ParkingPlace parkingPlace) {
               // HashMap<String, ParkingPlace> hashMap = parkings.get(position);
               // final String key = hashMap.keySet().iterator().next();
              //  final ParkingPlace parkingPlace = hashMap.get(key);
              holder.tvTitle.setText(parkingPlace.getTitle());
                holder.tvAddress.setText(parkingPlace.getAddress());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //listener.onParkingClick(key,parkingPlace);
                    }
                });
                holder.btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //listener.onGalleryClick(key,parkingPlace);
                    }
                });
            }

            @Override
            public void onError(DatabaseError e) {
                // Called when there is an error getting data. You may want to update
                // your UI to display an error message to the user.
                // ...
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG,e.toString());
            }
        };
        recyclerView.setAdapter(adapter);*/
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
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        ParkingAdapter adapter=new ParkingAdapter(parkings, new ParkingAdapter.OnParkingClickListener() {
            @Override
            public void onParkingClick(String key, ParkingPlace parkingPlace) {
                Intent intent=new Intent(context,MapsActivity.class);
                intent.putExtra(Const.PARKING,parkingPlace);
                startActivity(intent);
            }

            @Override
            public void onGalleryClick(String key, ParkingPlace parkingPlace) {
                Intent intent=new Intent(context,GalleryActivity.class);
                intent.putExtra(Const.KEY,key);
                startActivity(intent);
            }

            @Override
            public void onParkingLocation(String key, ParkingPlace parkingPlace) {

            }

            @Override
            public void onParkingRemove(String key, ParkingPlace parkingPlace) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parking_menu, menu);
        if(isOwner){

        }else{
            MenuItem item = menu.findItem(R.id.action_new);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_all){
            Intent intent=new Intent(this, MapsActivity.class);
            intent.putParcelableArrayListExtra(Const.PARKING,parkingList);
            startActivity(intent);
        }else if(item.getItemId()==R.id.action_new){
            Intent intent=new Intent(this, NewParkingPlaceActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                break;
        }
    }
}
