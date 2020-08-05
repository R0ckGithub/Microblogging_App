package com.example.mymedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class myPosts extends AppCompatActivity implements mrecylceviewAdapter.OnNoteListner {

    public Context mContext = this;
    RecyclerView mrecyclerview1;
    public ArrayList<post_data> newsfeed_data = new ArrayList<>();
    DatabaseFun databaseFun = new DatabaseFun();
    public mrecylceviewAdapter madapter;
    Map<String, Object> follow_data = new HashMap<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    //Bug removing
    static int refresh_cnt = 0;


    private static final String TAG = "Myposts";
    private mrecylceviewAdapter.OnNoteListner monNoteListner;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_u_i);


        mrecyclerview1 = (RecyclerView) findViewById(R.id.news_feed);
        mrecyclerview1.setLayoutManager(new LinearLayoutManager(this));
        madapter = new mrecylceviewAdapter(newsfeed_data, mContext, this);
        mrecyclerview1.setAdapter(madapter);

        // Request code check


        Log.d(TAG, "Removing default fragment " + Integer.toString(newsfeed_data.size()));
        remove_fragment("Like_Fragment");

        ImageButton profile_btn = (ImageButton) findViewById(R.id.profile_btn);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myPosts.this, ProfileActivity.class);
                myPosts.this.startActivity(intent);
            }
        });

        ImageButton add_btn = (ImageButton) findViewById(R.id.add_btn);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CameraFragment cameraFragment = new CameraFragment();
                cameraFragment.show(getSupportFragmentManager(), "camera_fragment");
            }
        });

        ImageButton logo_btn = (ImageButton) findViewById(R.id.logo);

        logo_btn.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onHover: ");
                madapter.notifyDataSetChanged();
                return false;
            }
        });
        logo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                madapter.notifyDataSetChanged();
            }
        });

    }


    //removing default fragment
    void remove_fragment(String Tag) {
        Log.d(TAG, "remove_fragment: ");
        findViewById(R.id.mainui_fragment).setVisibility(View.GONE);
    }


    // fetching all documents for newsfeed

    //fetchiong timeline data
    void timeline_post_fetch() {
        db.collection("users")
                .document(welcome.user1.user_uid)
                .collection("timeline")
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


    }

    void my_posts_fetch() {
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
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (newsfeed_data.add(document.toObject(post_data.class))) {
                                    int position = newsfeed_data.size() - 1;
                                    newsfeed_data.get(position).setPost_id(document.getId());
                                    newsfeed_data.get(position).set_User_pic_url(madapter, position);
                                    newsfeed_data.get(position).getPost_like_state(madapter, position);
                                    Log.d("Post id", "onComplete: " + newsfeed_data.get(newsfeed_data.size() - 1).post_id);
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
                    databaseFun.post_liked(post_id, welcome.user1.user_uid);
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
        } else if (id == R.id.comment_btn || id == R.id.comment_fragment_btn) {


            final CommentFragment commentFragment = new CommentFragment(newsfeed_data.get(position).post_id);
            commentFragment.show(getSupportFragmentManager(), "Comment_fragment");
            if (commentFragment.mcommentadapter != null)
                commentFragment.mcommentadapter.notifyDataSetChanged();

        } else if (id == R.id.likes_count) {
            final LikeFragment likeFragment = new LikeFragment(LikeFragment.MAINUI, newsfeed_data.get(position).post_id, null);
            likeFragment.show(getSupportFragmentManager(), "Comment_fragment");

        } else if (id == R.id.username_btn) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_name", newsfeed_data.get(position).getUser_name());
            startActivity(intent);
        } else if (id == R.id.share_btn) {
            //TODO: to be done later
        } else if (id == R.id.bookmark_btn) {
            //Todo: Tobe done later
        }

    }


}