package com.example.mymedia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseFun {

    FirebaseFirestore db;


    int post_state = -1;
    int temp;

    DatabaseFun() {
        db = FirebaseFirestore.getInstance();
        post_state = -1;

    }

    Boolean post_state(final String post_id, String user_name) {


        String post = "post", likes = "likes";
        final String TAG = "liked_post";
        final Boolean[] ans = {false};
        final CollectionReference mcolref = db.collection(post)
                .document(post_id)
                .collection(likes);


        mcolref
                .document(user_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document exists!");
                                post_state = 1;
                                ans[0] = true;
                            } else {
                                post_state = 0;
                                ans[0] = false;
                            }
                            Log.d(TAG, "Document does not exist!");
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
        Log.d(TAG, "post_state: " + ans[0].toString());
        return ans[0];
    }

    Boolean post_liked(String post_id,String UID) {
        temp = -1;
        final String TAG = "post_liked";
        String post = "post", likes = "likes";
        final DocumentReference mdocref = db.collection(post)
                .document(post_id);
        Map<String, Object> data = new HashMap<>();
        data.put("user_name", welcome.user1.authuser);

        Log.d(TAG, "post_liked: "+ UID);
        mdocref
                .collection("likes")
                .document(UID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {


                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Post liked by u");
                        temp = 1;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Post liking error", e);

                    }
                });

        mdocref
                .update("likes_cnt", FieldValue.increment(1));


        Log.d(TAG, "post_liked: " + temp);
        return temp == 1;
    }

    Boolean post_disliked(String post_id, String UID) {
        temp = -1;
        final String TAG = "post_disliked";
        String post = "post", likes = "likes";
        final DocumentReference mdocref = db.collection(post)
                .document(post_id);


        mdocref
                .collection(likes)
                .document(UID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        temp = 1;
                        Log.d(TAG, "Post Disliked Successfully");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error disliking post", e);
                    }
                });


        mdocref
                .update("likes_cnt", FieldValue.increment(-1));

        Log.d(TAG, "post_disliked: " + temp);
        return temp == 1;
    }


    comment_fragment_data add_comment(final String post_id, String comment) {
        final String TAG = "add_comment";
        int ans = 0;
        final comment_fragment_data new_cmnt = new comment_fragment_data();
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        new_cmnt.setComment(comment);
        data.put("likes_cnt", 0);
        new_cmnt.setLikes_cnt(0);
        data.put("user_name", welcome.user1.authuser);
        new_cmnt.setUser_name(welcome.user1.authuser);
        data.put("time_stamp", FieldValue.serverTimestamp());
        new_cmnt.setTime_stamp(Timestamp.now());
        Map<String, Object> comment_likes = new HashMap<>();
        new_cmnt.setPost_id(post_id);
        data.put("comment_likes", comment_likes);
        new_cmnt.setUid(welcome.user1.getUser_uid());
        data.put("uid",welcome.user1.getUser_uid());
        new_cmnt.setUser_pic_url(welcome.user1.authuser_url);


        DocumentReference mDocRef =
                db
                        .collection("post")
                        .document(post_id)
                        .collection("comments")
                        .document();
        new_cmnt.setComment_id(mDocRef.getId());
        mDocRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Comment successfully uploaded!");
                        db.collection("post")
                                .document(post_id)
                                .update("comment_cnt", FieldValue.increment(1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: incremnted likes cnt");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: not inremented", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating Comment", e);
                    }
                });
        return new_cmnt;
    }


    public void comment_liked(String post_id, String comment_id) {
        final String TAG = "comment_like";
        DocumentReference mDocref = db
                .collection("post")
                .document(post_id);
        if(mDocref!=null) {
            mDocref = mDocref
                    .collection("comments")
                    .document(comment_id);
        }
        if(mDocref!=null) {
            mDocref
                    .update(
                            "comment_likes." + welcome.user1.authuser, "true"
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: comment liked by u");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: comment liking error");
                        }
                    });
            mDocref
                    .update("likes_cnt",FieldValue.increment(1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Likes_cnt incremented");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: like_cnt_not_incremented",e );
                        }
                    });
        }

    }

    public void comment_disliked(String post_id, String comment_id) {
        final String TAG = "comment_dislike";
        DocumentReference mDocref = db
                .collection("post")
                .document(post_id);

        if(mDocref!=null) {
            mDocref = mDocref
                    .collection("comments")
                    .document(comment_id);
        }
        if(mDocref!=null) {
            mDocref
                    .update(
                            "comment_likes." + welcome.user1.authuser, FieldValue.delete()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: comment disliked by u");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: comment disliking error");
                        }
                    });
            mDocref
                    .update("likes_cnt", FieldValue.increment(1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Likes_cnt decremented");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: like_cnt_not_decremented", e);
                        }
                    });
        }


    }
}
