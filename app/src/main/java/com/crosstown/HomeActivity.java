package com.crosstown;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crosstown.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import java.util.Date;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap map;
    private double longitude, latitude;
    private LocationManager lm;
    private LatLng curLatLng;
    private MarkerOptions locationMarker = null;
    private Marker currentMarker = null;

    private final String TAG = "HomeActivity";

    private Location currentLocation = null;
    private Location previousLocation = null;
    private long previousTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().getDecorView().setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_color));
        }

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // region Events

    public void destinationSearchTrigger_OnClick(View view)
    {
        Intent intent = new Intent(getBaseContext(), SelectLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("Type", "Destination");
        intent.putExtra("OtherLocationPlaceId", "");
        intent.putExtra("OtherLocationMainText", "");
        intent.putExtra("OtherLocationSecondaryText", "");
        intent.putExtra("OtherLocationLatitude", 0d);
        intent.putExtra("OtherLocationLongitude", 0d);
        intent.putExtra("OtherLocationDescription", getString(R.string.current_location));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    // endregion

    // region Google Maps Stub

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        //map.setMapType(map.MAP_TYPE_TERRAIN);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setCompassEnabled(false);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = Utilities.GetBestLocationProvider(lm);
        currentLocation = lm.getLastKnownLocation(provider);

        if (Utilities.IsProviderEnabled(this, LocationManager.GPS_PROVIDER, lm))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        else
            lm.requestLocationUpdates(provider, 0, 0, locationListener);

        if (currentLocation != null)
        {
            longitude = currentLocation.getLongitude();
            latitude = currentLocation.getLatitude();
            curLatLng = new LatLng(latitude, longitude);
            if (locationMarker == null) {
                locationMarker = new MarkerOptions().position(curLatLng).title("Current location");
                currentMarker = map.addMarker(locationMarker);
            }
            currentMarker.setPosition(curLatLng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 17.0f));
        }
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onLocationChanged(Location location)
        {
            currentLocation = location;
            if (previousTime == -1 || previousLocation == null)
            {
                previousTime = System.currentTimeMillis();
                previousLocation = currentLocation;
            }
            if (System.currentTimeMillis() - previousTime > 3000)
            {
                double speed = Utilities.GetSpeed(previousLocation, currentLocation, previousTime);

                TextView speedTv = findViewById(R.id.speed);
                speedTv.setText("Speed is: " + Utilities.decimalFormat.format(speed) + " km/h");

                previousTime = System.currentTimeMillis();
                previousLocation = currentLocation;
            }

            longitude = currentLocation.getLongitude();
            latitude = currentLocation.getLatitude();

            Log.d("MAPS_LOCATION_UPDATE", "GPS Latitude: " + currentLocation.getLatitude()
                    + ", Longitude: " + currentLocation.getLongitude()
                    + ", Accuracy: " + currentLocation.getAccuracy()
                    + ", Time: " + (new Date(currentLocation.getTime()).toString())
                    + ", Speed: " + currentLocation.getSpeed());

            curLatLng = new LatLng(latitude, longitude);
            if (locationMarker == null) {
                locationMarker = new MarkerOptions().position(curLatLng).title("Current location");
                currentMarker = map.addMarker(locationMarker);
            }
            currentMarker.setPosition(curLatLng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 17.0f));
        }
    };

    // endregion
}