package com.example.beachingns;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth beachingNSAuth = FirebaseAuth.getInstance();
    private ArrayList<BeachItem> beachList;
    String[] beach = {"All Beaches", "Rocky", "Sandy", "Wheelchair Accessible", "Floating Wheelchair"};
    String[] capacity = {"Any Capacity", "High", "Medium", "Low"};
    String filterBeachItem = "";
    String filterCapacityItem = "";

    public String visualWaterConditionsText;
    public String capacityText;
    public List<String> dates = new ArrayList<>();

    public String currentDate;
    public String beachName;
    ArrayAdapter<String> adapterItems;

    AutoCompleteTextView beachType; //Beach
    AutoCompleteTextView capacityVolume; //Capacity

    private SearchView beachSearchView;
    private MasterBeachListAdapter adapter;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.BeachMasterList);
        beachSearchView = findViewById(R.id.beachSearchView);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        final Button homeBtn = findViewById(R.id.HomeButton);
        final Button loginProfileBtn = findViewById(R.id.LoginButton);

        if (beachingNSAuth.getCurrentUser() != null){
            loginProfileBtn.setText("Profile");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else{
            bottomNavigationView.setVisibility(View.GONE);
        }

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

        checkDate();

        if (recyclerView == null) {
            Log.e("MainActivity", "RecyclerView is null. Check your layout file.");
            return;
        }

        beachList = new ArrayList<>();

        adapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(adapter);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_favorites:
                        // Redirect to preferredBeaches class
                        Intent favoritesIntent = new Intent(MainActivity.this, preferredBeaches.class);
                        startActivity(favoritesIntent);
                        return true;
                    case R.id.nav_sign_out:
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        beachingNSAuth.signOut();
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Sign Out was successful", Toast.LENGTH_LONG).show();
                        return true;
                    // other cases
                }
                return false;
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        loginProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beachingNSAuth.getCurrentUser() != null){
                    Intent profileIntent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(profileIntent);
                } else {
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(loginIntent);
                }
            }
        });

    }

    private void setupSearchFunctionality(){
        beachSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

    }

    private void performSearch(String query) {
        ArrayList<BeachItem> filteredList = new ArrayList<>();
        for (BeachItem item : beachList) {
            // Convert both to lowercase to make the search case-insensitive
            String itemNameLower = item.getName().toLowerCase();
            String itemTerrainLower = item.getsandyOrRocky().toLowerCase();
            String itemWheelChairInfoinLower = item.getwheelchairAccess().toLowerCase();
            String itemFloatingWheelChairInfoinLower = item.getFloatingWheelchair().toLowerCase();


            String queryLower = query.toLowerCase();

            // Check if the name or terrain matches the query
            if (itemNameLower.contains(queryLower) || itemTerrainLower.contains(queryLower) || itemWheelChairInfoinLower.contains(queryLower) || itemFloatingWheelChairInfoinLower.contains(queryLower)) {
                filteredList.add(item);
            }
        }
        Log.d(TAG, "Beach Result Found: " + filteredList.size() + " results"); // Log the number of results found
        adapter.updateList(filteredList);


        if (filteredList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDbAndShowOnUI();


    }


    private void checkDate() {

        db.collection("survey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<String> list = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    if(list.contains(currentDate)){
                        Log.d("ResetDataforToday","yes contains");

                    }else{
                        Log.d("ResetDataforToday","no does not. ");
                        resetDataForToday();
                    }
                    Log.d("printDocs", list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    private void resetDataForToday(){
        Log.d("StartReset","yes");

        Map<String, Object> resetText = new HashMap<>();

        resetText.put("beachCapacityTextForTheDay", "Beach Capacity: No data today!");
        resetText.put("beachVisualWaveConditionsTextForTheDay", "Visual Water Conditions: No data today!");

        db.collection("beach").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        Log.d("resetting","name: "+document.getId());

                        document.getReference().update(resetText);

                    }
                } else {
                    Log.w(TAG, "Error resetingData", task.getException());
                    Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void getDataFromDbAndShowOnUI() {
        // to toggle between the "deleted posts" and active posts button
        // resetToggle();
        Log.d(TAG, "beachList size: " + beachList.size());


        db.collection("beach")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String DataName =  document.getData().get("name").toString();
                                beachName = DataName;
                                String beachCapacityTextForTheDay ="";
                                String beachVisualWaveConditionsTextForTheDay = "";
                                if(!(document.getData().get("beachCapacityTextForTheDay")==null)) {
                                    beachCapacityTextForTheDay = document.getData().get("beachCapacityTextForTheDay").toString();
                                }else{
                                    beachCapacityTextForTheDay="Beach Capacity: No data today!";
                                }
                                if(!(document.getData().get("beachVisualWaveConditionsTextForTheDay")==null)) {
                                    beachVisualWaveConditionsTextForTheDay = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();
                                }else{
                                    beachVisualWaveConditionsTextForTheDay ="Water Conditions: No data today!";
                                }
                                Object DataImage  = document.getData().get("image");
                                String DataImageValue;
                                if(DataImage == null){
                                    DataImageValue = "imageNotFound";
                                }else {
                                    DataImageValue = document.getData().get("image").toString();
                                }
                                String recyclerViewWheelchairAccessValue="";
                                String recyclerViewSandyOrRockyValue="";
                                String recyclerViewFloatingWheelchairValue="";
                                if(document.exists()){
                                    if(document.get("sandyOrRocky")!=null){
                                        recyclerViewSandyOrRockyValue = document.get("sandyOrRocky").toString();
                                    }else{
                                        recyclerViewSandyOrRockyValue = "";
                                    }
                                    if(document.get("wheelchairAccessible")!=null){
                                        recyclerViewWheelchairAccessValue = document.get("wheelchairAccessible").toString();
                                    }else{
                                        recyclerViewWheelchairAccessValue = "";
                                    }
                                    if(document.get("floatingWheelchair")!=null){
                                        recyclerViewFloatingWheelchairValue = document.get("floatingWheelchair").toString();
                                    }else{
                                        recyclerViewFloatingWheelchairValue = "";
                                    }
                                }

                                retrieveAdditionalDataFromDB();

                                Log.d("PrintingHere","BeachName: "+DataName + " capacity: "+beachCapacityTextForTheDay +  " visualWaterConditions: " +beachVisualWaveConditionsTextForTheDay);
                                BeachItem beachItem = new BeachItem(DataName,DataImageValue,beachCapacityTextForTheDay,
                                        beachVisualWaveConditionsTextForTheDay,recyclerViewWheelchairAccessValue,recyclerViewSandyOrRockyValue,recyclerViewFloatingWheelchairValue);
                                //beachItemArrayList.add(beachItem);
                                Log.d("beachdetails:","wheelchair: "+beachItem.getwheelchairAccess() + " floating: "+beachItem.getFloatingWheelchair() +" sandy or rocky: "+beachItem.getsandyOrRocky());
                                Log.d("FilterItem:","filterItem:"+filterCapacityItem);


                                if (Objects.equals(filterBeachItem, "") || Objects.equals(beachItem.getsandyOrRocky(), filterBeachItem) || Objects.equals(beachItem.getwheelchairAccess(), filterBeachItem) || Objects.equals(beachItem.getFloatingWheelchair(), filterBeachItem)) {
                                    if (Objects.equals(filterCapacityItem, "") || Objects.equals(beachItem.getcapacity(), filterCapacityItem)) {
                                        beachList.add(beachItem);
                                    }
                                }

                            }
                            Collections.reverse(beachList);
                            loadMasterBeachList();
                            setupSearchFunctionality();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                        }
                    }
                });




        //filters
        beachType = findViewById(R.id.auto_complete_textview);
        adapterItems = new ArrayAdapter<String>(this, R.layout.beach_list, beach);
        beachType.setAdapter(adapterItems);

        beachType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String beachItem = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, beachItem + " option has been selected", Toast.LENGTH_SHORT).show();
                beachList.clear();
                if (beachItem.equals("All Beaches")){
                    filterBeachItem = "";
                } else {
                    filterBeachItem = beachItem;
                }
                getDataFromDbAndShowOnUI();
            }

        });

        //Capacity
        capacityVolume = findViewById(R.id.auto_complete_textview2);

        ArrayAdapter<String> adapterItems2; //For Capacity
        adapterItems2 = new ArrayAdapter<String>(this, R.layout.capacy_list, capacity);
        capacityVolume.setAdapter(adapterItems2);

        // Capacity
        capacityVolume.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //dropdown item
                String capacityItem = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, capacityItem, Toast.LENGTH_SHORT).show();
                beachList.clear();
                if (capacityItem.equals("Any Capacity")) {
                    filterCapacityItem = "";
                }
                else {
                    filterCapacityItem = "Beach Capacity: "+ capacityItem + " Capacity";
                }
                getDataFromDbAndShowOnUI();
            }
        }));
    }

    private void retrieveAdditionalDataFromDB(){
        DocumentReference landingBeachRef = db.collection("survey").document(currentDate).collection(beachName).document(currentDate);
        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!(document.getData().get("beachCapacityTextForTheDay")==null))
                            capacityText  =document.getData().get("beachCapacityTextForTheDay").toString();
                        if(!(document.getData().get("beachVisualWaveConditionsTextForTheDay")==null))
                            visualWaterConditionsText  = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();




                        // showDataOnUI();
                    } else {
                        Log.d("Beach Landing Query", "No such document");
                        // showDataOnUI();
                    }
                } else {
                    Log.d("Beach Landing Query", "get failed with ", task.getException());
                }
            }
        });
    }

    private void loadMasterBeachList() {
        createRecyclerView(beachList);
    }

    /**
     * creates the Recycler view for all my task posts
     * @param beachList list of all my posts
     */
    public void createRecyclerView(ArrayList<BeachItem> beachList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BeachMasterList);

        // using a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(mAdapter);
    }
}
