package com.example.instalearn.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.instalearn.HomeActivity;
import com.example.instalearn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;

public class InsertPostActivity extends AppCompatActivity {

    ImageView postImageView;
    EditText postCaptionView;
    Button postInsertButton;
    String caption;
    Uri imageUri;
    DatabaseReference postRef;
    private StorageReference storageReference,filepath;
    private String generatedFilepath;
    FirebaseAuth mAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_post_activitty);

        postCaptionView=findViewById(R.id.insert_post_caption);
        postImageView=findViewById(R.id.insert_post_image);
        postInsertButton=findViewById(R.id.insert_post_btn);

        imageUri= Uri.parse(getIntent().getStringExtra("imageUri"));

        if (imageUri!=null){
            Picasso.get().load(imageUri).into(postImageView);
        }

        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();


        postInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                  uploadPicture();
                }
            }
        });

    }

    private void uploadPicture() {
        filepath = storageReference.child("Posts").child(Calendar.getInstance().getTime() + ".jpeg");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                getDownloadUrl(filepath);

            }
        });
    }

    private void getDownloadUrl(StorageReference filepath) {
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                            generatedFilepath=uri.toString();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                            insertPost(generatedFilepath);
                }
            });
    }

    private void insertPost(String generatedFilepath) {



        HashMap<String,Object> postMap=new HashMap<>();
        postMap.put("post",generatedFilepath);
        postMap.put("caption",caption);
        postMap.put("user",userId);

            postRef.push().setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent =new Intent(InsertPostActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
    }

    private boolean validation() {
        boolean isValid=true;
        caption=postCaptionView.getText().toString();

        if (TextUtils.isEmpty(caption)){
            isValid=false;
            postCaptionView.setError("Please enter a caption");
        }
        return  isValid;
    }
}