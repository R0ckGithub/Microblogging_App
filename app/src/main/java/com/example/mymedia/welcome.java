package com.example.mymedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class welcome extends AppCompatActivity {

    FirebaseAuth mAuth =FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "welcome";
    public static user_auth_data user1 = new user_auth_data();
    public static String blank_pic_url = "https://firebasestorage.googleapis.com/v0/b/micro-blogger-9313.appspot.com/o/user_profile_pic%2Fblank-profile-picture-973460_1280.webp?alt=media&token=b533ceba-29e1-4d7d-b73f-bed9d1b526c4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    firebaseauthsetup();
        user1.setUser_uid(FirebaseAuth.getInstance().getUid());
        if(FirebaseAuth.getInstance().getUid()!=null)user1.setAuthuser();


    }

    void firebaseauthsetup()
    {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null ) {
                    Log.d(TAG, "onAuthStateChanged: user not null");
                    if(user.isEmailVerified())
                        //     Toast.makeText(getBaseContext(), "Signed in", Toast.LENGTH_SHORT).show();
                        callmainui();
                } else {
                    Log.d(TAG, "onAuthStateChanged: null user");
                    callsigin();
                }
            }
        };
    }

    //Calling Sigin Activity
    public void callsigin() {
        Log.d(TAG, "callsigin: ");
        Intent intent = new Intent(this, signin.class);
        startActivity(intent);
    }

    //call mainUI
    public void callmainui() {
        Log.d(TAG, "callmainui: ");
        Intent intent = new Intent(this, mainUI.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {

        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

}