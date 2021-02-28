package com.example.instalearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.ActivityOptions;
import android.content.Intent;

import android.os.Bundle;

import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.instalearn.Activities.MenuActivity;
import com.example.instalearn.Activities.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class HomeActivity extends AppCompatActivity {

    NavController navController;
    BottomNavigationView bottomNavigationView;
    ImageView profileImage;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    String userImage;
    ImageButton menuBtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navController= Navigation.findNavController(this,R.id.home_nav_host);
        bottomNavigationView=findViewById(R.id.home_bottom_nav);
        profileImage=findViewById(R.id.home_user_picture);
        menuBtn=findViewById(R.id.menuBtn);
        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");





        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("image",userImage);
                startActivity(intent);
            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent=new Intent(HomeActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("image")){
                    userImage=snapshot.child("image").getValue().toString();
                    Picasso.get().load(userImage).into(profileImage);
                }
                else{
                    //do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}