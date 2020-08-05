package com.example.mymedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Profiledetails extends AppCompatActivity {
    private static final String TAG = "Profiledetails";

    private static FirebaseFirestore db =  FirebaseFirestore.getInstance();
    private EditText username_txt,realname_txt,bio_txt;
    private Button save_btn;
    private String username,realname,bio;
    private TextView user_pic ;
    private static FirebaseAuth mAuth =  FirebaseAuth.getInstance();
    private Context mContext = this;
    static final String profile_camera = "user_pic_updater";
    public static user_auth_data NEW_user = new user_auth_data();
    public static String user_pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetails);

        username_txt =  findViewById(R.id.username_txt);
        realname_txt = findViewById(R.id.realname_txt);
        bio_txt = findViewById(R.id.bio_txt);
        save_btn = findViewById(R.id.detail_btn_save);
        user_pic = findViewById(R.id.user_pic_upload);


        //select image
        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CameraFragment cameraFragment = new CameraFragment();
                cameraFragment.show(getSupportFragmentManager(), profile_camera);
            }
        });


        //save deatils
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = username_txt.getText().toString();
                realname = realname_txt.getText().toString();
                bio = bio_txt.getText().toString();

                if(username.isEmpty() || realname.isEmpty() || bio.isEmpty())
                {
                    Toast.makeText(getBaseContext(),"All fields are required",Toast.LENGTH_SHORT);
                }
                else {


                    if (username.length() > 30) {
                        username = username.substring(0, 30);
                        Toast.makeText(Profiledetails.this, "Handle Trunctaed to 30 chars", Toast.LENGTH_LONG).show();
                    }
                    if (realname.length() > 30) {
                        realname = realname.substring(0, 30);
                        Toast.makeText(Profiledetails.this, "Name Trunctaed to 30 chars", Toast.LENGTH_LONG).show();
                    }
                    if (username.length() > 150) {
                        bio = bio.substring(0, 150);
                        Toast.makeText(Profiledetails.this, "Handle Trunctaed to 150 chars", Toast.LENGTH_LONG).show();
                    }

                    Log.d(TAG, "onClick: user_pic_url" + user_pic_url);

                    NEW_user.setAuthuser(username);
                    NEW_user.setReal_name(realname);
                    NEW_user.setBio(bio);
                    if(NEW_user.authuser_url == null)
                    {
                        Toast.makeText(getBaseContext(), "No Profile Pic Uploaded", Toast.LENGTH_SHORT);
                        NEW_user.setAuthuser_url(welcome.blank_pic_url);
                    }

                    username_available();
                }

            }
        });
    }

    //user_name available or not
    private void username_available()
    {
        db
                .collection("users")
                .whereEqualTo("user_name",username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: user_name checker"+task.getResult().size());
                            if(task.getResult().size()==0) {
                                Map<String,Object> user_data = new HashMap<>();
                                user_data.put("user_name",NEW_user.authuser);
                                user_data.put("real_name",NEW_user.real_name);
                                user_data.put("bio",NEW_user.bio);
                                user_data.put("user_pic_url",NEW_user.authuser_url);
                                user_data.put("post_cnt",0);
                                user_data.put("followers",0);
                                user_data.put("following",0);

                                db
                                        .collection("users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .set(user_data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: new user created");
                                                Toast.makeText(getBaseContext(),"Details Saved",Toast.LENGTH_SHORT);

                                                Intent intent = new Intent(getBaseContext(),mainUI.class);
                                                intent.putExtra("new_user",1);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: user not created",e);
                                                Toast.makeText(getBaseContext(),"Error in saving details",Toast.LENGTH_SHORT);
                                            }
                                        });
                            }

                            else
                            {
                                Toast.makeText(Profiledetails.this, "Handle Not Available", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }



}