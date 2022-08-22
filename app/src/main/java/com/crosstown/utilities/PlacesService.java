package com.crosstown.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.crosstown.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PlacesService extends AsyncTask<String, Void, JSONObject>
{
    private Callback<JSONObject> callback;
    private Context context;
    private JSONObject searchResults;

    public PlacesService(Callback<JSONObject> callback, Context context)
    {
        this.callback = callback;
        this.context = context;
    }

    protected void onPreExecute() { }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        String placesApiKey = context.getString(R.string.places_api_key);
        URL url;
        HttpsURLConnection con;
        BufferedReader br;

        try
        {
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            String provider = Utilities.GetBestLocationProvider(lm);
            Location current = lm.getLastKnownLocation(provider);

            Log.d("MainActivity", current.getLatitude() + ", " + current.getLongitude());

            url = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + strings[0] + "&key=" + placesApiKey + "&language="
                    + Utilities.GetISOUserLanguage() + "&origin="
                    + current.getLatitude() + "," + current.getLongitude());
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
                    Log.d("MainActivity", strCurrentLine);
                }
                searchResults = new JSONObject(sb.toString());
                br.close();
            }
            else
            {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    Log.d("MainActivity", strCurrentLine);
                }
            }
            con.disconnect();
        } catch (ProtocolException | MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            JSONArray predictions = searchResults.getJSONArray("predictions");
            for (int i = 0; i < predictions.length(); i++)
            {
                JSONObject currentObject = predictions.getJSONObject(i);
                if (currentObject.has("distance_meters"))
                    continue;

                String placeId = currentObject.getString("place_id");
                url = new URL("https://maps.googleapis.com/maps/api/place/details/json?fields=geometry"
                        + "&place_id=" + placeId
                        + "&key=" + placesApiKey + "&language="
                        + Utilities.GetISOUserLanguage());
                con = (HttpsURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                if (con.getResponseCode() == 200)
                {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null)
                        sb.append(strCurrentLine);
                    JSONObject result = new JSONObject(sb.toString());
                    br.close();

                    JSONObject location = result.getJSONObject("result").getJSONObject("geometry")
                            .getJSONObject("location");
                    currentObject.accumulate("latitude", location.getDouble("lat"));
                    currentObject.accumulate("longitude", location.getDouble("lng"));
                }
                else
                {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null)
                        Log.d("MainActivity", strCurrentLine);
                }
                con.disconnect();
            }
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
