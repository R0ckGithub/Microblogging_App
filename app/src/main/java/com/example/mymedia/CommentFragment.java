package com.example.mymedia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CommentFragment extends DialogFragment implements comment_fragment_recycler_adapter.OnNoteListner_Comment {

    //Button functions

    private ImageButton close_btn;
    private EditText comment_area;
    private TextView post_btn;
    ArrayList<comment_fragment_data> data = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseFun databaseFun = new DatabaseFun();
    comment_fragment_recycler_adapter mcommentadapter;


    private static Context mContext;
    private static final String TAG = "CommentFragment";
    RecyclerView mrecyclerview2;
    private String post_id;
    static int refresh_cmnt_cnt = 0;

    // No argument constructor
    public CommentFragment() {
    }

    //Post_name argumented constuctor
    public CommentFragment(String post_id) {
        this.post_id = post_id;
    }


    public static CommentFragment newInstance(Context mContext) {
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fargment_custom_size);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.comment_fragment, container, false);
        mrecyclerview2 = (RecyclerView) view.findViewById(R.id.comment_fragment_recycler_view);
        mrecyclerview2.setLayoutManager(new LinearLayoutManager(mContext));
        mcommentadapter = new comment_fragment_recycler_adapter(data, getContext(), this);
        mrecyclerview2.setAdapter(mcommentadapter);

        data_intialisation_comment(post_id);

        //close btn
        close_btn = (ImageButton) view.findViewById(R.id.back_btn_comment_layout);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().onBackPressed();
            }
        });


        //user_pic
        ImageView authuser_pic = (ImageView) view
                .findViewById(R.id.user_pic_comment_layout);
        UniversalImageLoader
                .setImage(welcome.user1.authuser_url, authuser_pic,
                        null, "");

        //post btn(Comment posting)
        comment_area = (EditText) view.findViewById(R.id.comment_space);
        post_btn = (TextView) view.findViewById(R.id.comment_area_post_btn);


        //new comment adder
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comment_area.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(getActivity(), "Empty Comment", Toast.LENGTH_SHORT).show();
                } else {
                    data.add(0, databaseFun.add_comment(post_id, comment));
                    //Todo: Check boolean of above fun comment updated or not
                    comment_area.setText("");
                    mcommentadapter.notifyItemInserted(0);

                    //todo: increment of comment cnt in mainui

                    //    data_intialisation(post_id);
                }
            }
        });


        return view;

    }

    void data_intialisation_comment(String post_name) {

        CollectionReference mColRef = db
                .collection("post")
                .document(post_id)
                .collection("comments");

        mColRef
                .orderBy("time_stamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Todo: if i call data.clear() no image visible
                            //else static comments goes on increasing

                            //data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: =>" + document.getData());
                                data.add(document.toObject(comment_fragment_data.class));

                                int position = data.size() - 1;
                                mcommentadapter.notifyItemChanged(position);
                                data.get(position).setPost_id(post_id);
                                data.get(position).setComment_id(document.getId());
                                data.get(position).setUser_pic_url(mcommentadapter, position);
                                data.get(position).like_btn_state(mcommentadapter, position);
                            }
                        } else {
                            Log.d(TAG, "Unsuccesul fetching Comments data");
                            Toast.makeText(getContext(), "No Comments On this post", Toast.LENGTH_SHORT).show();
                        }

//                        Log.d(TAG, "onComplete: user_pic update"+data1.get(0).user_pic_url);
                    }
                });
        Log.d(TAG, "data_intialisation: calling adapter " + Integer.toString(data.size()));

    }


    @Override
    public void OnNoteClick_Comment(int position, int id) {

        Log.d(TAG, "OnNoteClick_Comment: " + Integer.toString(id));

        //like_state_changer
        if (id == R.id.whitelike_btn_comment_fragment || id == R.id.redlike_btn_comment_fragment) {
            String post_id = data.get(position).post_id;

            if (data.get(position).comment_liked != null) {
                if (data.get(position).comment_liked == false) {
                    Log.d(TAG, "OnNoteClick: comment liked by u");
                    data.get(position).setComment_liked(true);
                    data.get(position).likes_cnt++;
                    databaseFun.comment_liked(post_id, data.get(position).comment_id);
                    comment_fragment_recycler_adapter.comment_data_setter(data);
                    mcommentadapter.notifyItemChanged(position);
                } else {
                    Log.d(TAG, "OnNoteClick: comment disliked by u");
                    data.get(position).setComment_liked(false);
                    data.get(position).likes_cnt--;
                    databaseFun.comment_disliked(post_id, data.get(position).comment_id);
                    comment_fragment_recycler_adapter.comment_data_setter(data);
                    mcommentadapter.notifyItemChanged(position);

                }
            } else {
                Log.d(TAG, "OnNoteClick_Comment: unintialised comment error");
            }
        } else


            //profile_opening
            if (id == R.id.user_name_comment_fragment) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("uid", data.get(position).getUid());
                startActivity(intent);
            } else


                //comment_likes_fragment
                if (id == R.id.comment_fragment_likes_cnt) {

                    final LikeFragment likeFragment = new LikeFragment(LikeFragment.COMMENT_FRAGMENT, data.get(position).post_id
                            , data.get(position).comment_id);
                    likeFragment.show(getActivity().getSupportFragmentManager(), "Like_Fragment");

                }
    }

    void like_fragment_caller(ArrayList<likes_fragment_data> data) {
        Log.d(TAG, "like_fragment_caller: calling" + data.size());
        ArrayList<likes_fragment_data> data_like = data;
        final LikeFragment likeFragment = new LikeFragment(data_like);
        likeFragment.show(getActivity().getSupportFragmentManager(), "Comment_fragment");
    }

    void follow_setter(ArrayList<likes_fragment_data> mdata) {
        final String TAG1 = "follow_state_fun";
        final ArrayList<likes_fragment_data> data = mdata;

        db
                .collection("AfollowsB")
                .document(welcome.user1.authuser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> follow_data = new HashMap<>();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG1, document.getId() + " => " + document.getData());
                            follow_data = document.getData();
                        } else {
                            Log.d(TAG1, "Error getting documents: ", task.getException());
                        }
                        for (int i = 0; i < data.size(); ++i) {
                            if (follow_data.containsKey(data.get(i).getUser_name())) {
                                Log.d(TAG, "onComplete: following");
                                data.get(i).setFollow(true);
                            } else {
                                Log.d(TAG, "onComplete: Not following");
                                data.get(i).setFollow(false);

                            }
                        }
                        user_pic_setter(data);
                    }


                });

    }

    void user_pic_setter(ArrayList<likes_fragment_data> data1) {
        final ArrayList<likes_fragment_data> data = data1;
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
                            like_fragment_caller(data);
                        }

                    });
        }
    }
}




