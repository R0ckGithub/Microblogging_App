package com.example.mymedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_pic;
    TextView profile_bio ,profile_user_name,profile_real_name;
    Button post_btn,followers_btn,following_btn;
    ImageButton back_btn;

    String user_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        if(getIntent().hasExtra("user_name")) {
            user_name = getIntent().getStringExtra("user_name");
        }
        else
        {
            user_name=MainActivity.user1.authuser;
        }
        profile_bio = (TextView ) findViewById(R.id.profile_bio);
        profile_user_name = (TextView) findViewById(R.id.profile_user_name);
        profile_real_name = (TextView) findViewById(R.id.profile_real_name);
        profile_pic = (ImageView) findViewById(R.id.profile_user_pic);
        post_btn = (Button) findViewById(R.id.profile_post_btn);
        followers_btn = (Button ) findViewById(R.id.profile_followers_btn);
        following_btn = (Button) findViewById(R.id.profile_following_btn);
        back_btn = (ImageButton) findViewById(R.id.profile_back_Button);


        profile_user_name.setText(user_name);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profile_bio.setText(documentSnapshot.get("bio").toString());
                        profile_real_name.setText(documentSnapshot.get("real_name").toString());
                        post_btn.setText(documentSnapshot.get("post_cnt").toString()+"\nposts");
                        followers_btn.setText(documentSnapshot.get("followers").toString()+"\nfollowers");
                        following_btn.setText(documentSnapshot.get("following").toString()+"\nfollowing");

                        initImageLoader();
                        UniversalImageLoader.setImage(documentSnapshot.get("user_pic_url").toString(),profile_pic
                                ,null,"");


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
                final LikeFragment likeFragment = new LikeFragment(LikeFragment.PROFILE_PAGE_FOLLOWERS,null,null);
                likeFragment.show(getSupportFragmentManager(),"Profile_fragment");
            }
        });

        //following vtn
        following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LikeFragment likeFragment = new LikeFragment(LikeFragment.PROFILE_PAGE_FOLLOWING,null,null);
                likeFragment.show(getSupportFragmentManager(),"Profile_fragment");
            }
        });

        //post_btn
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),mainUI.class);
                intent.putExtra("Request_Code",mainUI.MYPOST);
                startActivity(intent);
            }
        });

    }
    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getBaseContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    
}
