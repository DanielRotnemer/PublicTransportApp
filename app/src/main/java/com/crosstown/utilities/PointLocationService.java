package com.crosstown.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class PointLocationService extends AsyncTask<String, Void, JSONArray>
{
    private Callback<JSONArray> callback;
    private Context context;
    private JSONArray locations;

    public PointLocationService(Callback<JSONArray> callback, Context context)
    {
        this.callback = callback;
        this.context = context;
    }

    protected void onPreExecute() { }

    @Override
    protected JSONArray doInBackground(String... strings)
    {
        String placesApiKey = context.getString(R.string.places_api_key);
        URL url;
        HttpsURLConnection con;

        locations = new JSONArray();

        try
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i  < strings.length; i++)
            {
                String placeId = strings[i];
                if (placeId.equals(""))
                    continue;

                url = new URL("https://maps.googleapis.com/maps/api/place/details/json?fields=geometry"
                        + "&place_id=" + placeId
                        + "&key=" + placesApiKey + "&language="
                        + Utilities.GetISOUserLanguage());
                con = (HttpsURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                if (con.getResponseCode() == 200)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null)
                        sb.append(strCurrentLine);
                    br.close();
                    JSONObject result = new JSONObject(sb.toString());
                    result.accumulate("index", i);
                    locations.put(result.toString());
                }
                else
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String strCurrentLine;
                    while ((strCurrentLine = br.readLine()) != null)
                        Log.d("MainActivity", strCurrentLine);
                }
                con.disconnect();
            }
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }

    protected void onPostExecute(JSONArray searchResults) {
        callback.callback(searchResults);
    }
}
