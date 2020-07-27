package com.example.mymedia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class mainUI extends AppCompatActivity implements mrecylceviewAdapter.OnNoteListner {

    public Context mContext=this;
    RecyclerView mrecyclerview1;
    public ArrayList<post_data> newsfeed_data=new ArrayList<>();
    DatabaseFun databaseFun = new DatabaseFun();
    public mrecylceviewAdapter madapter;
    Map<String, Object> follow_data = new HashMap<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static int Request_Code=0,MYPOST=1,TIMELINE=0;

    //Bug removing
    static int refresh_cnt=0;


    private static final String TAG = "firestore_data_fetch";
    private mrecylceviewAdapter.OnNoteListner monNoteListner;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_u_i);

        //Request Code Setter
        if(getIntent().hasExtra("Request_Code"))
        {
            Request_Code = getIntent().getIntExtra("Request_Code",0);
        }


        mrecyclerview1=(RecyclerView) findViewById(R.id.news_feed);
        mrecyclerview1.setLayoutManager(new LinearLayoutManager(this));
        madapter=new mrecylceviewAdapter(newsfeed_data,mContext,this);
        mrecyclerview1.setAdapter(madapter);

        // Request code check
        if(Request_Code == TIMELINE)
            timeline_post_fetch();
        if(Request_Code == MYPOST)
            my_posts_fetch();

        Log.d(TAG, "Removing default fragment "+Integer.toString(newsfeed_data.size()));
        remove_fragment("Like_Fragment");

        ImageButton profile_btn = (ImageButton) findViewById(R.id.profile_btn);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainUI.this,ProfileActivity.class);
                mainUI.this.startActivity(intent);
            }
        });

        ImageButton add_btn = (ImageButton) findViewById(R.id.add_btn);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CameraFragment cameraFragment = new CameraFragment();
                cameraFragment.show(getSupportFragmentManager(),"camera_fragment");
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
   void remove_fragment(String Tag)
    {
        Log.d(TAG, "remove_fragment: ");
        findViewById(R.id.mainui_fragment).setVisibility(View.GONE);
    }


    // fetching all documents for newsfeed

    //fetchiong timeline data
    void timeline_post_fetch()
    {
        db.collection("users")
                .document(MainActivity.user1.authuser)
                .collection("timeline")
                .orderBy("time_stamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: succesful data fetch");
                            ArrayList<String> post_ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                post_ids.add(document.get("post_id").toString());
                            }
                            data_intialisation_newsfeed(post_ids);
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: unsucessful timeline data fetch");
                        }
                    }
                });


    }
    void my_posts_fetch()
    {
        db.collection("post")
                .whereEqualTo("user_name",MainActivity.user1.authuser)
//  todo: Limiting posts              .limit(20)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: succesful my post data fetch");
                            ArrayList<String> post_ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                post_ids.add(document.getId());
                            }
                            data_intialisation_newsfeed(post_ids);
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: unsucessful my post data fetch");
                        }
                    }
                });
    }

    void data_intialisation_newsfeed(final ArrayList<String> post_ids) {
        Log.d(TAG, "data_intialisation_newsfeed11: "+post_ids.size());

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
                                    newsfeed_data.get(position).set_User_pic_url(madapter,position);
                                    newsfeed_data.get(position).getPost_like_state(madapter,position);
                                    Log.d("Post id", "onComplete: " + newsfeed_data.get(newsfeed_data.size() - 1).post_id);
                                }
                            } else {
                                Log.d(TAG, "onComplete: unsucesssful post data fetch" + post_ids.get(finalI));
                            }
                        //    madapter.notifyDataSetChanged();
                        }
                    });
            Log.d(TAG, "data_intialisation_newsfeed: "+newsfeed_data.size());

        }

    }


                        /*
                        Log.d(TAG, "onComplete99: "+Integer.toString(newsfeed_data.size()));
                        //getting post like state
                        for(int i=0;i<newsfeed_data.size();++i)
                        {
                            newsfeed_data.get(i).getPost_like_state();
                        }

                        recycler_adapter_setter(newsfeed_data);*/



   /* void recycler_adapter_setter(ArrayList<post_data> data1)
    {
        Log.d(TAG, "recycler_adapter_setter: "+Integer.toString(data1.size()));

            Log.d(TAG, "recycler_adapter_setter: ");

    }

    //post_like_state
    void post_like_state_setter(ArrayList<post_data> data)
    {
        Log.d(TAG, "post_like_state_setter: "+data.size());
        final ArrayList<post_data> newsfeed_data = data;
        final String TAG="POST_like_state_fun";
        for(int i=0;i<newsfeed_data.size();++i) {
            DocumentReference mDocref = db
                    .collection("post")
                    .document(data.get(i).getPost_id());
            if (mDocref != null) {
                final int finalI = i;
                mDocref
                        .collection("likes")
                        .whereEqualTo("user_name", MainActivity.user1.authuser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot doc = task.getResult();
                                    if (doc.isEmpty()) {
                                        Log.d(TAG, "onComplete: doc empty");
                                        newsfeed_data.get(finalI).setPost_liked(false);
                                    } else {
                                        Log.d(TAG, "onComplete: doc not empty" + doc.getDocuments());
                                        newsfeed_data.get(finalI).setPost_liked(true);
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: Unsuccesful task");
                                }
                            }
                        });
            }
        }
        user_pic_setter(newsfeed_data);
    }


    //user_pic_setter
    void user_pic_setter(ArrayList<post_data> data1) {
        final String TAG = "User_pic_setter";
        final ArrayList<post_data> data = data1;
        for (int i = 0; i < data.size(); ++i) {

            final int finalI = i;
            db
                    .collection("users")
                    .document(data.get(i).user_name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String url = document.get("user_pic_url").toString();

                                    data.get(finalI).setUser_pic_url(url);
                                    Log.d(TAG, "URLSnapshot data: " + data.get(data.size() - 1)
                                            .user_pic_url);
                                } else {
                                    Log.d(TAG, "No such URL document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }


                        }
                    });
        }
        newsfeed_data = data;
        recycler_adapter_setter(data);

    }*/

    //Adapter Click Response


    @Override
    public void OnNoteClick(final int position, int id) {
        Log.d(TAG, "OnNoteClick: "+Integer.toString(id));


        if(id==R.id.redlike_btn || id==R.id.whitelike_btn)
        {
         /*  ImageButton redlikebtn = (ImageButton)findViewById(R.id.redlike_btn);
            ImageButton whitelikebtn = (ImageButton) findViewById(R.id.whitelike_btn);


          String user_name=newsfeed_data.get(position).user_name;
            String post_id = newsfeed_data.get(position).post_id;
            if(redlikebtn.getVisibility()==View.INVISIBLE)
            {
                redlikebtn.setVisibility(View.VISIBLE);
                whitelikebtn.setVisibility(View.INVISIBLE);
                if(databaseFun.post_liked(post_id,MainActivity.user1.authuser))
                {
                    Log.d(TAG, "OnNoteClick: red_heart");
                }

                //Updating likes on a post
                newsfeed_data.get(position).setPost_liked(true);
                TextView likescnt= (TextView) findViewById(R.id.likes_count);
                newsfeed_data.get(position).likes_cnt++;
                likescnt.setText(newsfeed_data.get(position).likes_cnt+" Likes");
                madapter.notifyItemChanged(position);



            }
            else
            if(redlikebtn.getVisibility()==View.VISIBLE)
            {
                Log.d(TAG, "OnNoteClick: heart");
                redlikebtn.setVisibility(View.INVISIBLE);
                whitelikebtn.setVisibility(View.VISIBLE);
                databaseFun.post_disliked(post_id,MainActivity.user1.authuser);


                newsfeed_data.get(position).setPost_liked(false);
                TextView likescnt= (TextView) findViewById(R.id.likes_count);
                newsfeed_data.get(position).likes_cnt--;
                likescnt.setText(newsfeed_data.get(position).likes_cnt+" Likes");
                madapter.notifyItemChanged(position);



            }*/

            String post_id = newsfeed_data.get(position).post_id;

            if(newsfeed_data.get(position).post_liked!=null)
         {
             if(newsfeed_data.get(position).post_liked==false)
             {
                 Log.d(TAG, "OnNoteClick: post liked by u");
                 newsfeed_data.get(position).setPost_liked(true);
                 newsfeed_data.get(position).likes_cnt++;
                 databaseFun.post_liked(post_id,MainActivity.user1.authuser);
                 mrecylceviewAdapter.newsfeed_data_setter(newsfeed_data);
                 madapter.notifyItemChanged(position);
             }
             else
             {
                 Log.d(TAG, "OnNoteClick: post disliked by u");
                 newsfeed_data.get(position).setPost_liked(false);
                 newsfeed_data.get(position).likes_cnt--;
                 databaseFun.post_disliked(post_id,MainActivity.user1.authuser);
                 mrecylceviewAdapter.newsfeed_data_setter(newsfeed_data);
                 madapter.notifyItemChanged(position);

             }
         }



            //TODO: iss post_id ke like section main iss username ko add kar
        //TODO: dolamanee will give u the username
        // (authentication ) se
    }
    else
    if(id==R.id.comment_btn || id==R.id.comment_fragment_btn)
    {


        final CommentFragment commentFragment = new CommentFragment(newsfeed_data.get(position).post_id);
        commentFragment.show(getSupportFragmentManager(),"Comment_fragment");
        if(commentFragment.mcommentadapter!=null)    commentFragment.mcommentadapter.notifyDataSetChanged();

    }
    else
    if(id==R.id.likes_count)
    {
        final LikeFragment likeFragment = new LikeFragment(LikeFragment.MAINUI,newsfeed_data.get(position).post_id,null);
        likeFragment.show(getSupportFragmentManager(),"Comment_fragment");
     /*   final FirebaseFirestore db= FirebaseFirestore.getInstance();




        CollectionReference mColRef = db.collection("post")
                .document(newsfeed_data.get(position).post_id).collection("likes");

        mColRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final ArrayList<likes_fragment_data> data_like = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: =>" + document.getData());
                                data_like.add(document.toObject(likes_fragment_data.class));
                            }

                        } else {
                            Log.d(TAG, "Unsuccesul fetching likes data");
                            Toast.makeText(getBaseContext(), "No Likes On this post", Toast.LENGTH_SHORT).show();
                        }


                        final String TAG1 = "follow_state_fun";


                        db
                                .collection("AfollowsB")
                                .document(MainActivity.user1.authuser)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Log.d(TAG1, document.getId() + " => " + document.getData());
                                            follow_data = document.getData();
                                        } else {
                                            Log.d(TAG1, "Error getting documents: ", task.getException());
                                        }
                                    }


                                });
                        Log.d(TAG, "onComplete: " + follow_data.size());
                        for (int i = 0; i < data_like.size(); ++i) {
                            if (follow_data.containsKey(data_like.get(i).getUser_name())) {
                                Log.d(TAG, "onComplete: following");
                                data_like.get(i).setFollow(true);
                            } else {
                                Log.d(TAG, "onComplete: Not following");
                                data_like.get(i).setFollow(false);

                            }
                        }

                        for(int i=0;i<data_like.size();++i)
                        {

                            final int finalI = i;
                            db
                                    .collection("users")
                                    .document(data_like.get(i).user_name)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String url=document.get("user_pic_url").toString();

                                                    data_like.get(finalI).setUser_pic_url(url);
                                                    Log.d(TAG, "URLSnapshot data: " + data_like.get(data_like.size()-1).user_pic_url);
                                                } else {
                                                    Log.d(TAG, "No such URL document");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }

                                    });



                        }
                        like_fragment_caller(data_like);
                    }


                });


*/

    }
    else
    if(id==R.id.username_btn)
    {
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("user_name",newsfeed_data.get(position).getUser_name());
        startActivity(intent);
    }
    else
    if(id==R.id.share_btn)
    {
        //TODO: to be done later
    }
    else
    if(id==R.id.bookmark_btn)
    {
        //Todo: Tobe done later
    }

    }


}