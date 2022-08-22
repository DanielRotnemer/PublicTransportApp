package com.crosstown.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

import com.crosstown.R;

import java.text.DecimalFormat;
import java.util.Locale;

public class Utilities
{
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public static enum DistanceUnits { KM, MILE, METER };

    public static String GetISOUserLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static boolean HasPermissions(Context context, String[] permissions)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int PX2DP(int px) {
        return (int)(px * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int DP2PX(int dp) {
        return (int)(dp / Resources.getSystem().getDisplayMetrics().density);
    }

    public static boolean IsProviderEnabled(Context context, String provider, LocationManager lm)
    {
        ContentResolver contentResolver = context.getContentResolver();
        boolean status = Settings.Secure
                .isLocationProviderEnabled(contentResolver, provider);
        status = status && lm.isProviderEnabled(provider);
        return status;
    }

    public static double DistanceMiles(Location l1, Location l2)
    {
        double lon1 = l1.getLongitude(), lat1 = l1.getLatitude(),
                lon2 = l2.getLongitude(), lat2 = l2.getLatitude();
        double theta = lon1 - lon2;
        double dist = Math.sin(Deg2Rad(lat1))
                * Math.sin(Deg2Rad(lat2))
                + Math.cos(Deg2Rad(lat1))
                * Math.cos(Deg2Rad(lat2))
                * Math.cos(Deg2Rad(theta));
        dist = Math.acos(dist);
        dist = Rad2Deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    public static double MilesToKM(double miles) {
        return miles / 0.62137;
    }

    public static double KmToMiles(double km) { return km * 0.62137; }

    public static double Deg2Rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double Rad2Deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double Speed(double distanceKM, double timeLapseMillis)
    {
        double kmPerHour = 3600000 / timeLapseMillis;
        kmPerHour *= distanceKM;
        return kmPerHour;
    }

    public static double GetSpeed(Location previous, Location current, long time)
    {
        double distance = Utilities.DistanceMiles(current, previous);
        distance = Utilities.MilesToKM(distance);
        double timeLapse = System.currentTimeMillis() - time;
        double speed = Utilities.Speed(distance, timeLapse);
        return speed;
    }

    public static String GetBestLocationProvider(LocationManager lm)
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        String provider = lm.getBestProvider(criteria, true);
        return provider;
    }

    public static DistanceUnits GetDistanceUnit(String distance)
    {
        if (distance.contains("km"))
            return DistanceUnits.KM;
        if (distance.contains("mi"))
            return DistanceUnits.MILE;
        return DistanceUnits.METER;
    }

    public static String AdjustDistance(int meters, DistanceUnits distanceUnit, Context context)
    {
        if (meters < 100)
            return meters + " " + context.getResources().getString(R.string.meters);

        double km = meters / 1000d;
        if (distanceUnit == DistanceUnits.KM)
            return decimalFormat.format(km) + " " + context.getResources().getString(R.string.km);

        double miles = KmToMiles(km);
        return decimalFormat.format(miles) + " " + context.getResources().getString(R.string.miles);
    }

    public static DistanceUnits GetSupportedDistanceUnit(Context context)
    {
        Locale locale = context.getResources().getConfiguration().locale;
        String countryCode = locale.getCountry().toUpperCase();
        if (countryCode.equals("US") || countryCode.equals("LR") || countryCode.equals("MM"))
            return DistanceUnits.MILE;
        return DistanceUnits.KM;
    }

    public static String AdjustHours(int seconds, Context context)
    {
        if (seconds < 60)
            return seconds + " " + context.getResources().getString(R.string.seconds);
        if (seconds < 3600)
        {
            int minutes = seconds / 60;
            seconds -= minutes * 60;
            String result = "";

            if (minutes == 1)
                result += minutes + " " + context.getResources().getString(R.string.minute);
            else
                result += minutes + " " + context.getResources().getString(R.string.minutes);

            if (seconds == 1)
                result += ", " + seconds + " " + context.getResources().getString(R.string.second);
            else if (seconds != 0)
                result += ", " + seconds + " " + context.getResources().getString(R.string.seconds);
            return result;
        }
        else
        {
            int minutes = seconds / 60;
            seconds -= minutes * 60;
            int hours = minutes / 60;
            minutes -= hours * 60;
            String result = "";

            if (hours == 1)
                result += hours + " " + context.getResources().getString(R.string.hour);
            else
                result += hours + " " + context.getResources().getString(R.string.hours);

            if (minutes == 1)
                result += minutes + ", " + context.getResources().getString(R.string.minute);
            else if (minutes > 0)
                result += minutes + ", " + context.getResources().getString(R.string.minutes);

            return result;
        }
    }
}
