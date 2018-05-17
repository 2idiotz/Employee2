package ziza1996.hcewras.employee;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btShowLocation;
    GPSTrackerClass gps;
    TextView dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btShowLocation = (Button) findViewById(R.id.showLocation);
        dis=(TextView) findViewById(R.id.distance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create class object
                gps = new GPSTrackerClass(MainActivity.this);

                 //check if GPS enabled
                if (gps.canGetLocation()) {

                    // onMapReady(mMap);

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LatLng sydney = new LatLng(latitude, longitude);
                    LatLng sydeny2=new LatLng(24.904961,67.0782979);

                    mMap.addMarker(new MarkerOptions().position(sydney));
                    mMap.addMarker(new MarkerOptions().position(sydeny2));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydeny2));

                    ////////////add circle//////////////////
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(24.904961, 67.0782979))
                            .radius(3)    //in meters  s
                            .fillColor(Color.TRANSPARENT)
                            .strokeColor(Color.DKGRAY));

                    ///////////////draw path line////////////////////
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(latitude, longitude), new LatLng(24.904961, 67.0782979))
                            .width(9)
                            .color(Color.BLUE));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(100), 2000, null);
                    ////////////////bssid//////////

                    getMacId();
                    /////////////////////////distance//////////////////
                    Location startPoint=new Location("locationA");
                    startPoint.setLatitude(latitude);
                    startPoint.setLongitude(longitude);

                    Location endPoint=new Location("locationA");
                    endPoint.setLatitude(24.904961);
                    endPoint.setLongitude(67.0782979);

                    double distance=startPoint.distanceTo(endPoint);
                  //  dist.setText(Double.toString(distance));

                    double earthRadius = 6371000; //meters
                    double dLat = Math.toRadians(24.904961-latitude);
                    double dLng = Math.toRadians(67.0782979-longitude);
                    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                            Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(24.904961)) *
                                    Math.sin(dLng/2) * Math.sin(dLng/2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                    float dist = (float) (earthRadius * c);
                    if(dist>20){
                        btShowLocation.setEnabled(false);
                    }
                    dis.setText(Double.toString(dist));
                    System.out.println("--tag distance===="+distance);
                    System.out.println("--tag long ==="+longitude);
                    System.out.println("--tag lat ==="+latitude);


                    ///////////////////ip address for wifi/////////////////
                    WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInf = wifiMan.getConnectionInfo();
                    int ipAddress = wifiInf.getIpAddress();
                    String address = wifiInf.getMacAddress();
                    String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
                    System.out.println("--tag ip address"+ip);
                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Your ip address is"+ip, Toast.LENGTH_LONG).show();
                   Toast.makeText(getApplicationContext(), "Your mac address is is"+distance, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Your istanced "+dist, Toast.LENGTH_LONG).show();


                   ////////////////////////////////////mac address/////////////////////

                    try {
                        // get all the interfaces
                        List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                        //find network interface wlan0
                        for (NetworkInterface networkInterface : all) {
                            if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                            //get the hardware address (MAC) of the interface
                            byte[] macBytes = networkInterface.getHardwareAddress();
                            if (macBytes == null) {
                                String ab="";

                                System.out.println("--tag ab of mac"+ab);
                            }


                            StringBuilder res1 = new StringBuilder();
                            for (byte b : macBytes) {
                                //gets the last byte of b
                                res1.append(Integer.toHexString(b & 0xFF) + ":");
                            }

                            if (res1.length() > 0) {
                                res1.deleteCharAt(res1.length() - 1);
                            }
                            String res= res1.toString();
                            System.out.println("--tag res in mac=="+res);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //   getMyLocation();
        // Add a marker in Sydney, Australia, and move the camera.
        // LatLng sydney = new LatLng(25.3751, 68.3505);
//        double latitude = gps.getLatitude();
//        double longitude = gps.getLongitude();
//        LatLng latLng = new LatLng(latitude, longitude);
//        LatLng sydney = new LatLng(gps.getLatitude(), gps.getLongitude());
        // mMap.addMarker(new MarkerOptions().position(sydney).title("GEX EDU"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public String getMacId() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        System.out.println("--tag mac id"+wifiInfo.getBSSID());
        return wifiInfo.getBSSID();
    }

}