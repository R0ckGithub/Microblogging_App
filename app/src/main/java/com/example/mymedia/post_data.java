package com.example.mymedia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class  post_data {
    static FirebaseFirestore db=FirebaseFirestore.getInstance();
    String description;
    String user_name;
    Timestamp time_stamp;
    String post_image_url;
    String user_pic_url;
    int likes_cnt;
    int comment_cnt;
    String post_id;
    Boolean post_liked;



    //utility functions

    public void set_User_pic_url(final mrecylceviewAdapter Adapter, final int position)
    {
        final String TAG = "User_pic_setter";

        db
                .collection("users")
                .document(user_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user_pic_url = document.get("user_pic_url").toString();

                                Log.d(TAG, "URLSnapshot data: " + user_pic_url);
                            } else {
                                Log.d(TAG, "No such URL document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        Adapter.notifyItemChanged(position);
                    }
                });


    }


    public void getPost_like_state(final mrecylceviewAdapter Adapter, final int position)
    {
        final String TAG="POST_like_state_fun";
        db
                .collection("post")
                .document(post_id)
                .collection("likes")
                .whereEqualTo("user_name",MainActivity.user1.authuser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            QuerySnapshot doc = task.getResult();
                            if(doc.isEmpty())
                            {
                                Log.d(TAG, "onComplete: doc empty");
                                post_liked=false;
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: doc not empty"+doc.getDocuments());
                                post_liked=true;
                            }
                            Adapter.notifyItemChanged(position);
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: Unsuccesful task");
                        }

                    }
                });




    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Timestamp getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Timestamp time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getPost_image_url() {
        return post_image_url;
    }

    public void setPost_image_url(String post_image_url) {
        this.post_image_url = post_image_url;
    }

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public void setUser_pic_url(String user_pic_url) {
        this.user_pic_url = user_pic_url;
    }

    public int getLikes_cnt() {
        return likes_cnt;
    }

    public void setLikes_cnt(int likes_cnt) {
        this.likes_cnt = likes_cnt;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Boolean getPost_liked() {
        return post_liked;
    }

    public void setPost_liked(Boolean post_liked) {
        this.post_liked = post_liked;
    }
}
