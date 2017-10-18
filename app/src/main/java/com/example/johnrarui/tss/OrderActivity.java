package com.example.johnrarui.tss;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

/**
 * Created by john rarui on 10/14/2017.
 */

public class OrderActivity extends AppCompatActivity implements OnMapReadyCallback {
        private GoogleMap mMap;
        Geocoder geocoder;
        DatabaseReference thisRef;
        TextView begin,end,vehicleType,pricetag;
        Button order,cancel;
        int price;
        String origin,destination,distance,vehicle,cargotype,time,ad,pickup;
        LatLng first,second;
        JSONObject js;
        Bundle bundle;
    private StorageReference mStorageRef;
    private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchy();


        order = (Button) findViewById(R.id.buttonOrder);
        cancel=(Button)findViewById(R.id.buttonCancel);
        order.setEnabled(false);

        mStorageRef= FirebaseStorage.getInstance().getReference();
        thisRef=FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Read from the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference myRef = database.getReference().child("orders"
//        ).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("orderbegin");
//
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Map<String, String> map = (Map) dataSnapshot.getValue();
//                Map<String, String> map2 = (Map) dataSnapshot.child("origin").getValue();
////                                String origin = map.get("latitude");
////                                String destination=map.get("destination").toString();
//                lat=String.valueOf(map2.get("latitude"));
//                longit=String.valueOf(map2.get("longitude").toString());
//                distance=map.get("distance");
//
//
//
//                //List<Address> addresses = geocoder.getFromLocation(Double.valueOf(map.get("latitude")),Double.valueOf(map.get("latitude")), 1);
//                //begin.setText("Origin="+(String)map.get("latitude"));
//                begin.setText("distance=" + (String) map.get("distance"));
//
//
//                //Log.d(TAG, "Value is: " + value);
//            }

//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                // Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date=new Date();
                time=String.valueOf(date.getTime());


                Map map=new HashMap();
                try {
                    origin=String.valueOf(js.getJSONArray("origin_addresses"));
                    destination=String.valueOf(js.getJSONArray("destination_addresses"));
                    price=12*Integer.valueOf(String.valueOf(js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value")));
                    distance=String.valueOf(js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //map.put("time",ServerValue.TIMESTAMP);

                    map.put("origin", first);
                    map.put("destination", second);
                    map.put("distance", distance);
                    map.put("price", price);

                    Toast.makeText(OrderActivity.this, origin, Toast.LENGTH_SHORT).show();
                    Toast.makeText(OrderActivity.this, destination, Toast.LENGTH_SHORT).show();
                    Toast.makeText(OrderActivity.this, distance, Toast.LENGTH_SHORT).show();
                    Toast.makeText(OrderActivity.this, time, Toast.LENGTH_SHORT).show();
                    thisRef.child("finalorder").child(time).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                View v1=getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap=Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);
                String mPath= Environment.getExternalStorageDirectory().toString()+"/"+time+".jpg";
                File image=new File(mPath);
                try {
                    FileOutputStream outputStream=new FileOutputStream(image);
                    int quality=100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG,quality,outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri file = Uri.fromFile(image);
                StorageReference riversRef = mStorageRef.child("images/"+time+".jpg");

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Toast.makeText(OrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(OrderActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderActivity.this,UserSideActivity.class);
                startActivity(intent);
            }
        });

    }
    public void captureScreen(){
        String mPath= Environment.getExternalStorageDirectory().toString()+"/"+time+".jpg";

        File fle=new File(mPath);
        GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback() {
           Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                String mPath= Environment.getExternalStorageDirectory().toString()+"/"+time+".jpg";

                bitmap=snapshot;
                try {
                    FileOutputStream stream=new FileOutputStream(new File(mPath));
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,stream);
                    stream.flush();
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Uri file = Uri.fromFile(fle);
        StorageReference riversRef = mStorageRef.child("images/map/"+time+".jpg");

        mMap.snapshot(callback);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(OrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(OrderActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }
    public String searchy() {

        bundle=getIntent().getExtras();
        ad=bundle.getString("stop");
        pickup=bundle.getString("pickup");
        vehicle=bundle.getString("vehicle");
        cargotype=bundle.getString("cargo");
        begin = (TextView) findViewById(R.id.textViewOrigin);
        end = (TextView) findViewById(R.id.textViewDest);
        vehicleType = (TextView) findViewById(R.id.textViewVehicleType);
        pricetag = (TextView) findViewById(R.id.textViewPrice);
        geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());



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
                first=dest;
                second=dest1;

                Map_values values=new Map_values();
                values.setFirst(first);
                values.setSec(second);
                distancemat.execute();



            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class Distancemat extends AsyncTask<String,Void,String> {
        // private String ad,pickup;
        // private static final String API_KEY = "AIzaSyCXEPdkGIjQJVXtfZOQwJuIMdlbNWyBEX8";
        JSONObject jsdet;

        @Override
        protected String doInBackground(String... url) {

            String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    "origins="+ad+
                    "&destinations="+pickup+
                    "&mode=driving" +
                    "&key=" + API_KEY;

            JSONObject response= null;
            String respond=null;
            try {
                respond=this.run(url_request);
                //userSideActivity=new UserSideActivity();
                //jsdet=response;
                //onPostExecute(this.run(url_request));


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            return respond ;
        }
        public String run(String url) throws IOException, JSONException {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String res=response.body().string();


            //jsdet=js;
           // price=jsdet.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value").toString();
    //            PriceCalc priceCalc=new PriceCalc();
    //            priceCalc.setDistance(Integer.getInteger(js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value").toString()));
    //            priceCalc.calcPrice(priceCalc.distance,12);
    //            price= priceCalc.getPrice();


            //return String.valueOf(priceCalc.calcPrice(priceCalc.distance,12));
            //return js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value").toString();
    //            return res;
            return res;




        }
        protected void onPostExecute(String message) {
            //process message
            // Write a message to the database
           // String detail="Origin="+first.toString()+"/nDestination="+second.toString()+"/nPrice="+price;
           // begin.setText(detail);
           // Toast.makeText(OrderActivity.this, price, Toast.LENGTH_SHORT).show();

//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference();
//            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
//            //myRef.setValue("Hello, World!");
//
//            String dist=jsdet.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value").toString();
//            //Toast.makeText(userSideActivity, dist, Toast.LENGTH_SHORT).show();
//            //int price=Integer.getInteger(dist)*12;
//            //Toast.makeText(UserSideActivity.this, price, Toast.LENGTH_SHORT).show();
//            Map map=new HashMap();
//            map.put("origin",first);
//            map.put("destination",second);
//            map.put("distance",dist);
//            // map.put("price",price);
            if(message!=null) {
                order.setEnabled(true);
            }
            try {
                jsdet=new JSONObject(message);
                OrderActivity.this.js=jsdet;
                String orig="ORIGIN:  "+String.valueOf(js.getJSONArray("origin_addresses"));
                String destin="DESTINATION:  "+String.valueOf(js.getJSONArray("destination_addresses"));
                String cash="PRICE:  "+String.valueOf(2*Integer.valueOf(String.valueOf(js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("value")))+".00KSH");
                String vtype="VEHICLE:  "+vehicle+"    CARGO:   "+cargotype;

                    OrderActivity.this.begin.setText(orig);
                    OrderActivity.this.end.setText(destin);
                    OrderActivity.this.vehicleType.setText(cash);
                    OrderActivity.this.pricetag.setText(vtype);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//
//            myRef.child("users").child(uid).child("orderbegin").setValue(map);
//            OrderActivity.this.begin.setText(orig);
//            OrderActivity.this.begin.setText(destin);
//            OrderActivity.this.begin.setText(cash);
//            OrderActivity.this.begin.setText(vtype);

            //Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
            //Toast.makeText(OrderActivity.this, price, Toast.LENGTH_SHORT).show();

        }

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Map_values map_values=new Map_values();
        //second=map_values.getSec();
        //first=map_values.getFirst();


        if (first!=null&&second!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(first)
                    .title("Origin"+origin)
            );
            mMap.addMarker(new MarkerOptions()
                    .position(second)
                    .title("destination"+destination)
            );
            // Move the camera instantly to hamburg with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 7));
        }else{
            // Add a marker in Sydney and move the camera
//            LatLng sydney = new LatLng(-34, 151);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }
}