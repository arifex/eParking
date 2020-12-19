package com.example.e_parking.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.adapter.GalleryAdapter;
import com.example.e_parking.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";
    Context context;
    private static final int REQ_CAMERA_IMAGE = 100;
    private Bitmap bitmap;
    ProgressDialog pd;
    private String key;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String,String>> galleries;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        context=this;
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        key=getIntent().getStringExtra(Const.KEY);
        readGallery();
    }

    private void readGallery() {
        myRef=FirebaseDatabase.getInstance()
                .getReference().child(Const.GALLERY)
                .child(FirebaseAuth.getInstance().getUid())
                .child(key);

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        galleries=new ArrayList<>();
                        for (DataSnapshot ds :dataSnapshot.getChildren()) {
                            String key=ds.getKey();
                            String name=ds.getValue(String.class);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(key,name);
                            galleries.add(hashMap);
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
        GalleryAdapter adapter=new GalleryAdapter(context, galleries, new GalleryAdapter.OnGalleryClickListener() {
            @Override
            public void onGalleryRemove(String key, String name) {
                new AlertDialog.Builder(context)
                        .setTitle("Remove Photo")
                        .setMessage("Are you sure want to remove photo?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeGallery(key);
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

    private void removeGallery(String key) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_new){
            showImagePickerDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImagePickerDialog() {
        String[] imageUploadOptions={"Image From Gallery","Image From Camera"};
        new AlertDialog.Builder(context)
                .setItems(imageUploadOptions, (dialog, which) -> {
                    switch (which){
                        case 0:
                            pickImagesFromSource();
                            break;
                        case 1:
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQ_CAMERA_IMAGE);
                            break;

                    }
                })
                .create()
                .show();
    }

    private void pickImagesFromSource() {
        RxImagePicker.with(context).requestImage(Sources.GALLERY)
                .flatMap(new Function<Uri, ObservableSource<Bitmap>>() {
                    @Override
                    public ObservableSource<Bitmap> apply(@NonNull Uri uri) throws Exception {
                        return RxImageConverters.uriToBitmap(context, uri);
                    }
                }).subscribe(new Consumer<Bitmap>() {
            @Override
            public void accept(@NonNull Bitmap bmp) throws Exception {
                // Do something with Bitmap
                bitmap=bmp;
                uploadImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CAMERA_IMAGE && resultCode==RESULT_OK){
            //UtilityHelper.showLoading(context);
            //Uri filePath = Uri.parse(UtilityHelper.getOriginalImagePath(getActivity()));
            bitmap=(Bitmap) data.getExtras().get("data");
            //Uri filePath=saveImage(bitmap);
            uploadImage();
        }
    }

    private void uploadImage() {

            if(bitmap==null){
                Toast.makeText(context, "Please Choose Image First!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            String fileName=System.currentTimeMillis()+"";
            StorageReference riversRef = FirebaseStorage.getInstance()
                    .getReference().child(Const.IMAGES+"/"+fileName+".jpg");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            pd= ProgressDialog.show(context,"","");
            riversRef.putBytes(byteArray)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            saveOtherDetails(fileName);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            pd.dismiss();
                            Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    private void saveOtherDetails(String imageFileName) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child(Const.GALLERY)
                .child(FirebaseAuth.getInstance().getUid())
                .child(key).push();
        pd=ProgressDialog.show(context,"","");
        reference.setValue(imageFileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
