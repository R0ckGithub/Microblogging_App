package com.example.mymedia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class likes_fragment_data {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_name, real_name;
    String user_pic_url;
    Boolean follow;
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    static Map<String, Object> follower_list = new HashMap<>();

    public likes_fragment_data() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public void setUser_pic_url(String user_pic_url) {
        this.user_pic_url = user_pic_url;
    }


    void user_pic_setter(final like_fragment_recycle_adapter adapter, final int position) {
        final String TAG = "user_pic_setter_like";
        DocumentReference mDocref = db
                .collection("users")
                .document(uid);

        if (mDocref != null) {
            mDocref
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String url = document.get("user_pic_url").toString();
                                    user_pic_url = url;
                                    user_name = document.get("user_name").toString();
                                    Log.d(TAG, "URLSnapshot data: " + user_pic_url);
                                } else {
                                    Log.d(TAG, "No such URL document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                            adapter.notifyItemChanged(position);
                        }

                    });
        }
    }

    void followers_list_update() {
        final String TAG = "follow_list_like";

        db
                .collection("AfollowsB")
                .document(welcome.user1.user_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            follower_list = document.getData();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }


                });
    }

    void follower_state(final like_fragment_recycle_adapter adapter, int position) {
        final String TAG = "follower_state_like";

        if (follower_list.containsKey(uid)) {
            follow = true;
        } else follow = false;

        adapter.notifyItemChanged(position);
    }

}
