package com.crosstown.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.crosstown.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RoutesService extends AsyncTask<String, Void, JSONObject>
{
    private Callback<JSONObject> callback;
    private Context context;
    private JSONObject searchResults;

    public RoutesService(Callback<JSONObject> callback, Context context)
    {
        this.callback = callback;
        this.context = context;
    }

    protected void onPreExecute() { }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        String directionsApiKey = context.getString(R.string.directions_api_key);
        URL url;
        HttpsURLConnection con;
        BufferedReader br;

        try
        {
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            String provider = Utilities.GetBestLocationProvider(lm);
            Location current = lm.getLastKnownLocation(provider);

            url = new URL("https://maps.googleapis.com/maps/api/directions/json?destination="
                    + strings[0] + "&alternatives=true&mode=transit&transit_mode=bus&origin="
                    + current.getLatitude() + "," + current.getLongitude() + "&key="
                    + directionsApiKey + "&language="
                    + Utilities.GetISOUserLanguage());

            con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            if (con.getResponseCode() == 200)
            {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    sb.append(strCurrentLine);
                    Log.d("RoutesService", strCurrentLine);
                }
                searchResults = new JSONObject(sb.toString());
                br.close();
            }
            else
            {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    Log.d("RoutesService", strCurrentLine);
                }
            }
            con.disconnect();
        } catch (ProtocolException | MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResults;
    }

    protected void onPostExecute(JSONObject searchResults) {
        callback.callback(searchResults);
    }
}