package com.example.mymedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    ImageView profile_pic;
    TextView profile_bio, profile_user_name, profile_real_name;
    Button post_btn, followers_btn, following_btn,logout_btn;
    ImageButton back_btn;
    private FirebaseAuth mAuth;
    String UID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        profile_bio = findViewById(R.id.profile_bio);
        profile_user_name = findViewById(R.id.profile_user_name);
        profile_real_name = findViewById(R.id.profile_real_name);
        profile_pic = findViewById(R.id.profile_user_pic);
        post_btn = findViewById(R.id.profile_post_btn);
        followers_btn = findViewById(R.id.profile_followers_btn);
        following_btn = findViewById(R.id.profile_following_btn);
        back_btn = findViewById(R.id.profile_back_Button);
        logout_btn = findViewById(R.id.logout_btn);
        mAuth = FirebaseAuth.getInstance();




        if (getIntent().hasExtra("uid")) {
            UID = getIntent().getStringExtra("uid");

        } else {
            UID= welcome.user1.getUser_uid();
        }



        //todo : proper functioning and remove misuse(opening others posts and changes in main ui /timeline )
        post_btn.setClickable(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(UID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profile_user_name.setText(documentSnapshot.get("user_name").toString());
                        profile_bio.setText(documentSnapshot.get("bio").toString());
                        profile_real_name.setText(documentSnapshot.get("real_name").toString());
                        post_btn.setText(documentSnapshot.get("post_cnt").toString() + "\nposts");
                        followers_btn.setText(documentSnapshot.get("followers").toString() + "\nfollowers");
                        following_btn.setText(documentSnapshot.get("following").toString() + "\nfollowing");

                        initImageLoader();
                        UniversalImageLoader.setImage(documentSnapshot.get("user_pic_url").toString(), profile_pic
                                , null, "");


                    }
                });


        //back_btn
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //followers btn
        followers_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LikeFragment likeFragment = new LikeFragment(LikeFragment.PROFILE_PAGE_FOLLOWERS, null, null);
                likeFragment.show(getSupportFragmentManager(), "Profile_fragment");
            }
        });

        //following vtn
        following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LikeFragment likeFragment = new LikeFragment(LikeFragment.PROFILE_PAGE_FOLLOWING, null, null);
                likeFragment.show(getSupportFragmentManager(), "Profile_fragment");
            }
        });

        //TODO : My posts Viewer
        //post_btn
        /*post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), mainUI.class);
                intent.putExtra("Request_Code", mainUI.MYPOST);
                startActivity(intent);
            }
        });*/

        //Logout btn
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: User Sigining out");
                Toast.makeText(ProfileActivity.this,"Signed Out Successfully",Toast.LENGTH_LONG);
                mAuth.signOut();
                finish();
            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getBaseContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}
