package com.example.beachingns;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beachingns.BeachItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class preferredBeaches extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MasterBeachListAdapter adapter;
    private ArrayList<BeachItem> beachList;

    private Button homeBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_beaches); // Set your layout here

        recyclerView = findViewById(R.id.recyclerViewPreferredBeaches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeBtn = findViewById(R.id.HomeButton);
        beachList = new ArrayList<>();
        adapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(adapter);

        if (auth.getCurrentUser() != null) {
            loadPreferredBeaches(auth.getCurrentUser().getUid());
        }else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(preferredBeaches.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void loadPreferredBeaches(String userID) {
        db.collection("BNSUSERSTABLE-PROD").document(userID).collection("preferredBeaches")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        beachList.clear();
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                BeachItem beach = document.toObject(BeachItem.class);
                                beachList.add(beach);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // No preferred beaches found
                            Toast.makeText(this, "No preferred beaches found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Database access error
                        Toast.makeText(this, "Error loading preferred beaches", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


