package com.example.instalearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.auth.FirebaseAuth;

public class FlashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);


        // firebase components
        mAuth=FirebaseAuth.getInstance();

      Handler handler=new Handler();
      handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              if (mAuth.getCurrentUser()!=null){

                  Intent intent=new Intent(FlashActivity.this,HomeActivity.class);
                  startActivity(intent);
                  finish();

              }
              else{
                  Intent intent=new Intent(FlashActivity.this,LoginActivity.class);
                  startActivity(intent);
                  finish();
              }

          }
      },2000);



    }
}