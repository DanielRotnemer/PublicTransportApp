package com.crosstown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.crosstown.utilities.Callback;
import com.crosstown.utilities.PlacesService;
import com.crosstown.utilities.RoutesService;
import com.crosstown.utilities.Utilities;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.collections.PolylineManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoutesActivity extends AppCompatActivity
{
    private TextView startingPointTv, destinationTv;
    private Location current;
    private String destinationMainText, destinationSecondaryText, destinationDescription, placeId,
            destinationDistance;

    private Callback<JSONObject> routesCallback = new Callback<JSONObject>()
    {
        @Override
        public void callback(JSONObject routesResults)
        {
            try
            {
                JSONArray routes = routesResults.getJSONArray("routes");
                for (int i = 0; i < routes.length(); i++)
                {
                    Log.d("RoutesService", "-------- Route #" + i + " --------");

                    JSONObject currentRoute = routes.getJSONObject(i);
                    JSONArray legs = currentRoute.getJSONArray("legs");
                    for (int l = 0; l < legs.length(); l++)
                    {
                        Log.d("RoutesService", "-------- Leg #" + l + " --------");

                        JSONObject currentLeg = legs.getJSONObject(l);
                        JSONArray steps = currentLeg.getJSONArray("steps");

                        // get out time
                        JSONObject getOutTimeObj = currentLeg.getJSONObject("departure_time");
                        String getOutTime = getOutTimeObj.getString("text");

                        // arrival time
                        JSONObject arrivalTimeObj = currentLeg.getJSONObject("arrival_time");
                        String arrivalTime = arrivalTimeObj.getString("text");

                        // distance
                        JSONObject distanceObj = currentLeg.getJSONObject("distance");
                        Utilities.DistanceUnits unit = Utilities.GetSupportedDistanceUnit(getBaseContext());
                        int meters = distanceObj.getInt("value");
                        String adjustedDistance = Utilities.AdjustDistance(meters, unit, getBaseContext());

                        // duration
                        JSONObject durationObj = currentLeg.getJSONObject("duration");
                        int seconds = durationObj.getInt("value");
                        String adjustedDuration = Utilities.AdjustHours(seconds, getBaseContext());

                        // start stop
                        String departFrom = "", departureTime = "", line = "";

                        for (int s = 0; s < steps.length(); s++)
                        {
                            JSONObject currentStep = steps.getJSONObject(s);
                            String travelMode = currentStep.getString("travel_mode");
                            if ("TRANSIT".equals(travelMode))
                            {
                                JSONObject transitDetails = currentStep.getJSONObject("transit_details");
                                JSONObject departureStop = transitDetails.getJSONObject("departure_stop");
                                JSONObject departTime = transitDetails.getJSONObject("departure_time");
                                JSONObject lineObj = transitDetails.getJSONObject("line");

                                line = lineObj.getString("short_name");
                                departFrom = departureStop.getString("name");
                                departureTime = departTime.getString("text");
                                break;
                            }
                        }

                        Log.d("RoutesService", "Get out at: " + getOutTime);
                        Log.d("RoutesService", "Line: " + line);
                        Log.d("RoutesService", "Depart from: " + departFrom);
                        Log.d("RoutesService", "Departure time: " + departureTime);
                        Log.d("RoutesService", "Distance: " + adjustedDistance);
                        Log.d("RoutesService", "Duration: " + adjustedDuration);
                        Log.d("RoutesService", "Arrival time: " + arrivalTime);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        startingPointTv = (TextView)findViewById(R.id.startingPointTv);
        destinationTv = (TextView)findViewById(R.id.destinationTv);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = Utilities.GetBestLocationProvider(lm);
        current = lm.getLastKnownLocation(provider);

        destinationMainText = getIntent().getStringExtra("MainText");
        destinationSecondaryText = getIntent().getStringExtra("SecondaryText");
        destinationDescription = getIntent().getStringExtra("Description");
        placeId = getIntent().getStringExtra("PlaceId");
        destinationDistance = getIntent().getStringExtra("Distance");

        startingPointTv.setText(R.string.current_location);
        destinationTv.setText(destinationDescription);
        RoutesService task = new RoutesService(routesCallback, getApplicationContext());
        String[] params = new String[1];
        params[0] = "place_id:" + placeId;
        task.execute(params);
    }
}