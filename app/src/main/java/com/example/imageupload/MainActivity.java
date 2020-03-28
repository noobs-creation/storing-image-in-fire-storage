package com.example.imageupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int ImageBack = 1;
    private StorageReference Folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
    }

    public void UploadData(View view) {
        //opening a new intent to choose image

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ImageBack);


    }
    //work to do after choosing image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageBack){
            if (resultCode == RESULT_OK){
                Uri ImageData = data.getData();


                //setting up image name for the chosen image
                final StorageReference ImageName = Folder.child("image"+ImageData.getLastPathSegment());
                //sending image to storage
                ImageName.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                        //getting the url to retrieve the same image and storing it in db
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //name of the header under which images would be stored
                                DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference().child("image");

                                //using hashmap to store image url with "iamge url" as the header

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("imageurl", String.valueOf(uri));

                                //confirming the putting of data
                                imagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "finally stored in database", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                    //confirming if the image storing in storage is failed
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "not uploaded", Toast.LENGTH_LONG).show();
                    }
                });


            }
        }


    }
}
