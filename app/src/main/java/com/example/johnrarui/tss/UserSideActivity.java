package com.example.johnrarui.tss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserSideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GeoQueryEventListener, GoogleMap.OnCameraChangeListener  {

    private GoogleMap mMap;
    private String ad, pickup;
    Double price;
    Button searchView, book, order;
    EditText source, destination;
    RelativeLayout layout;
    ProgressDialog progressDialog;
    JSONObject response;
    TextView mat, details;
    LatLng loc1;
    private FirebaseAuth mAuth;

    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7789, -122.4017);

    private static final int INITIAL_ZOOM_LEVEL = 14;

    private static final String GEO_FIRE_DB = "https://publicdata-transit.firebaseio.com";

    private static final String GEO_FIRE_REF = GEO_FIRE_DB + "/_geofire";



    Geocoder geocoder;
    private LocationListener mLocationListener;
    private Map<String,Marker> markers;
    private Circle searchCircle;

    LocationManager mlocationManager;
    LatLng first, second;
    UserSideActivity userSideActivity;
    private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_side);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        book = (Button) findViewById(R.id.buttonInitiate);
        progressDialog=new ProgressDialog(this);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserSideActivity.this,RequestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_side, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent=new Intent(UserSideActivity.this,HistoryActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            mAuth.signOut();
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);






        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Map_values map_values=new Map_values();
        second=map_values.getSec();
        first=map_values.getFirst();



        if (first!=null&&second!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(first)
                    .title("MAEKER SET")
            );
            mMap.addMarker(new MarkerOptions()
                    .position(second)
                    .title("MAEKER SET")
            );
            // Move the camera instantly to hamburg with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 15));
        }else{
            // Add a marker in Sydney and move the camera
            mlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


                return;
            }
            mLocationListener= new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    LatLng loc=new LatLng( location.getLatitude(),location.getLongitude());
                    loc1=loc;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
                    GeoFire geoFire = new GeoFire(ref);
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(loc.latitude,loc.longitude), 0.6);


                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title("your position"));
                    searchCircle=mMap.addCircle(new CircleOptions().center(loc).radius(1000));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10));

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

            mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2,  mLocationListener);

//            LatLng sydney = new LatLng(-34, 151);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }



    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.latitude,location.longitude))
                .title(""));


    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Marker marker=this.markers.get(key);
        if (marker != null) {

            this.animateMarkerTo(marker, location.latitude, location.longitude);

        }
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {

        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();

        final long DURATION_MS = 3000;

        final Interpolator interpolator = new AccelerateDecelerateInterpolator();

        final LatLng startPosition = marker.getPosition();

        handler.post(new Runnable() {

            @Override

            public void run() {

                float elapsed = SystemClock.uptimeMillis() - start;

                float t = elapsed/DURATION_MS;

                float v = interpolator.getInterpolation(t);



                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;

                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;

                marker.setPosition(new LatLng(currentLat, currentLng));



                // if animation is not finished yet, repeat

                if (t < 1) {

                    handler.postDelayed(this, 16);

                }

            }

        });

    }
}
