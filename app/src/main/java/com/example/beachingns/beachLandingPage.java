package com.example.beachingns;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class beachLandingPage extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String beachName, parsedBeachName;
    public String landingBeachCapacityText;
    public String landingBeachSandyOrRockyText;
    public String landingBeachWheelchairAccessibleText;
    public String landingBeachImageSource;
    public String landingBeachVisualWaterConditionsText;
    public String landingBeachParkingText;
    public String landingBeachFloatingWheelchairText;
    public String weatherDescriptionText;
    public String temperatureText;

    public ImageView landingBeachImageView;
    public TextView landingBeachCapacityView;
    public TextView landingBeachSandyOrRockyView;
    public TextView landingBeachWheelchairAccessibleView;
    public TextView landingBeachFloatingWheelchairView;
    public TextView landingBeachParkingView;
    public TextView landingBeachNameView;
    public TextView landingBeachVisualWaterConditionsView;
    public TextView weatherView;
    public TextView weatherDescriptionView;
    public TextView humidityView;
    public TextView temperatureView;
    public TextView windView;
    public TextView cloudView;

    public String currentDate;
    public String userID;
    public String userType = "";

    public Double beachLat,beachLong;
    public String beachLocation;
    public Button mapsBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);
        Bundle bundle = getIntent().getExtras();
        Button setPrefBtn = findViewById(R.id.setPreference);
        ImageButton backBtn = findViewById(R.id.backButton);

        if (bundle != null) {
            if (bundle.getString("beachName") != null) {
                beachName = bundle.getString("beachName");
                Log.d("beach Main Page Name ", " Name : " + beachName);
                parsedBeachName = beachName.replace(" ", "+");
            }
            if (bundle.getString("userType") != null) {
                userType = bundle.getString("userType");
            }
            else if (auth.getCurrentUser() != null) {
                userID = auth.getCurrentUser().getUid();
                DocumentReference userRef = db.collection("BNSUSERSTABLE-PROD").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userType = document.getData().get("userType").toString();
                                Log.d("USERTYPE ", userType);
                            }
                        }
                    }
                });
            } if (auth.getCurrentUser() != null) {
                userID = auth.getCurrentUser().getUid();
                checkIfBeachIsPreferred(beachName, userID, setPrefBtn);
            }else {
                setPrefBtn.setVisibility(View.GONE);
            }

