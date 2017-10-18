package com.example.johnrarui.tss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RequestActivity extends AppCompatActivity {
    Button searchView;
    EditText source,destination,vehicletype,cargotype;
    String pickup,stop,vehicle,cargo;
    Bundle bundle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        bundle = new Bundle();
        searchView = (Button) findViewById(R.id.SearchViewSearch);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                source = (EditText) findViewById(R.id.editTextSource);
                destination = (EditText) findViewById(R.id.editTextSearch);
                vehicletype=(EditText)findViewById(R.id.editTextVehicleType);
                cargotype=(EditText)findViewById(R.id.editTextCargoType);

                pickup = source.getText().toString();
                stop = destination.getText().toString();
                vehicle =vehicletype.getText().toString();
                cargo = cargotype.getText().toString();

                Intent intent=new Intent(RequestActivity.this,OrderActivity.class);
                bundle.putString("stop",pickup);
                bundle.putString("pickup",stop);
                bundle.putString("vehicle",vehicle);
                bundle.putString("cargo",cargo);
                intent.putExtras(bundle);
//                intent.putExtra("ad",ad);
//                intent.putExtra("pickup",pickup);
                startActivity(intent);



//                progressDialog.setMessage("loading...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                            finishAndRemoveTask();
////
//
//                        }

//
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef = database.getReference("message");
//
//                        myRef.setValue();





            }
        });

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



}
