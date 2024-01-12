package com.example.beachingns;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class Registration extends AppCompatActivity {

    EditText userNameText, passwordFieldText, fullNameText, emailAddressText;
    FirebaseFirestore beachBluenoserDB;
    private FirebaseAuth beachBluenoserAuth;
    Button registerBtn;
    String userName, emailAddress, fullName, passwordValue, userID;
    ImageButton backArrowkey;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userNameText = findViewById(R.id.registerUsernameTxt);
        passwordFieldText = findViewById(R.id.registerPasswordTxt);
        emailAddressText = findViewById(R.id.registerEmailAddressTxt);
        fullNameText = findViewById(R.id.registerFullNameTxt);
        registerBtn = findViewById(R.id.signUpBtn);
        backArrowkey = findViewById(R.id.backArrow);

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();

        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = userNameText.getText().toString();
                fullName = fullNameText.getText().toString();
                emailAddress = emailAddressText.getText().toString().trim();
                passwordValue = passwordFieldText.getText().toString().trim();

                if (!validateInputs(userName, fullName, emailAddress, passwordValue)) return;


                beachBluenoserAuth.createUserWithEmailAndPassword(emailAddress, passwordValue).addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "User Created.", Toast.LENGTH_SHORT).show();
                        userID = Objects.requireNonNull(beachBluenoserAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = beachBluenoserDB.collection("BNSUSERSTABLE-PROD").document(userID);

/*
                        User user = new User(username,fullname,email,hashedPassword);
*/

                        Map<String, Object> user = new HashMap<>();
                        user.put("Fullname", fullName);
                        user.put("Email", emailAddress);
                        user.put("Username", userName);
                        user.put("Password", passwordValue);
                        user.put("userType", "User");

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                            }
                        });
                        startActivity(new Intent(Registration.this, MainActivity.class));
                    } else {
                        Toast.makeText(Registration.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show(); //example - It will show an error if email already exists
                    }
                });
            }
        });
    }

    private boolean validateInputs(String username, String fullname, String email, String password) {
        if (TextUtils.isEmpty(username)) {
            userNameText.setError("Please Enter a Username!");
            return false;
        } else if (!username.matches("[a-zA-Z0-9]+")) {
            userNameText.setError("Username can contain only letters and numbers!");
            return false;
        }

        if (TextUtils.isEmpty(fullname) || !fullname.matches("[a-zA-Z ]+")) {
            fullNameText.setError("Full name must contain only letters and a space between first name and last name.");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailAddressText.setError("Please Enter an Email!");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]{2,}")) {
            emailAddressText.setError("Email is Invalid. Please enter a valid email address (e.g., user@example.com)");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordFieldText.setError("Please Enter a Password!");
            return false;
        } else if (!(password.length() >= 8 && password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*"))) {
            passwordFieldText.setError("Password needs to be more than 8 characters and a mix of alphabets and numbers!");
            return false;
        }

        return true;
    }


}
