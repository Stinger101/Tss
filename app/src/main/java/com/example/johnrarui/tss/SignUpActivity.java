package com.example.johnrarui.tss;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextFirstName,editTextSecondName,editTextPhoneNumber,editTextEmail,editTextPassword
            ,editTextConfirmPassword;
    Button buttonSignUp;
    ProgressDialog progressDialog;
    String firstname,secondname,phonenumber,emaill,password,confirmpassword;
    DatabaseReference databaseReference;
    String uid;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //INITIALISING DATABASE
        databaseReference=FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        //EDIT TEXT INITIALIZATION
        editTextFirstName=(EditText) findViewById(R.id.editTextFirstName);
        editTextSecondName=(EditText) findViewById(R.id.editTextSecondName);
        editTextPhoneNumber=(EditText) findViewById(R.id.editTextPhoneNumber);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword=(EditText) findViewById(R.id.editTextConfirmPassword);
        //BUTTON INITIALISE
        buttonSignUp=(Button)findViewById(R.id.buttonSignUp);
        progressDialog=new ProgressDialog(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname=editTextFirstName.getText().toString();
                secondname=editTextSecondName.getText().toString();
                phonenumber=editTextPhoneNumber.getText().toString();
                emaill=editTextEmail.getText().toString();
                password=editTextPassword.getText().toString();
                confirmpassword=editTextConfirmPassword.getText().toString();

                if (emaill.equals("")||password.equals("")||firstname.equals("")||secondname.equals("")||phonenumber.equals("")){
                    Toast.makeText(SignUpActivity.this, "please fill in all details", Toast.LENGTH_SHORT).show();
                }else if(!confirmpassword.equals(password)){
                    Toast.makeText(SignUpActivity.this, "please confirm password", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(emaill,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Unable to sign up", Toast.LENGTH_SHORT).show();

                            }else{
                                uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Map map=new HashMap();
                                map.put("firstname",firstname);
                                map.put("secondname",secondname);
                                map.put("email",emaill);
                                map.put("phonenumber",phonenumber);

                                databaseReference.child("users").child(uid).child("profile_info").setValue(map);
                                Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();


                            }
                        }
                    });



                }

            }
        });
    }
}
