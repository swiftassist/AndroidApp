package io.github.swiftassist.swiftassist;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmergencyActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{
    private Button logoutButton;

    private Switch breathingSwitch;
    private Switch allergySwitch;
    private Switch epipenSwitch;
    private Switch inhalerSwitch;

    private Button emergencyButton;

    private GoogleMap map;
    private LocationManager locMan;

    private Firebase mainFirebaseRef;
    private Firebase emergencyFirebaseRef;

    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(this, EmergencyResponseService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        mainFirebaseRef = ((SwiftAssistApplication)getApplication()).getFirebaseRef();
        emergencyFirebaseRef = new Firebase("https://swiftassist.firebaseio.com/emergency/");

        logoutButton = (Button) findViewById(R.id.logoutButton);
        breathingSwitch = (Switch) findViewById(R.id.breathingSwitch);
        allergySwitch = (Switch) findViewById(R.id.allergySwitch);
        epipenSwitch = (Switch) findViewById(R.id.epipenSwitch);
        inhalerSwitch = (Switch) findViewById(R.id.inhalerSwitch);
        emergencyButton = (Button) findViewById(R.id.emergencyButton);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFirebaseRef.unauth();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location currentLocation = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    String type = "general";                    //TODO update type db stuff

                    if (epipenSwitch.isChecked()) {
                        type = "epipen";
                    }

                    if (breathingSwitch.isChecked()) {
                        type = "allergy";
                    }

                    EmergencyData data = new EmergencyData(currentLocation.getLatitude(), currentLocation.getLongitude(), type, "android");
                    emergencyFirebaseRef.push().setValue(data, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase){
                            //noinspection ResourceType <- prevents warnings about retrieving color resource as string
                            String colorString = getResources().getString(R.color.colorPrimary).substring(3);
                            Toast.makeText(EmergencyActivity.this, colorString, Toast.LENGTH_SHORT).show();
                            AlertDialog dialog = new AlertDialog.Builder(EmergencyActivity.this)
                                    .setTitle(Html.fromHtml("<font color='#"
                                            +colorString
                                            +"'>Recieved</font>"))
                                    .setMessage("Nearby users have been notified of your situation. Help is on the way!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create();
                            dialog.show();

                        }
                    });
                }
                else
                    ActivityCompat.requestPermissions(EmergencyActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_LOCATION);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case (MY_PERMISSIONS_REQUEST_READ_LOCATION):
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initLocationManager();
                else
                    Toast.makeText(this, "SwiftAssist will not work unless you allow location access!", Toast.LENGTH_SHORT).show();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initLocationManager(){
        locMan = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EmergencyActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                //show dialog explaining permission
            }
            ActivityCompat.requestPermissions(EmergencyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
        }else {
            initLocationManager();
        }
    }

    public void zoomToMyLocation(){
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            Location loc = locMan.getLastKnownLocation(locMan.getBestProvider(new Criteria(), false));
            CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .zoom(15.8f)
                    .build();
            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
            map.animateCamera(camUpdate, 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    // map is zoomed into users location
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        emergencyButton.setEnabled(true);
        zoomToMyLocation();
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            locMan.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
