package pl.menagochicken.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HikersWatchActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }
    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        }
    }

    public void updateLocationInfo(Location location) {

        Log.i("Location", location.toString());

        TextView latTextView = findViewById(R.id.latTextView);
        TextView lonTextView = findViewById(R.id.lonTextView);
        TextView accTextView = findViewById(R.id.accTextView);
        TextView speedTextView = findViewById(R.id.speedTextView);
        TextView altTextView = findViewById(R.id.altTextView);
        TextView addressTextView = findViewById(R.id.addresTextView);

        latTextView.setText("Latitude: " + Math.round(location.getLatitude() * 10000d) / 10000d);
        lonTextView.setText("Longitude: " + Math.round(location.getLongitude() * 10000d) / 10000d);
        accTextView.setText("Accuracy: " + location.getAccuracy());
        speedTextView.setText("Speed: " + Math.round(location.getSpeed() * 10d / 10d));
        altTextView.setText("Alttitude: " + Math.round(location.getAltitude() * 100d / 100d));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find address :(";

            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addressList != null && addressList.size() > 0) {

                address = "";

                if (addressList.get(0).getSubThoroughfare() != null) {
                    address += addressList.get(0).getSubThoroughfare() + " ";
                }

                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare() + "\n";
                }

                if (addressList.get(0).getLocality() != null) {
                    address += addressList.get(0).getLocality() + "\n";
                }

                if (addressList.get(0).getPostalCode() != null) {
                    Log.i("Postal code", addressList.get(0).toString());
                    address += addressList.get(0).getPostalCode() + "\n";
                }

                if (addressList.get(0).getCountryName() != null) {
                    address += addressList.get(0).getCountryName();
                }
            }

            addressTextView.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {

                updateLocationInfo(location);

            }
        }
    }

    public void toCompass(View view) {
        Intent intent = new Intent(getApplicationContext(), CompassActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}

