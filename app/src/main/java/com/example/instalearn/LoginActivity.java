package com.example.instalearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email,password,name;
    ExtendedFloatingActionButton loginBtn;
    String str_email,str_password,str_token,str_name,str_image;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        loginBtn=findViewById(R.id.loginBtn);
        name=findViewById(R.id.login_name);
        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                 mAuth.createUserWithEmailAndPassword(str_email,str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser user=task.getResult().getUser();


                                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                    str_token=task.getResult().getToken();

                                            HashMap<String,Object> userMap=new HashMap<>();
                                            userMap.put("name",str_name);
                                            userMap.put("token",str_token);
                                            userMap.put("email",str_email);



                                            userRef.child(mAuth.getCurrentUser().getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                }
                                            });


                                        }
                                    });

                                }
                                else{
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                     }
                 });
                }
            }
        });



    }

    private boolean validation() {
        boolean isValid=true;
        str_email=email.getText().toString();
        str_password=password.getText().toString();
        str_name=name.getText().toString();

        if (TextUtils.isEmpty(str_email)){
            isValid=false;
        }
        if (TextUtils.isEmpty(str_password)){
            isValid=false;
        }
        if (TextUtils.isEmpty(str_name)){
            isValid=false;
        }


        return isValid;
    }
}