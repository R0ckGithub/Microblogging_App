package com.example.mymedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LikeFragment extends DialogFragment implements like_fragment_recycle_adapter.OnNoteListner_like {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton close_btn;
    static int cnt = 0;
    private TextView header;
    static Map<String, Object> follow_data = new HashMap<>();
    ArrayList<likes_fragment_data> data_like = new ArrayList<>();
    like_fragment_recycle_adapter adapter;



    private static Context mContext ;
    private static final String TAG = "LikeFragment";
    RecyclerView mrecyclerview2;
    private String post_name, comment_name;

    int Request_Code = -1;
    public static int COMMENT_FRAGMENT = 2, MAINUI = 1, PROFILE_PAGE_FOLLOWERS = 4, PROFILE_PAGE_FOLLOWING = 3 , NEW_USER=5;

    // No argument constructor
    public LikeFragment() {
    }

    //Post_name argumented constuctor
    public LikeFragment(ArrayList<likes_fragment_data> data) {
        this.post_name = post_name;
        data_like = data;
    }

    public LikeFragment(int request_code, @Nullable String post_id, @Nullable String comment_id) {
        Request_Code = request_code;
        if (post_id != null) post_name = post_id;
        if (comment_id != null) comment_name = comment_id;
    }

    public static LikeFragment newInstance(Context mContext) {
        LikeFragment fragment = new LikeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for min width fragment
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fargment_custom_size);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_like, container, false);
        mrecyclerview2 = (RecyclerView) view.findViewById(R.id.like_fragment_recycler);
        mrecyclerview2.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new like_fragment_recycle_adapter(data_like, getContext(), this);
        mrecyclerview2.setAdapter(adapter);
        header = view.findViewById(R.id.like_fragment_header_txt);
        header.setText("Likes");
        Log.d(TAG, "onCreateView: " + post_name);

        if (Request_Code == COMMENT_FRAGMENT) Comment_fragment_likes();
        else if (Request_Code == MAINUI) post_like_fragment();
        else if (Request_Code == PROFILE_PAGE_FOLLOWERS) profile_page_follower_fragment();
        else if (Request_Code == PROFILE_PAGE_FOLLOWING) setProfilePage_following();
        else if(Request_Code == NEW_USER) new_user();


        //close btn working
        close_btn = (ImageButton) view.findViewById(R.id.like_fragment_close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().onBackPressed();
            }
        });
        return view;
    }


    void Comment_fragment_likes() {
        data_like.clear();
        DocumentReference mDocref = db
                .collection("post")
                .document(post_name);
        if (mDocref != null) {
            mDocref = mDocref
                    .collection("comments")
                    .document(comment_name);
            if (mDocref != null) {
                mDocref
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> comment_like_data = new HashMap<>();
                                comment_like_data = (Map<String, Object>) documentSnapshot.get("comment_likes");
                                for (Map.Entry<String, Object> entry : comment_like_data.entrySet()) {
                                    likes_fragment_data obj = new likes_fragment_data();
                                    obj.setUid(entry.getKey());
                                    data_like.add(obj);

                                    int position = data_like.size() - 1;
                                    data_like.get(position).user_pic_setter(adapter, position);
                                    data_like.get(position).followers_list_update();
                                    data_like.get(position).follower_state(adapter, position);
                                }

                            }
                        });
            }
        }


    }

    void post_like_fragment() {
        data_like.clear();
        CollectionReference mColRef = db.collection("post")
                .document(post_name).collection("likes");
        mColRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: =>" + document.getData());
                                data_like.add(document.toObject(likes_fragment_data.class));

                                int position = data_like.size() - 1;
                                data_like.get(position).setUid(document.getId());
                                data_like.get(position).user_pic_setter(adapter, position);
                                data_like.get(position).followers_list_update();
                                data_like.get(position).follower_state(adapter, position);
                            }

                        } else {
                            Log.d(TAG, "Unsuccesul fetching likes data");
                            Toast.makeText(getContext(), "No Likes On this post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void profile_page_follower_fragment() {
        data_like.clear();
        header.setText("Your Followers");
        Log.d(TAG, "profile_page_follower_fragment: ");
        boolean TRUE = true;
        db
                .collection("AfollowsB")
                .whereEqualTo(welcome.user1.user_uid, TRUE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //data_like.clear();
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                if (doc.exists()) {
                                    Log.d(TAG, "onComplete: follower frag " + doc.getData());
                                    likes_fragment_data obj = new likes_fragment_data();
                                    obj.setUid(doc.getId());
                                    data_like.add(obj);
                                    int position = data_like.size() - 1;
                                    data_like.get(position).user_pic_setter(adapter, position);
                                    data_like.get(position).followers_list_update();
                                    data_like.get(position).follower_state(adapter, position);
                                }
                                else
                                {
                                    Log.d(TAG, "onComplete: No Followers");
                                    Toast.makeText(getActivity(),"No followers",Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.d(TAG, "onComplete: followers .task unsuccessful");
                        }
                    }
                });
        Log.d(TAG, "profile_page_follower_fragment: ending");

    }

    void setProfilePage_following() {
        data_like.clear();
        header.setText("You Following");

        db
                .collection("AfollowsB")
                .document(welcome.user1.user_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "onComplete: following" + doc.getData());
                            Map<String, Object> following_data = doc.getData();
                            for (Map.Entry<String, Object> entry : following_data.entrySet())
                            {
                                Log.d(TAG, "following list"+following_data.size());
                                likes_fragment_data obj = new likes_fragment_data();
                                obj.setUid(entry.getKey());
                                data_like.add(obj);

                                int position = data_like.size() - 1;
                                adapter.notifyItemInserted(position);
                                data_like.get(position).setFollow(true);
                                data_like.get(position).user_pic_setter(adapter, position);
                                //data_like.get(position).followers_list_update();
                                //data_like.get(position).follower_state(adapter, position);
                            }
                        } else {
                            Log.d(TAG, "onComplete: Problem in fetchiong user_following data");
                        }
                    }
                });
    }




    @Override
    public void OnNoteClick_like(final int position, int id) {
        if (id == R.id.user_name_like_fragment)
        {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("uid", data_like.get(position).getUid());
            startActivity(intent);
        } else
            if (id == R.id.follow_btn_like_fragment)
            {
            if (data_like.get(position).follow != null)
            {

                if (data_like.get(position).getFollow())
                {

                    db
                            .collection("AfollowsB")
                            .document(welcome.user1.user_uid)
                            .update(data_like.get(position).uid, FieldValue.delete())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "OnNoteClick_like: unfollowed Succesfully");
                                        data_like.get(position).setFollow(false);

                                        //followers decrement
                                        db.collection("users")
                                                .document(welcome.user1.user_uid)
                                                .update("following",FieldValue.increment(-1));

                                        //Removing unfollowed user post from timeline

                                        final CollectionReference timeline_ref=
                                                db
                                                .collection("users")
                                                .document(welcome.user1.user_uid)
                                                .collection("timeline");

                                        Log.d(TAG, "onComplete: timeline ref"+data_like.get(position).getUid());
                                                timeline_ref
                                                        .whereEqualTo("uid",data_like.get(position).getUid())
                                                    .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        ArrayList<String> del_data = new ArrayList<>();
                                                        for(QueryDocumentSnapshot doc :querySnapshot)
                                                        {
                                                            Log.d(TAG, "saving this post to delete from timeline"+doc.getId());
                                                            del_data.add(doc.getId());
                                                        }
                                                        remove_post_from_timeline(del_data);
                                                    }
                                                });

                                        adapter.notifyItemChanged(position);
                                    } else {
                                        Log.d(TAG, "onComplete: cnat unfollow");
                                    }
                                }
                            });


                } else {

                    final Map<String,Object> data = new HashMap<>();
                    data.put(data_like.get(position).uid, true);
                    db
                            .collection("AfollowsB")
                            .document(welcome.user1.user_uid)
                            .set(data, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "OnNoteClick_like: followed Succesfully");
                                        data_like.get(position).setFollow(true);

                                        //follwers increment
                                        db.collection("users")
                                                .document(welcome.user1.user_uid)
                                                .update("following",FieldValue.increment(1));

                                        final DocumentReference timeline_ref =db
                                                .collection("users")
                                                .document(welcome.user1.user_uid);




                                        Log.d(TAG, "Adding post to timeline: "+data_like.get(position).getUid());

                                        db
                                                .collection("post")
                                                .whereEqualTo("uid",data_like.get(position).getUid())
                                                .limit(30)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "onComplete: follow data success");
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            for (QueryDocumentSnapshot doc : querySnapshot)
                                                            {
                                                                Log.d(TAG, "onCompletefollow: " + doc.getData());
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("post_id", doc.getId());
                                                                data.put("time_stamp", doc.get("time_stamp"));
                                                                data.put("uid", doc.get("uid"));

                                                                timeline_ref
                                                                        .collection("timeline")
                                                                        .document(doc.getId())
                                                                        .set(data)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "onSuccess: timeline data set");
                                                                    }
                                                                });


                                                            }
                                                        } else {
                                                            Log.d(TAG, "instance initializer: unsuccesful fetch follow");
                                                        }
                                                    }
                                                });


                                        adapter.notifyItemChanged(position);
                                    } else {
                                        Log.d(TAG, "onComplete: cant follow");
                                    }

                                }
                            });


                }


            }
            else
                {
                Log.d(TAG, "OnNoteClick_like: Uninitialised follow state");
            }
        }


    }

    void new_user()
    {
        header.setText("Suggestions For You");
        db
                .collection("users")
                .limit(30)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: new user follower");
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Log.d(TAG, "onComplete: "+doc.getData());
                                data_like.add(doc.toObject(likes_fragment_data.class));
                                int position = data_like.size()-1;
                                Log.d(TAG, "onComplete: "+data_like.get(position).getUser_pic_url());
                                data_like.get(position).setUid(doc.getId());
                                data_like.get(position).followers_list_update();
                                data_like.get(position).follower_state(adapter, position);
                                adapter.notifyItemInserted(position);
                            }

                        }
                        else
                        {
                            Log.d(TAG, "onComplete:Error finding users list");
                        }
                    }
                });
                adapter.notifyDataSetChanged();

    }


    //removing given posts from user timeline
    void remove_post_from_timeline(ArrayList<String> data)
    {
        CollectionReference timeline_ref
                =db
                .collection("users")
                .document(welcome.user1.user_uid)
                .collection("timeline");
        for(int i=0;i<data.size();++i) {
            timeline_ref
                    .document(data.get(i))
                    .delete();
        }
    }
}