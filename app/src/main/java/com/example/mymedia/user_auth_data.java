package com.example.mymedia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class user_auth_data {
    final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String authuser;

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    String real_name;
    String authuser_url;
    String bio;
    String user_uid;

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    int followers_cnt = 0;
    int following_cnt = 0;

    public user_auth_data(String authuser, String authuser_url, String bio, int followers_cnt, int following_cnt) {
        this.authuser = authuser;
        this.authuser_url = authuser_url;
        this.bio = bio;
        this.followers_cnt = followers_cnt;
        this.following_cnt = following_cnt;
    }

    public String getAuthuser() {
        return authuser;
    }

    public void setAuthuser(String authuser) {
        this.authuser = authuser;
    }

    public String getAuthuser_url() {
        return authuser_url;
    }

    public void setAuthuser_url(String authuser_url) {
        this.authuser_url = authuser_url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getFollowers_cnt() {
        return followers_cnt;
    }

    public void setFollowers_cnt(int followers_cnt) {
        this.followers_cnt = followers_cnt;
    }

    public int getFollowing_cnt() {
        return following_cnt;
    }

    public void setFollowing_cnt(int following_cnt) {
        this.following_cnt = following_cnt;
    }

    public user_auth_data() {
    }

    public user_auth_data(String authuser, String authuser_url) {
        this.authuser = authuser;
        this.authuser_url = authuser_url;
    }

    public void setAuthuser()
    {
        final String TAG = "Authuser setter";
        if(user_uid==null)
        {
            user_uid = FirebaseAuth.getInstance().getUid();
        }
       DocumentReference mDocRef = db.collection("users")
               .document(user_uid);
               if(mDocRef!=null)
                   mDocRef
                           .get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.isSuccessful())
                       {
                           DocumentSnapshot doc = task.getResult();
                           if(doc.exists())
                           {
                               Log.d(TAG, "onComplete: doc found setting authuser");
                               authuser = doc.get("user_name").toString();
                               authuser_url = doc.get("user_pic_url").toString();
                           }
                           else
                           {
                               Log.e(TAG, "onComplete: Doc not found in Authuser NULL uid ");
                           }

                       }
                   }
               });
    }
}
