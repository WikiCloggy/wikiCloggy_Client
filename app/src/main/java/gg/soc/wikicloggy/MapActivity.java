package gg.soc.wikicloggy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapView;


public class MapActivity extends Activity {
    private final static String TAG = "MapActivity";
    //MapView mapView;
    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;

    Location location;
    double lat; //위도
    double lon; //경도
    //RelativeLayout container;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1;

    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                }else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    }else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
            //StopMapTask stopMapTask = new StopMapTask();
            //stopMapTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lon = location.getLongitude();
            lat = location.getLatitude();
            String url = "daummaps://search?q=동물 병원&p="+lat+", "+lon;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            locationManager.removeUpdates(mLocationListener);
            finish();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    class StopMapTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
                locationManager.removeUpdates(mLocationListener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
