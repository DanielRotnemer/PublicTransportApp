package com.crosstown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crosstown.utilities.Callback;
import com.crosstown.utilities.PlacesService;
import com.crosstown.utilities.PointLocationService;
import com.crosstown.utilities.Utilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowSelectedLocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private TextView startingPointTv, destinationTv;
    private Location source, destination;
    private GoogleMap map;

    private Callback<JSONArray> pointLocationCallback = new Callback<JSONArray>()
    {
        @Override
        public void callback(JSONArray arg)
        {
            try
            {
                for (int i = 0; i < arg.length(); i++)
                {
                    JSONObject current = arg.getJSONObject(i);
                    JSONObject location = current.getJSONObject("result").getJSONObject("geometry")
                            .getJSONObject("location");

                    int index = current.getInt("index");
                    if (index == 0) /* source */
                    {
                        source.setLatitude(location.getDouble("lat"));
                        source.setLongitude(location.getDouble("lng"));
                    }
                    else if (index == 1) /* destination */
                    {
                        destination.setLatitude(location.getDouble("lat"));
                        destination.setLongitude(location.getDouble("lng"));
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            // add markers and draw a line between destination and source

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_location);

        getWindow().getDecorView().setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_color));
        }

        startingPointTv = findViewById(R.id.startingPointTv);
        destinationTv = findViewById(R.id.destinationTv);

        Intent intent = getIntent();
        if (intent.getStringExtra("Type").equals("Destination"))
        {
            destinationTv.setText(intent.getStringExtra("Description"));
            startingPointTv.setText(intent.getStringExtra("OtherLocationDescription"));
        }
        else if (intent.getStringExtra("Type").equals("Source"))
        {
            startingPointTv.setText(intent.getStringExtra("Description"));
            destinationTv.setText(intent.getStringExtra("OtherLocationDescription"));
        }

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = Utilities.GetBestLocationProvider(lm);
        source = new Location(provider);
        destination = new Location(provider);

        String[] placesId = new String[] { "" /* source */, "" /* destination */ };
        if (intent.getStringExtra("Type").equals("Destination")) // changed location is destination
        {
            if (intent.getStringExtra("OtherLocationDescription").equals(getString(R.string.current_location)))
                source = lm.getLastKnownLocation(provider);
            else if (intent.hasExtra("OtherLocationLatitude") && intent.hasExtra("OtherLocationLongitude"))
            {
                double latitude = intent.getDoubleExtra("OtherLocationLatitude", 0d);
                double longitude = intent.getDoubleExtra("OtherLocationLongitude", 0d);
                source.setLatitude(latitude);
                source.setLongitude(longitude);
            }
            else
                placesId[0] = intent.getStringExtra("OtherLocationPlaceId");

            if (intent.getStringExtra("Description").equals(getString(R.string.current_location)))
                destination = lm.getLastKnownLocation(provider);
            else
                placesId[1] = intent.getStringExtra("PlaceId");
        }
        else if (intent.getStringExtra("Type").equals("Source")) // changed location is source
        {
            if (intent.getStringExtra("OtherLocationDescription").equals(getString(R.string.current_location)))
                destination = lm.getLastKnownLocation(provider);
            else if (intent.hasExtra("OtherLocationLatitude") && intent.hasExtra("OtherLocationLongitude"))
            {
                double latitude = intent.getDoubleExtra("OtherLocationLatitude", 0d);
                double longitude = intent.getDoubleExtra("OtherLocationLongitude", 0d);
                destination.setLatitude(latitude);
                destination.setLongitude(longitude);
            }
            else
                placesId[1] = intent.getStringExtra("OtherLocationPlaceId");

            if (intent.getStringExtra("Description").equals(getString(R.string.current_location)))
                source = lm.getLastKnownLocation(provider);
            else
                placesId[0] = intent.getStringExtra("PlaceId");
        }

        PointLocationService task = new PointLocationService(pointLocationCallback, this);
        task.execute(placesId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
    }
}