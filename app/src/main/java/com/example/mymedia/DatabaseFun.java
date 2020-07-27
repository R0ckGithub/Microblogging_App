package com.example.mymedia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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


    int post_state=-1;
    int temp;
    DatabaseFun() {
        db = FirebaseFirestore.getInstance();
        post_state=-1;

    }
    Boolean post_state(final String post_id, String user_name) {


        String post = "post", likes = "likes";
        final String TAG="liked_post";
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
                                post_state=1;
                                ans[0] =true;
                            } else {
                                post_state=0;
                                ans[0]=false;
                            }
                            Log.d(TAG, "Document does not exist!");
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                    });
        Log.d(TAG, "post_state: "+ans[0].toString());
        return ans[0];
    }

    Boolean post_liked(String post_id,String user_name)
    {
        temp=-1;
        final String TAG = "post_liked";
        String post="post",likes="likes";
        final DocumentReference mdocref = db.collection(post)
                .document(post_id)
                ;
        Map<String,Object> data= new HashMap<>();
        data.put("user_name",user_name);


        mdocref
                .collection("likes")
                .document(MainActivity.user1.authuser)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {


                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Post liked by u");
                        temp=1;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Post liking error", e);

                    }
                });

        mdocref
                .update("likes_cnt",FieldValue.increment(1));


        Log.d(TAG, "post_disliked: "+temp);
        return temp==1;
    }

    Boolean post_disliked(String post_id,String user_name)
    {
        temp=-1;
        final String TAG = "post_disliked";
        String post="post",likes="likes";
        final DocumentReference mdocref = db.collection(post)
                .document(post_id);



        mdocref
                .collection(likes)
                .document(user_name)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                temp=1;
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

        Log.d(TAG, "post_disliked: "+temp);
        return temp==1;
    }


    comment_fragment_data add_comment(final String post_id, String comment )
    {
        final String TAG="add_comment";
        int ans=0;
        final comment_fragment_data new_cmnt = new comment_fragment_data();
        Map<String , Object> data = new HashMap<>();
        data.put("comment",comment); new_cmnt.setComment(comment);
        data.put("likes_cnt",0); new_cmnt.setLikes_cnt(0);
        data.put("user_name",MainActivity.user1.authuser); new_cmnt.setUser_name(MainActivity.user1.authuser);
        data.put("time_stamp",FieldValue.serverTimestamp()); new_cmnt.setTime_stamp(Timestamp.now());
        Map<String ,Object> comment_likes = new HashMap<>(); new_cmnt.setPost_id(post_id);
        data.put("comment_likes",comment_likes);

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
                                .update("comment_cnt",FieldValue.increment(1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: incremnted likes cnt");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: not inremented",e);
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


    ArrayList<comment_fragment_data> user_pic_comment_updater(ArrayList<comment_fragment_data> data)
    {
        final ArrayList<comment_fragment_data> data1=data;
        for(int i=0;i<data1.size();++i)
        {
            String user_name;
            final String TAG = "pic_comment_updater";
            user_name = data.get(i).user_name;
            final int finalI = i;
            db
                    .collection("users")
                    .document(user_name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                data1.get(finalI).setUser_pic_url(doc.get("user_pic_url").toString());
                                Log.d(TAG, "onComplete: " + data1.get(finalI).user_pic_url);
                            }
                        }
                    });
        }
            return data1;
    }



    ArrayList<likes_fragment_data> user_pic_likes_updater(ArrayList<likes_fragment_data> data)
    {

        final String user_pic_url="user_pic_url";
        final ArrayList<likes_fragment_data> data1=data;
        for(int i=0;i<data1.size();++i) {
            String user_name;

            final String TAG = "pic_like_updater";
            user_name = data.get(i).user_name;
            Log.d(TAG, "user_pic_likes_updater: "+user_name);
            final int finalI = i;
            db
                    .collection("users")
                    .document(user_name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                Log.d(TAG, "onComplete: "+doc.get(user_pic_url));
                                String tempurlholder
                                        =doc.get(user_pic_url)!=null?
                                        doc.get(user_pic_url).toString():"";
                               if(!tempurlholder.isEmpty())
                                   data1.get(finalI)
                                           .setUser_pic_url(tempurlholder);
                             //   Log.d(TAG, "onComplete: " + data1.get(finalI).user_pic_url);
                            }
                        }
                    });
        }
            return data1;

    }

    public void comment_liked(String post_id, String comment_id) {
        final String TAG = "comment_like";
        db
                .collection("post")
                .document(post_id)
                .collection("comments")
                .document(comment_id)
                .update(
                        "comment_likes."+MainActivity.user1.authuser,"true"
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

    }

    public void comment_disliked(String post_id, String comment_id) {
        final String TAG = "comment_dislike";
        db
                .collection("post")
                .document(post_id)
                .collection("comments")
                .document(comment_id)
                .update(
                        "comment_likes."+MainActivity.user1.authuser,FieldValue.delete()
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


    }
}
