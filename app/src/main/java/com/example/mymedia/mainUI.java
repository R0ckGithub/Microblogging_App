package com.example.mymedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class mainUI extends AppCompatActivity implements mrecylceviewAdapter.OnNoteListner {

    public Context mContext = this;
    RecyclerView mrecyclerview1;
    public ArrayList<post_data> newsfeed_data = new ArrayList<>();
    DatabaseFun databaseFun = new DatabaseFun();
    public mrecylceviewAdapter madapter;
    Map<String, Object> follow_data = new HashMap<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static int Request_Code = 0, MYPOST = 1, TIMELINE = 0;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageButton logo_btn,suggestion_btn;
    //Bug removing
    static int refresh_cnt = 0;
    static final String mainUI_camera = "camera_fragment";


    private static final String TAG = "mainUi";
    private mrecylceviewAdapter.OnNoteListner monNoteListner;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_u_i);

        //Setup auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        newsfeed_data.clear();


        //Request Code Setter
        if (getIntent().hasExtra("Request_Code")) {
            Request_Code = getIntent().getIntExtra("Request_Code", 0);
        }



        mrecyclerview1 = findViewById(R.id.news_feed);
        mrecyclerview1.setLayoutManager(new LinearLayoutManager(this));
        madapter = new mrecylceviewAdapter(newsfeed_data, mContext, this);
        mrecyclerview1.setAdapter(madapter);

        // Request code check
        if (Request_Code == TIMELINE)
            timeline_post_fetch();
        if (Request_Code == MYPOST)
            my_posts_fetch();

        Log.d(TAG, "Removing default fragment " + newsfeed_data.size());
        remove_fragment("Like_Fragment");

        ImageButton profile_btn = (ImageButton) findViewById(R.id.profile_btn);
        suggestion_btn = findViewById(R.id.suggestion_btn);


        //profile
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainUI.this, ProfileActivity.class);
                mainUI.this.startActivity(intent);
            }
        });

        ImageButton add_btn = findViewById(R.id.add_btn);

        //create post
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CameraFragment cameraFragment = new CameraFragment();
                cameraFragment.show(getSupportFragmentManager(), mainUI_camera);
            }
        });

        logo_btn = findViewById(R.id.logo);


        //setup Authststate listner
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null ) {
                    if(user.isEmailVerified()){}
                    //     Toast.makeText(getBaseContext(), "Signed in", Toast.LENGTH_SHORT).show();
                } else {
                    callsigin();
                }
            }
        };

        //suggestions
        suggestion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeFragment likeFragment = new LikeFragment(LikeFragment.NEW_USER,"","");
                likeFragment.show(getSupportFragmentManager(),"new user suggestions");
            }
        });


        //refresh
        logo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),mainUI.class);
                finish();
                startActivity(intent);
            }
        });


    }

    //Calling Sigin Activity 
    public void callsigin() {
        Log.d(TAG, "callsigin: ");
        Intent intent = new Intent(this, signin.class);
        startActivity(intent);
    }


    //removing default fragment
    void remove_fragment(String Tag) {
        Log.d(Tag, "remove_fragment: ");
        findViewById(R.id.mainui_fragment).setVisibility(View.GONE);
    }


    // fetching all documents for newsfeed

    //fetchiong timeline data
    void timeline_post_fetch() {
     /*   if (MainActivity.user1.user_uid == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user==null) callsigin();
            MainActivity.user1.setUser_uid(user.getUid());
            Log.d(TAG, "timeline_post_fetch: "+MainActivity.user1.user_uid);
        }*/


        DocumentReference mDocref = db.collection("users").document(welcome.user1.user_uid);
       // Log.d(TAG, "timeline_post_fetch: " + MainActivity.user1.user_uid);
        if (mDocref != null) {
            mDocref.collection("timeline")
                    .orderBy("time_stamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: succesful data fetch");
                                ArrayList<String> post_ids = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    post_ids.add(document.get("post_id").toString());
                                }
                                data_intialisation_newsfeed(post_ids);
                            } else {
                                Log.d(TAG, "onComplete: unsucessful timeline data fetch");
                            }
                        }
                    });
        } else {
            Log.e(TAG, "timeline_post_fetch:Null user");
        }



}

    void my_posts_fetch() {
        if (welcome.user1.user_uid == null) {
            welcome.user1.setUser_uid(mFirebaseAuth.getCurrentUser().getUid());
        }

        db.collection("post")
                .whereEqualTo("user_name", welcome.user1.authuser)
//  todo: Limiting posts              .limit(20)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: succesful my post data fetch");
                            ArrayList<String> post_ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                post_ids.add(document.getId());
                            }
                            data_intialisation_newsfeed(post_ids);
                        } else {
                            Log.d(TAG, "onComplete: unsucessful my post data fetch");
                        }
                    }
                });
    }

    void data_intialisation_newsfeed(final ArrayList<String> post_ids) {
        Log.d(TAG, "data_intialisation_newsfeed11: " + post_ids.size());

        for (int i = 0; i < post_ids.size(); ++i) {
            final int finalI = i;
            db.collection("post")
                    .document(post_ids.get(finalI))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists())
                                {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (newsfeed_data.add(document.toObject(post_data.class))) {
                                    int position = newsfeed_data.size() - 1;
                                    newsfeed_data.get(position).setPost_id(document.getId());
                                    newsfeed_data.get(position).set_User_pic_url(madapter, position);
                                    newsfeed_data.get(position).getPost_like_state(madapter, position);
                                    Log.d("Post id", "onComplete: " + newsfeed_data.get(newsfeed_data.size() - 1).post_id);
                                }
                                }
                                else 
                                {
                                    Log.d(TAG, "onComplete: no doc exist");
                                }
                            } else {
                                Log.d(TAG, "onComplete: unsucesssful post data fetch" + post_ids.get(finalI));
                            }
                            //    madapter.notifyDataSetChanged();
                        }
                    });
            Log.d(TAG, "data_intialisation_newsfeed: " + newsfeed_data.size());

        }

    }


    //Adapter Click Response


    @Override
    public void OnNoteClick(final int position, int id) {
        Log.d(TAG, "OnNoteClick: " + Integer.toString(id));


        if (id == R.id.redlike_btn || id == R.id.whitelike_btn) {

            String post_id = newsfeed_data.get(position).post_id;

            if (newsfeed_data.get(position).post_liked != null) {
                if (newsfeed_data.get(position).post_liked == false) {
                    Log.d(TAG, "OnNoteClick: post liked by u");
                    newsfeed_data.get(position).setPost_liked(true);
                    newsfeed_data.get(position).likes_cnt++;
                    databaseFun.post_liked(post_id,welcome.user1.user_uid);
                    mrecylceviewAdapter.newsfeed_data_setter(newsfeed_data);
                    madapter.notifyItemChanged(position);
                } else {
                    Log.d(TAG, "OnNoteClick: post disliked by u");
                    newsfeed_data.get(position).setPost_liked(false);
                    newsfeed_data.get(position).likes_cnt--;
                    databaseFun.post_disliked(post_id, welcome.user1.user_uid);
                    mrecylceviewAdapter.newsfeed_data_setter(newsfeed_data);
                    madapter.notifyItemChanged(position);

                }
            }


            //TODO: iss post_id ke like section main iss username ko add kar
            //TODO: dolamanee will give u the username
            // (authentication ) se
        } else if (id == R.id.comment_btn || id == R.id.comment_fragment_btn)
        {


            final CommentFragment commentFragment = new CommentFragment(newsfeed_data.get(position).post_id);
            commentFragment.show(getSupportFragmentManager(), "Comment_fragment");

        } else if (id == R.id.likes_count) {
            final LikeFragment likeFragment = new LikeFragment(LikeFragment.MAINUI, newsfeed_data.get(position).post_id, null);
            likeFragment.show(getSupportFragmentManager(), "Comment_fragment");

        } else if (id == R.id.username_btn) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("uid", newsfeed_data.get(position).getUid());
            startActivity(intent);
        } else if (id == R.id.share_btn) {
            //TODO: to be done later
        } else if (id == R.id.bookmark_btn) {
            //Todo: Tobe done later
        }

    }


    @Override
    public void onResume() {

        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void onBackPressed()
    {
        finish();
    }

}