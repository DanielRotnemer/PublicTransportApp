package com.crosstown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.crosstown.custom.PlaceResultView;
import com.crosstown.utilities.Callback;
import com.crosstown.utilities.PlacesService;
import com.crosstown.utilities.RoutesService;
import com.crosstown.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

public class SelectLocationActivity extends AppCompatActivity
{
    private EditText locationSearchEditText;
    private LinearLayout locationSearchResultsLayout;
    private String searchType;

    private String otherLocationPlaceId, otherLocationMainText, otherLocationSecondaryText,
            otherLocationDescription;
    private double otherLocationLongitude, otherLocationLatitude;

    private Callback<JSONObject> placesCallback = new Callback<JSONObject>()
    {
        @Override
        public void callback(JSONObject placesResult)
        {
            try
            {
                locationSearchResultsLayout.removeAllViews();
                Log.d("MainActivity", "--------------------------");
                JSONArray predictions = placesResult.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++)
                {
                    JSONObject currentObject = predictions.getJSONObject(i);
                    Log.d("MainActivity", "********** JSONObject " + i + ": **********");
                    Log.d("MainActivity", "Description: " + currentObject.getString("description"));

                    JSONObject structuredFormatting = currentObject.getJSONObject("structured_formatting");
                    Log.d("MainActivity", "Main text: " + structuredFormatting.getString("main_text"));
                    Log.d("MainActivity", "Secondary text: " + (structuredFormatting.has("secondary_text")
                        ? structuredFormatting.getString("secondary_text") : ""));

                    String termsString = "";
                    JSONArray terms = currentObject.getJSONArray("terms");
                    for (int t = 0; t < terms.length(); t++)
                    {
                        JSONObject currentTerm = terms.getJSONObject(t);
                        termsString += t != 0 ? ", " : "";
                        termsString += currentTerm.getString("value");
                    }
                    Log.d("MainActivity", "Terms: " + termsString);

                    // -------------------

                    String mainText = terms.length() > 0 ? terms.getJSONObject(0).getString("value")
                            : structuredFormatting.getString("main_text");

                    String secondaryText = structuredFormatting.has("secondary_text") ?
                            structuredFormatting.getString("secondary_text") : "";
                    if (terms.length() > 1)
                    {
                        secondaryText = "";
                        for (int t = 1; t < terms.length(); t++)
                            secondaryText += t == 1 ? terms.getJSONObject(t).getString("value")
                                    : ", " + terms.getJSONObject(t).getString("value");
                    }

                    String distance = "";
                    if (currentObject.has("distance_meters"))
                    {
                        int distanceInMeters = currentObject.getInt("distance_meters");
                        distance = Utilities.AdjustDistance((int)distanceInMeters,
                                Utilities.GetSupportedDistanceUnit(getBaseContext()), getBaseContext());
                        Log.d("TestDistance", "distanceInMeters: " + distanceInMeters + ", distance: " + distance);
                    }
                    else
                    {
                        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        String provider = Utilities.GetBestLocationProvider(lm);
                        Location current = lm.getLastKnownLocation(provider);
                        Location l = new Location(provider);
                        l.setLatitude(currentObject.getDouble("latitude"));
                        l.setLongitude(currentObject.getDouble("longitude"));
                        double distanceInMeters = Utilities.DistanceMiles(current, l);
                        distanceInMeters = Utilities.MilesToKM(distanceInMeters);
                        distanceInMeters *= 1000;
                        distance = Utilities.AdjustDistance((int)distanceInMeters,
                                Utilities.GetSupportedDistanceUnit(getBaseContext()), getBaseContext());
                    }

                    String placeId = currentObject.getString("place_id");

                    PlaceResultView prv = new PlaceResultView(getApplicationContext());
                    locationSearchResultsLayout.addView(prv);
                    prv.setDescription(currentObject.getString("description"));
                    prv.setMainText(mainText);
                    prv.setSecondaryText(secondaryText);
                    prv.setDistance(distance);
                    prv.setPlaceId(placeId);
                    prv.setOnClickListener(resultClickListener);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener resultClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            PlaceResultView prv = (PlaceResultView)view;
            Intent intent = new Intent(getBaseContext(), ShowSelectedLocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            intent.putExtra("Type", searchType);
            intent.putExtra("MainText", prv.getMainText());
            intent.putExtra("SecondaryText", prv.getSecondaryText());
            intent.putExtra("Description", prv.getDescription());
            intent.putExtra("PlaceId", prv.getPlaceId());
            intent.putExtra("Distance", prv.getDistance());

            intent.putExtra("OtherLocationDescription", otherLocationDescription);
            intent.putExtra("OtherLocationMainText", otherLocationMainText);
            intent.putExtra("OtherLocationSecondaryText", otherLocationSecondaryText);
            intent.putExtra("OtherLocationPlaceId", otherLocationPlaceId);
            intent.putExtra("OtherLocationLongitude", otherLocationLongitude);
            intent.putExtra("OtherLocationLatitude", otherLocationLatitude);

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        locationSearchEditText = findViewById(R.id.locationSearchEditText);
        locationSearchResultsLayout = findViewById(R.id.locationSearchResultsLayout);

        searchType = getIntent().getStringExtra("Type");
        if (searchType.equals("Destination"))
            locationSearchEditText.setHint(R.string.select_destination);
        else if (searchType.equals("Source"))
            locationSearchEditText.setHint(R.string.select_source);

        otherLocationDescription = getIntent().getStringExtra("OtherLocationDescription");
        otherLocationMainText = getIntent().getStringExtra("OtherLocationMainText");
        otherLocationSecondaryText = getIntent().getStringExtra("OtherLocationSecondaryText");
        otherLocationPlaceId = getIntent().getStringExtra("OtherLocationPlaceId");
        otherLocationLatitude = getIntent().getDoubleExtra("OtherLocationLatitude", 0d);
        otherLocationLongitude = getIntent().getDoubleExtra("OtherLocationLongitude", 0d);

        locationSearchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        locationSearchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after)
            {
                if (!charSequence.toString().equals(""))
                {
                    PlacesService task = new PlacesService(placesCallback, getApplicationContext());
                    String[] params = new String[1];
                    params[0] = charSequence.toString();
                    task.execute(params);
                }
                else {
                    Log.d("MainActivity", "Empty: " + charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }
}