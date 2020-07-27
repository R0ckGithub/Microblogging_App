package com.example.mymedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public static String user="developer_nobita";
    public static String user_url="https://scontent.fdel27-1.fna.fbcdn.net/v/t1.0-1/c0.0.160.160a/p160x160/51392143_162927194689732_2938163633722490880_o.jpg?_nc_cat=105&_nc_sid=dbb9e7&_nc_ohc=McZG1S0mlGYAX8fN6oN&_nc_ht=scontent.fdel27-1.fna&oh=b56b7535ec2726e9f6ca83f2cb533d0b&oe=5F39FCE1";

    public static user_auth_data user1=new user_auth_data(user,user_url);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference mDocRef = db.collection("users").document("md_saif");
        final String TAG = "Firebase";

        //debugiing
        Button btn1;
        final Button btn2;
        final Button btn3;
        btn1=(Button) findViewById(R.id.button_login);
        btn2=(Button) findViewById(R.id.button_mainUI);
        btn3=(Button) findViewById(R.id.button_search);


    }
}
