package com.example.mymedia;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class upload_post extends AppCompatActivity {

    ImageView upload_image;
    EditText description;
    Button submit_btn, cancel_btn;
    Uri downloadUrl;
    String description_txt;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "upload_post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        upload_image = findViewById(R.id.post_upload_image);
        description = findViewById(R.id.post_upload_description);
        submit_btn = findViewById(R.id.post_upload_submit_btn);
        cancel_btn = findViewById(R.id.post_upload_cancel_btn);

        initImageLoader();

        final StorageReference mstorage = FirebaseStorage.getInstance().getReference();

        if (getIntent().hasExtra("user_pic_url")) {
            String user_pic_url = getIntent().getParcelableExtra("user_pic_url").toString();
            UniversalImageLoader.setImage(user_pic_url, upload_image,
                    null, "");


        } else if (getIntent().hasExtra("user_pic_bitmap")) {
            upload_image.setImageBitmap((Bitmap) getIntent().getParcelableExtra("user_pic_bitmap"));
        }


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description_txt = description.getText().toString();
                int len = description_txt.length() > 30 ? 30 : description_txt.length();
                description_txt = description_txt.substring(0, len);
                Log.d(TAG, "onClick: desc" + ' ' + description_txt);
                if (getIntent().hasExtra("user_pic_url")) {
                    Log.d(TAG, "onClick: start");
                    Uri file = (Uri) getIntent().getParcelableExtra("user_pic_url");

                    UUID unique_id = UUID.randomUUID();
                    Log.d(TAG, "onClick: uNique id" + unique_id.toString());
                    StorageReference post_imageRef = mstorage.child("post_image/" + unique_id.toString() + ".png");

                    post_imageRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    downloadUrl = taskSnapshot.getUploadSessionUri();
                                    Log.d(TAG, "onSuccess: " + downloadUrl.toString());
                                    post_firestore_uploader(downloadUrl.toString(), description_txt);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            });
                }


            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getBaseContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    void post_firestore_uploader(String downloadUrl, String description_txt) {
        Map<String, Object> post_data = new HashMap<>();
        post_data.put("user_name", welcome.user1.authuser);
        post_data.put("likes_cnt", 0);
        post_data.put("comment_cnt", 0);
        post_data.put("description", description_txt);
        post_data.put("post_image_url", downloadUrl);
        post_data.put("time_stamp", FieldValue.serverTimestamp());
        post_data.put("uid",welcome.user1.user_uid);

        DocumentReference mDocref
                = db
                .collection("post")
                .document();
        mDocref
                .set(post_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: post_uploaded_succesfully");
                        Toast.makeText(getBaseContext(), "Post_Uploaded_Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: post_not_uploaded");
                        Toast.makeText(getBaseContext(), "Error:Post_Not_Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
        followers_fetch(mDocref.getId());

    }
    //uploading post_id to every follower timeline

    //fetching followers list
    void followers_fetch(String post_name) {
        final String post_id = post_name;
        DocumentReference mDocref
                = db
                .collection("AfollowsB")
                .document(welcome.user1.user_uid);
        try {


            mDocref
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null) {
                                Map<String, Object> followers_data = new HashMap<>();
                                Log.d(TAG, "onComplete: doc reterived");
                                followers_data = doc.getData();
                                timeline_upload(followers_data, post_id);

                            }
                        }
                    });

        } catch (Exception e) {
            Log.d(TAG, "timeline_setup: Null value" + e);
        }
    }

    //uploading post_id to followers list

    void timeline_upload(Map<String, Object> data, String post_id) {
        DocumentReference mDocref;
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            mDocref = db
                    .collection("users")
                    .document(entry.getKey());
            if (mDocref != null) {
                Map<String, Object> post_data = new HashMap<>();
                post_data.put("post_id", post_id);
                post_data.put("time_stamp", FieldValue.serverTimestamp());
                post_data.put("uid",welcome.user1.user_uid);
                mDocref
                        .collection("timeline")
                        .document(post_id)
                        .set(post_data)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e);
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: timeline_set" + entry.getKey());
                            }
                        });
            } else {
                Log.d(TAG, "timeline_upload: No such user ERRRROR");
            }

        }
    }

}