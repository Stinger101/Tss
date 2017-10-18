package com.example.johnrarui.tss;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
//import android.support.v7.widget.SearchView;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
//from net
import android.app.SearchManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String ad,pickup;
    Button searchView,book;
    EditText source,destination;
    ProgressDialog progressDialog ;
    TextView mat;
    Geocoder geocoder;
    UserSideActivity userSideActivity;
    private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        userSideActivity=new UserSideActivity();



        progressDialog=new ProgressDialog(this);
        book=(Button)findViewById(R.id.buttonInitiate);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        GeoFire geoFire = new GeoFire(ref);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setContentView(R.layout.activity_mapsupport);

                searchView = (Button) findViewById(R.id.SearchViewSearch);
                searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        source=(EditText)findViewById(R.id.editTextSource);
                        destination=(EditText)findViewById(R.id.editTextSearch);

                        pickup = source.getText().toString();

                        ad = destination.getText().toString();


                        progressDialog.setMessage("loading...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        searchy();
                        progressDialog.dismiss();


                    }
                });
            }});




    }

    public void searchy() {


        geocoder=new Geocoder(this,Locale.getDefault());


        try {

            List<Address> addresses = geocoder.getFromLocationName(ad,5);
            List<Address> addresses1 = geocoder.getFromLocationName(pickup,5);
            if (addresses.size() > 0)
            {

                Double lat = (double) (addresses.get(0).getLatitude());
                Double lon = (double) (addresses.get(0).getLongitude());
                Double lat1 = (double) (addresses1.get(0).getLatitude());
                Double lon1 = (double) (addresses1.get(0).getLongitude());

                Log.d("lat-long", "" + lat + "......." + lon);
                Log.d("lat-long", "" + lat1 + "......." + lon1);
                Toast.makeText(this, "lat-long" + lat + "......." + lon, Toast.LENGTH_SHORT).show();

                final LatLng dest = new LatLng(lat, lon);
                final LatLng dest1=new LatLng(lat1,lon1);
                Distancemat distancemat=new Distancemat();
                distancemat.execute();

//        set the layout to maps
//                setContentView(R.layout.activity_maps);
        /*used marker for show the location */
//                mMap.addMarker(new MarkerOptions()
//                        .position(dest)
//                        .title("MAEKER SET")
//                );
//                mMap.addMarker(new MarkerOptions()
//                        .position(dest1)
//                        .title("MAEKER SET")
//                );
//                // Move the camera instantly to hamburg with a zoom of 15.
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));
//                String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                        "origins="+ad+
//                        "&destinations="+pickup+

//                        "&mode=driving" +
//                        "&key=" + API_KEY;
//                String response=this.run(url_request);
//                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                //mat.setText(response);


                // Zoom in, animating the camera.
                // map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public String run(String url) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }



    public class Distancemat extends AsyncTask<String,Void,String> {
       // private String ad,pickup;
       // private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";

        @Override
        protected String doInBackground(String... url) {

            String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    "origins="+ad+
                    "&destinations="+pickup+
                    "&mode=driving" +
                    "&key=" + API_KEY;

            String response= null;
            try {
                response = this.run(url_request);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            return response ;
        }
        public String run(String url) throws IOException, JSONException {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String res=response.body().string();
            JSONObject js=new JSONObject(res);
            return js.getString("distance");

        }
        protected void onPostExecute(String message) {
            //process message

            Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
        }



    }

//    public static void main(String[] args) throws IOException {
//
//        String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC%7CSeattle&destinations=San+Francisco%7CVictoria+BC&mode=bicycling&language=fr-FR&key=" + API_KEY;
//
//        String response = request.run(url_request);
//        System.out.println(response);
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
}


