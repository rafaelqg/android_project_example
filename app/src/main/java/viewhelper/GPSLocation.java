package viewhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSLocation {
    public static void initService(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else
            configService(activity);
    }

    public static void configService(Activity activity){
        try {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Double latPoint = location.getLatitude();
                    Double lngPoint = location.getLongitude();
                   // Log.i("Location","Latitude:"+latPoint);
                    // Log.i("Location","Longitude:"+lngPoint);
                    try {
                        ((ActivityLocationChangeHandler) activity).updateLocation(latPoint, lngPoint);
                    }catch(Exception e){
                        Log.e("Location","Error parsing class "+activity.getClass().toString()+" ActivityLocationChanteHandler.");
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
