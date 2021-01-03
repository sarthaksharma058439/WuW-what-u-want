package com.rakibul.haque.whatuwant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakibul.haque.whatuwant.Model.Users;
import com.rakibul.haque.whatuwant.Prevalent.Prevalent;

public class welcomeActivity extends AppCompatActivity {

    private Button Login,Register;
    TextView Skip;
    DatabaseReference mref;
    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Login=(Button)findViewById(R.id.login);
        Register=(Button)findViewById(R.id.register);
        Skip=(TextView)findViewById(R.id.skip);

        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3291596984753787/9308373332");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed() {

                startActivity(new Intent(welcomeActivity.this,RegisterActivity.class));
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


        mref= FirebaseDatabase.getInstance().getReference();
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(welcomeActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(interstitialAd.isLoaded())
                {
                    interstitialAd.show();
                }
                else
                {
                    startActivity(new Intent(welcomeActivity.this,RegisterActivity.class));
                }
            }
        });
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Users").child("+911111111111").exists())
                        {
                            Users userdata=dataSnapshot.child("Users").child("+911111111111").getValue(Users.class);
                            Intent i=new Intent(welcomeActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser=userdata;
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

    }
}
