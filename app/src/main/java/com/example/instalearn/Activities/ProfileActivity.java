package com.example.instalearn.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instalearn.Model.Posts;
import com.example.instalearn.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    Button saveBtn;
    private Uri imageUri=null;
    private StorageReference storageReference,filepath;
    private String generatedFilepath;
    private DatabaseReference userRef,postRef;
    FirebaseAuth mAuth;
    String image;
    RecyclerView postList;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerOptions<Posts> Options;
    FirebaseRecyclerAdapter<Posts,ProfilePostViewHolder> adapter;
    String post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        image=getIntent().getStringExtra("image");


        profileImage=findViewById(R.id.profile_pic);
        saveBtn=findViewById(R.id.profile_save);
        storageReference= FirebaseStorage.getInstance().getReference();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth=FirebaseAuth.getInstance();
        postList=findViewById(R.id.profile_post_list);

      //  linearLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        linearLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false);

        /* linearLayoutManager=new LinearLayoutManager(this);*/
        postList.setHasFixedSize(true);
        postList.setLayoutManager(linearLayoutManager);

        if (image!=null){
            Picasso.get().load(image).into(profileImage);
        }


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filepath = storageReference.child("Profile Pictures").child(Calendar.getInstance().getTime() + ".jpeg");
                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if (task.isSuccessful()) {
                            getDownloadUrl(filepath);
                        } else {

                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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

                if (task.isSuccessful()){

                    HashMap<String, Object> pictureMap=new HashMap<>();
                    pictureMap.put("image",generatedFilepath);

                    userRef.child(mAuth.getCurrentUser().getUid()).updateChildren(pictureMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });

                }

            }
        });

    }

    private void openGallery() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), 1);
    }


    @Override
    protected void onStart() {
        super.onStart();

        Options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postRef,Posts.class).build();
        adapter=new FirebaseRecyclerAdapter<Posts, ProfilePostViewHolder>(Options) {
            @Override
            protected void onBindViewHolder(@NonNull ProfilePostViewHolder holder, int position, @NonNull Posts model) {

                post=model.getPost();
                Picasso.get().load(post).placeholder(R.drawable.placeholder).into(holder.posts);

            }

            @NonNull
            @Override
            public ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_profile,parent,false);
                return new ProfilePostViewHolder(view);
            }
        };
        adapter.startListening();
        postList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        imageUri = data.getData();


        if (imageUri != null) {

            Picasso.get().load(imageUri).into(profileImage);


        }



    }

    private class ProfilePostViewHolder extends RecyclerView.ViewHolder {
        ImageView posts;
        public ProfilePostViewHolder(@NonNull View itemView) {
            super(itemView);
            posts=itemView.findViewById(R.id.profile_post_item);
        }
    }
}