package com.example.mymedia;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikeFragment extends DialogFragment implements like_fragment_recycle_adapter.OnNoteListner_like {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton close_btn;
     static Map<String, Object> follow_data =new HashMap<>();
     like_fragment_recycle_adapter adapter;
    static int cnt=0;


    private static Context mContext;
    private static final String TAG = "LikeFragment";
    RecyclerView mrecyclerview2;
    private  String post_name,comment_name;
    ArrayList<likes_fragment_data>  data_like = new ArrayList<>();
    int Request_Code=-1;
    public static int COMMENT_FRAGMENT=2,MAINUI=1,PROFILE_PAGE_FOLLOWERS=4,PROFILE_PAGE_FOLLOWING=3;

    // No argument constructor
    public LikeFragment(){}

    //Post_name argumented constuctor
    public LikeFragment(ArrayList<likes_fragment_data> data) {
        this.post_name=post_name;
        data_like=data;
    }

    public LikeFragment(int request_code, @Nullable String post_id,@Nullable String comment_id)
    {
        Request_Code = request_code;
        if(post_id!=null)post_name=post_id;
        if(comment_id!=null)comment_name=comment_id;
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

        //Custom height and width


        // Inflate the layout for this fragment

        View  view = inflater.inflate(R.layout.fragment_like, container, false);
        mrecyclerview2=(RecyclerView) view.findViewById(R.id.like_fragment_recycler);
        mrecyclerview2.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new like_fragment_recycle_adapter(data_like,getContext(),this);
        mrecyclerview2.setAdapter(adapter);
        Log.d(TAG, "onCreateView: "+post_name);
        //data_intialisation_like(post_name);
        if(Request_Code==COMMENT_FRAGMENT) Comment_fragment_likes();
        else if(Request_Code==MAINUI)post_like_fragment();
            else if(Request_Code == PROFILE_PAGE_FOLLOWERS)profile_page_follower_fragment();
            else if (Request_Code == PROFILE_PAGE_FOLLOWING)setProfilePage_following();


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



    void Comment_fragment_likes()
    {
        DocumentReference mDocref = db
                .collection("post")
                .document(post_name);
        if(mDocref!=null)
        {
            mDocref = mDocref
                    .collection("comments")
                    .document(comment_name);
            if(mDocref!=null)
            {
                mDocref
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String,Object> comment_like_data = new HashMap<>();
                                comment_like_data = (Map<String, Object>) documentSnapshot.get("comment_likes");
                                for(Map.Entry<String,Object> entry : comment_like_data.entrySet())
                                {
                                    likes_fragment_data obj = new likes_fragment_data();
                                    obj.setUser_name(entry.getKey());
                                    data_like.add(obj);

                                    int position = data_like.size()-1;
                                    data_like.get(position).user_pic_setter(adapter,position);
                                    data_like.get(position).followers_list_update();
                                    data_like.get(position).follower_state(adapter,position);
                                }

                            }
                        });
            }
        }


    }

    void post_like_fragment() {
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
                                data_like.get(position).user_pic_setter(adapter, position);
                                data_like.get(position).followers_list_update();
                                data_like.get(position).follower_state(adapter, position);
                            }

                        }
                        else
                            {
                            Log.d(TAG, "Unsuccesul fetching likes data");
                            Toast.makeText(getContext(), "No Likes On this post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void profile_page_follower_fragment()
    {
        Log.d(TAG, "profile_page_follower_fragment: ");
        db
                .collection("AfollowsB")
                .whereEqualTo("developer_nobita","true")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                if(doc.exists()) Log.d(TAG, "onComplete: exist data");
                                Log.d(TAG, "onComplete: follower frag "+doc.getData());
                                likes_fragment_data obj = new likes_fragment_data();
                                obj.setUser_name(doc.getId());
                                data_like.add(obj);
                                int position = data_like.size()-1;
                                data_like.get(position).user_pic_setter(adapter,position);
                                data_like.get(position).followers_list_update();
                                data_like.get(position).follower_state(adapter,position);
                            }
                        }
                        else 
                        {
                            Log.d(TAG, "onComplete: followers .task unsuccessful");
                        }
                    }
                });
        Log.d(TAG, "profile_page_follower_fragment: ending");

    }
    void setProfilePage_following()
    {

        db
                .collection("AfollowsB")
                .document(MainActivity.user1.authuser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "onComplete: following"+doc.getData());
                            Map<String,Object> following_data = doc.getData();
                            for(Map.Entry<String ,Object> entry : following_data.entrySet())
                            {
                                likes_fragment_data obj =new likes_fragment_data();
                                obj.setUser_name(entry.getKey());
                                data_like.add(obj);
                                int position = data_like.size()-1;
                                adapter.notifyItemInserted(position);
                                data_like.get(position).user_pic_setter(adapter,position);
                                data_like.get(position).followers_list_update();
                                data_like.get(position).follower_state(adapter,position);
                            }
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: Problem in fetchiong user_following data");
                        }
                    }
                });
    }
    /*
    Intailsed in mainui
    void data_intialisation_like(String post_name)
    {
        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        CollectionReference mColRef = db.collection("post")
                .document(post_name).collection("likes");
        mColRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d(TAG, "onComplete: =>" + document.getData());
                                data_like.add(document.toObject(likes_fragment_data.class));
                            }

                        }
                        else
                        {
                            Log.d(TAG, "Unsuccesul fetching likes data");
                            Toast.makeText(getContext(), "No Likes On this post", Toast.LENGTH_SHORT).show();
                        }


                        final String TAG1 ="follow_state_fun";

                        db
                                .collection("AfollowsB")
                                .document(MainActivity.user1.authuser)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document =task.getResult();
                                            Log.d(TAG1, document.getId() + " => " + document.getData());
                                            follow_data =document.getData();
                                        }
                                        else {
                                            Log.d(TAG1, "Error getting documents: ", task.getException());
                                        }
                                    }


                                });
                        Log.d(TAG, "onComplete: "+follow_data.size());
                        for(int i=0;i<data_like.size();++i)
                        {
                            if(follow_data.containsKey(data_like.get(i).getUser_name()))
                            {
                                Log.d(TAG, "onComplete: following");
                                data_like.get(i).setFollow(true);
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: Not following");
                                data_like.get(i).setFollow(false);
                            }
                        }

                        like_recycler_setter(data_like,getActivity());
                    }
                });
        Log.d(TAG, "data_intialisation: calling adapter");

    }*/

    void like_recycler_setter(ArrayList<likes_fragment_data> data1,Context context)
    {
        adapter = new like_fragment_recycle_adapter(data1,context,this);
        mrecyclerview2.setAdapter(adapter);
    }

    @Override
    public void OnNoteClick_like(final int position, int id) {
        if(id==R.id.user_name_like_fragment)
        {
            Intent intent = new Intent(getActivity(),ProfileActivity.class);
            intent.putExtra("user_name",data_like.get(position).getUser_name());
            startActivity(intent);
        }
        else
        if(id==R.id.follow_btn_like_fragment)
        {
            if(data_like.get(position).follow!=null)
            {
                FirebaseFirestore db =FirebaseFirestore.getInstance();
                if(data_like.get(position).getFollow())
                {

                    db
                            .collection("AfollowsB")
                            .document(MainActivity.user1.authuser)
                            .update(data_like.get(position).user_name, FieldValue.delete())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "OnNoteClick_like: unfollowed Succesfully");
                                        data_like.get(position).setFollow(false);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "onComplete: cnat unfollow");
                                    }
                                }
                            });


                }
                else
                {

                    db
                            .collection("AfollowsB")
                            .document(MainActivity.user1.authuser)
                            .update(data_like.get(position).user_name, true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Log.d(TAG, "OnNoteClick_like: followed Succesfully");
                                        data_like.get(position).setFollow(true);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
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
        else
        {
                adapter.notifyDataSetChanged();
                ++cnt;
        }

    }
}