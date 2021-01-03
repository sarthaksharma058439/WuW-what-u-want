package com.rakibul.haque.whatuwant;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakibul.haque.whatuwant.Prevalent.Prevalent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    String RequestedWorker;
    EditText message;
    Button Send;


    RadioGroup radioGroup;
    RadioButton radioButton;
    DatabaseReference mref;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        RequestedWorker=getIntent().getStringExtra("RequestedWorker");

        Send=(Button)findViewById(R.id.send);
        message=(EditText)findViewById(R.id.sms);

        LoadingBar=new ProgressDialog(this);
        mref= FirebaseDatabase.getInstance().getReference();

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);




        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if ( dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address").getValue().equals(""))
                {

                }
                else
                {
                    final AlertDialog.Builder builder=new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("WUW Assistant!");
                    builder.setMessage("Do You Want To Use Address that you use Last Time");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String myadd=dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address").getValue().toString();
                                    message.setText(myadd);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(message.getText().toString()))
                {
                    Toast.makeText(MessageActivity.this, "Please provide your Address..", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(Prevalent.currentOnlineUser.getPhone().equals("+911111111111"))
                    {
                        Intent i=new Intent(MessageActivity.this,LoginActivity.class);
                        startActivity(i);
                        Toast.makeText(MessageActivity.this, "Login First..", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        int radioId=radioGroup.getCheckedRadioButtonId();
                        radioButton=findViewById(radioId);


                        LoadingBar.setTitle("Please Wait..");
                        LoadingBar.setMessage("Sending message..");
                        LoadingBar.show();

                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        Date today = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                        String dateToStr = format.format(today);
                        //System.out.println(dateToStr);

                        String SMSmessage=message.getText().toString();
                        String send="My name is "+ Prevalent.currentOnlineUser.getName()+" i need a "+RequestedWorker
                                +" at address "+SMSmessage+" and i complete payment by "+radioButton.getText().toString()
                                +" mode and my phone number is "+Prevalent.currentOnlineUser.getPhone();

                        Map<String,Object> map=new HashMap<String,Object>();
                        map.put(Prevalent.currentOnlineUser.getPhone()+System.currentTimeMillis(),dateToStr+System.currentTimeMillis()+"\n"+send);

                        mref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                dataSnapshot.getRef().child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address").setValue(message.getText().toString().trim());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        mref.child("Services").updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(MessageActivity.this, "Message Send Successfully..", Toast.LENGTH_SHORT).show();
                                            LoadingBar.dismiss();
                                            Intent i =new Intent(MessageActivity.this,HomeActivity.class);
                                            startActivity(i);

                                        }
                                        else
                                        {
                                            LoadingBar.dismiss();
                                            Toast.makeText(MessageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    }

                }

            }

        });
    }
}
