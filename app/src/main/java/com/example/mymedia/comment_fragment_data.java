package com.example.mymedia;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class comment_fragment_data {
    String user_name="anonymous",comment="hidden";
    String user_pic_url="";
    Timestamp time_stamp;
    int likes_cnt=0;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    String post_id="";
    String comment_id="";


    public comment_fragment_data(String user_name, String comment, String user_pic_url, Timestamp time_stamp, String post_id, String comment_id, Boolean comment_liked) {
        this.user_name = user_name;
        this.comment = comment;
        this.user_pic_url = user_pic_url;
        this.time_stamp = time_stamp;
        this.post_id = post_id;
        this.comment_id = comment_id;
        this.comment_liked = comment_liked;
    }

    Boolean comment_liked;

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public void setUser_pic_url(String user_pic_url) {
        this.user_pic_url = user_pic_url;
    }

    public void setTime_stamp(Timestamp time_stamp) {
        this.time_stamp = time_stamp;
    }


    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Timestamp getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stampstamp(Timestamp time_stamp) {
        this.time_stamp = time_stamp;
    }

    public int getLikes_cnt() {
        return likes_cnt;
    }

    public void setLikes_cnt(int likes_cnt) {
        this.likes_cnt = likes_cnt;
    }

    public comment_fragment_data() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getComment_liked() {
        return comment_liked;
    }

    public void setComment_liked(Boolean comment_liked) {
        this.comment_liked = comment_liked;
    }

    public void setUser_pic_url(final comment_fragment_recycler_adapter Adapter, final int position)
    {
        final String TAG ="user_pic_url_comment";
         DocumentReference mDocref
                 =db
                .collection("users")
                .document(user_name);
        if(mDocref!=null) {
            mDocref
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String url = document.get("user_pic_url").toString();
                                    user_pic_url=url;
                                    Adapter.notifyItemChanged(position);
                                } else {
                                    Log.d(TAG, "No such URL document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }

                    });
        }
        else
        {
            Log.d(TAG, "user_pic_url_setter: No such comment exist error");
        }

    }

    public void like_btn_state(final comment_fragment_recycler_adapter Adapter, final int position)
    {
        DocumentReference mDocref = db
                .collection("post")
                .document(post_id);
        if (mDocref != null) {
            mDocref =
                    mDocref
                            .collection("comments")
                            .document(comment_id);
        }

        if (mDocref != null) {
            mDocref
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                Object object = documentSnapshot.get("comment_likes");
                                if (object != null) {
                                    HashMap<String, Object> h1 = (HashMap) object;
                                    if (h1.containsKey(MainActivity.user1.authuser)) {
                                        Log.d(TAG, "onComplete: true");
                                        comment_liked=true;
                                    } else {
                                        Log.d(TAG, "onComplete: false");
                                        comment_liked=false;
                                    }

                                    Adapter.notifyItemChanged(position);
                                } else Log.d(TAG, "onComplete: Comment_like not found");
                            } else {
                                Log.d(TAG, "Not complete");
                            }
                        }
                    });


        }
    }
}
