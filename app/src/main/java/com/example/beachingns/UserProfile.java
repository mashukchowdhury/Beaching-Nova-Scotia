package com.example.beachingns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth beachBluenoserAuth = FirebaseAuth.getInstance();
    public Button prefBtn, signOutBtn;
    public TextView Email, username, FullName;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        prefBtn = findViewById(R.id.preferredBeachListButton);
        signOutBtn = findViewById(R.id.SignOut);
        Email = findViewById(R.id.EmailTextView);
        FullName = findViewById(R.id.fullNameTextView);
        username = findViewById(R.id.usernameTextView);

        //Header Code
        final Button homeBtn = findViewById(R.id.HomeButton);
        final Button loginProfileBtn = findViewById(R.id.LoginButton);
        if (beachBluenoserAuth.getCurrentUser() != null){
            loginProfileBtn.setText("Profile");
        }
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });
        loginProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beachBluenoserAuth.getCurrentUser() != null) {
                    Intent profileIntent = new Intent(UserProfile.this, UserProfile.class);
                    startActivity(profileIntent);
                }
            }
        });
        //End of Header Code


        //TODO: Not implemented edit user_profile page
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(userprofile.this, editprofile.class);
//                startActivity(intent);
//            }
//        });

        prefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, preferredBeaches.class);
                startActivity(intent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                beachBluenoserAuth.signOut();
                startActivity(intent);
                Toast.makeText(UserProfile.this, "Sign Out was successful", Toast.LENGTH_LONG).show();

            }
        });
        String userID = beachBluenoserAuth.getCurrentUser().getUid();
        DocumentReference Ref = db.collection("BNSUSERSTABLE-PROD").document(userID);
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String email = documentSnapshot.getData().get("Email").toString();
                        Email.setText(email);
                        String name = documentSnapshot.getData().get("Fullname").toString();
                        FullName.setText(name);
                        String user = documentSnapshot.getData().get("Username").toString();
                        username.setText(user);
                    }
                }
            }
        });

    }
}