//            //hiding the "set favorite" button from the favorites' list
//
//            boolean isFromPreferredList = bundle.getBoolean("isFromPreferredList", true);
//            if (isFromPreferredList) {
//                setPrefBtn.setVisibility(View.GONE);
//            }
        }


        getPreliminaryDataFromDB();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(beachLandingPage.this, MainActivity.class);
                startActivity(backIntent);
            }
        });

        setPrefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    String userID = auth.getCurrentUser().getUid();
                    addBeachToPreferredList(userID, beachName);
                } else {
                    Toast.makeText(beachLandingPage.this, "Please login first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mapsBtn = findViewById(R.id.mapsBtn);
        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beachLocation = beachLat + "," + beachLong;
                //https://developers.google.com/maps/documentation/urls/android-intents#location_search
                //if you want maps to launch directly into navigation switch out gmmIntentUri for below
                //Uri gmmIntentUri = Uri.parse("google.navigation:q=" + parsedBeachName + "@" + beachLocation);
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + parsedBeachName + "@" + beachLocation);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void checkIfBeachIsPreferred(String beachName, String userID, Button setPrefBtn) {
        DocumentReference preferredBeachRef = db.collection("BNSUSERSTABLE-PROD")
                .document(userID)
                .collection("preferredBeaches")
                .document(beachName);
        preferredBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Beach is in the preferred list, so hide the button
                        setPrefBtn.setVisibility(View.GONE);
                    } else {
                        // Beach is not in the preferred list, so show the button
                        setPrefBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void addBeachToPreferredList(String userID, String beachName) {
        DocumentReference userRef = db.collection("BNSUSERSTABLE-PROD").document(userID);

        Map<String, Object> beachData = new HashMap<>();
        beachData.put("name", beachName);
        beachData.put("imageSource", landingBeachImageSource+".jpg");
        beachData.put("capacity", landingBeachCapacityText);
        beachData.put("sandyOrRocky", landingBeachSandyOrRockyText);
        beachData.put("wheelchairAccessible", landingBeachWheelchairAccessibleText);
        beachData.put("floatingWheelchair", landingBeachFloatingWheelchairText);
        beachData.put("parking", landingBeachParkingText);
        beachData.put("visualWaterConditions", landingBeachVisualWaterConditionsText);

        userRef.collection("preferredBeaches").document(beachName).set(beachData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful addition here, e.g., show a confirmation message
                        Toast.makeText(beachLandingPage.this, "Beach added to favorites!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors here
                        Toast.makeText(beachLandingPage.this, "Error adding beach to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onResume() {
        getPreliminaryDataFromDB();
        super.onResume();
    }

    private void getPreliminaryDataFromDB() {
        DocumentReference landingBeachRef = db.collection("beach").document(beachName);
        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object DataImage = document.getData().get("image");
                        String DataImageValue;
                        if (DataImage == null) {
                            DataImageValue = "imageNotFound";
                        } else {
                            DataImageValue = document.getData().get("image").toString();
                        }
                        landingBeachImageSource = DataImageValue;
                    }
                    if (!(document.getData().get("beachCapacityTextForTheDay") == null)) {
                        landingBeachCapacityText = document.getData().get("beachCapacityTextForTheDay").toString();
                        Log.d("DataCheck", "Beach Capacity Status: " + landingBeachCapacityText);

                    } else {
                        landingBeachCapacityText = "Beach Capacity: No data today!";
                    }
                    if (!(document.getData().get("beachVisualWaveConditionsTextForTheDay") == null)) {
                        landingBeachVisualWaterConditionsText = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();
                    } else {
                        landingBeachVisualWaterConditionsText = "Water Conditions: No data today!";
                    }
                    if (!(document.getData().get("sandyOrRocky") == null)) {
                        landingBeachSandyOrRockyText = "Beach type: " + document.getData().get("sandyOrRocky").toString();
                    } else {
                        landingBeachSandyOrRockyText = "Beach type: Unknown";
                    }
                    if (!(document.getData().get("beachParkingConForDay") == null)) {
                        landingBeachParkingText = document.getData().get("beachParkingConForDay").toString();
                    } else {
                        landingBeachParkingText = "Parking: No data today!";
                    }
                    if (!(document.getData().get("floatingWheelchair") == null)) {
                        landingBeachFloatingWheelchairText = document.getData().get("floatingWheelchair").toString();
                        if (landingBeachFloatingWheelchairText.equals("Floating Wheelchair"))
                        { landingBeachFloatingWheelchairText="Floating Wheelchair: Yes"; }
                        else {landingBeachFloatingWheelchairText="Floating Wheelchair: No"; }
                    } else {
                        landingBeachFloatingWheelchairText = "Floating Wheelchair: Unknown";
                    }
                    if (!(document.getData().get("wheelchairAccessible") == null)) {
                        landingBeachWheelchairAccessibleText = document.getData().get("wheelchairAccessible").toString();
                        if (landingBeachWheelchairAccessibleText.equals("Wheelchair Accessible"))
                        { landingBeachWheelchairAccessibleText="Wheelchair Accessible: Yes"; }
                        else {landingBeachWheelchairAccessibleText="Wheelchair Accessible: No"; }
                    } else {
                        landingBeachWheelchairAccessibleText = "Wheelchair Accessible: Unknown";
                    }
                    if(document.get("location")!=null){
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        beachLat = geoPoint.getLatitude();
                        beachLong = geoPoint.getLongitude();
                        Log.d("Beach Location", "location : "+ beachLat +", " + beachLong);
                        beachLocation = beachLat + "," + beachLong;
                    }
                    getWeatherDetails();
                    showDataOnUI();
                } else {
                    Log.d("Beach Landing Query", "No such document");
                }
            }
        });
    }

    /**Code referenced from
     * https://github.com/sandipapps/Weather-Update
     * https://www.youtube.com/watch?v=f2oSRBwN2HY
     * */
    public void getWeatherDetails() {
        String tempUrl="https://api.openweathermap.org/data/2.5/weather?lat="+ beachLat + "&lon=" + beachLong + "&appid=895284fb2d2c50a520ea537456963d9c"; //weather api
        Log.d("Beach Location", "url : "+ tempUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Beach Location", "Response Success!");
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double tempCelsius = jsonObjectMain.getDouble("temp")- 273.15 ;
                    double tempFahrenheit = tempCelsius * 9/5 + 32;
                    double feelsLikeCelsius = jsonObjectMain.getDouble("feels_like")- 273.15 ;
                    double feelsLikeFahrenheit = feelsLikeCelsius * 9/5 + 32;
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
//                    String wind = jsonObjectWind.getString("speed");
                    double windSpeedMetersPerSecond = jsonObjectWind.getDouble("speed");
                    double windSpeedKilometersPerHour = windSpeedMetersPerSecond * 3.6;
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
//                    output += "Current weather of " + beachName
//                            + "\n Temp: " + df.format(temp) + " °C"
//                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
//                            + "\n Humidity: " + humidity + "%"
//                            + "\n Description: " + description
//                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
//                            + "\n Cloudiness: " + clouds + "%"
//                            + "\n Pressure: " + pressure + " hPa";
                    weatherDescriptionView.setText("Weather: " + description);
                    weatherView.setText("Temperature: " + Math.round(tempCelsius) + "°C/" + Math.round(tempFahrenheit) + "°F");
                    temperatureView.setText("Feels Like: " + Math.round(feelsLikeCelsius) + "°C/" + Math.round(feelsLikeFahrenheit) + "°F");
                    humidityView.setText("Humidity: " + humidity + "%");
                    windView.setText("Wind Speed: " + Math.round(windSpeedMetersPerSecond) + "m/s or " + Math.round(windSpeedKilometersPerHour) + "km/h");
                    cloudView.setText("Cloud cover: " + clouds + "%");
                    Log.d("Beach Location", "Texts: " + weatherDescriptionText +" + "+ temperatureText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void showDataOnUI() {
        landingBeachCapacityView = findViewById(R.id.landingBeachCapacityTextView);
        landingBeachSandyOrRockyView = findViewById(R.id.landingBeachSandyOrRockyTextView);
        landingBeachWheelchairAccessibleView = findViewById(R.id.landingBeachWheelchairAccessibleTextView);
        landingBeachNameView = findViewById(R.id.landingBeachNameTextView);
        landingBeachVisualWaterConditionsView = findViewById(R.id.landingBeachVisualWaterConditionsTextView);
        landingBeachFloatingWheelchairView = findViewById(R.id.landingBeachFloatingWheelchairTextView);
        landingBeachParkingView = findViewById(R.id.landingBeachParkingTextView);

        landingBeachCapacityView.setText(landingBeachCapacityText);
        landingBeachVisualWaterConditionsView.setText(landingBeachVisualWaterConditionsText);
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyText);
        landingBeachWheelchairAccessibleView.setText(landingBeachWheelchairAccessibleText);
        landingBeachFloatingWheelchairView.setText(landingBeachFloatingWheelchairText);
        landingBeachParkingView.setText(landingBeachParkingText);
        landingBeachNameView.setText(beachName);
        setBeachImage();

        weatherView = findViewById(R.id.weatherTextView);
        weatherDescriptionView = findViewById(R.id.weatherDescriptionTextView);
        humidityView = findViewById(R.id.humidityTextView);
        temperatureView = findViewById(R.id.temperatureTextView);
        windView = findViewById(R.id.windTextView);
        cloudView = findViewById(R.id.cloudsTextView);
    }

    public void setBeachImage() {

        if (landingBeachImageSource.equals("") || landingBeachImageSource == null) {
            landingBeachImageSource = "default1.jpg";
        }
        landingBeachImageSource = landingBeachImageSource.replace('-', '_');
        int fileExtension = landingBeachImageSource.indexOf('.');

        landingBeachImageSource = landingBeachImageSource.substring(0, fileExtension);
        String uri = "@drawable/" + landingBeachImageSource;
        Log.d("SetImage", " this is the file path: " + uri);
        int fileID = 0;

        try {
            fileID = R.drawable.class.getField(landingBeachImageSource).getInt(null);
        } catch (IllegalAccessException e) {
            Log.d("getImageIDError", "Error getting image");
        } catch (NoSuchFieldException e2) {
            Log.d("getImageIDError", "no Icon found");
        }
        landingBeachImageView = findViewById(R.id.landingBeachImageView);
        landingBeachImageView.setImageResource(fileID);

    }
}
