package com.example.mymedia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

//import static android.content.ContentValues.TAG;

public class comment_fragment_recycler_adapter extends RecyclerView.Adapter<comment_fragment_recycler_adapter.comment_fragment_recycler_adapter_viewholder> {

    private static final String TAG = "commentrecycler_adapter";
    private static ArrayList<comment_fragment_data> data;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context mcontext;
    OnNoteListner_Comment monNoteListner_comment;

    public comment_fragment_recycler_adapter(ArrayList<comment_fragment_data> data,Context context
            , OnNoteListner_Comment onNoteListner_comment)
    {
        monNoteListner_comment=onNoteListner_comment;
        mcontext=context;
        this.data=data;

        //this.layout=layout;
    }

    public static void comment_data_setter(ArrayList<comment_fragment_data> comment_data) {
        data=comment_data;
    }


    @Override
    public comment_fragment_recycler_adapter_viewholder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater .from (parent.getContext());
        View view = inflater.inflate(R.layout.comment_fragment_layout,parent,false);
        return new comment_fragment_recycler_adapter_viewholder(view,monNoteListner_comment);
    }

    @Override
    public void onBindViewHolder
            (@NonNull final comment_fragment_recycler_adapter_viewholder holder, final int position) {
        if (position < data.size()) {

            holder.user_name.setText(data.get(position).getUser_name());
            holder.comment.setText(data.get(position).getComment());
            holder.likes_txt.setText(Integer.toString(data.get(position).getLikes_cnt()) + " Likes");
            holder.time_stamp.setText
                    (Time_stamp_converter.time_Converter(data.get(position).getTime_stamp()));

            initImageLoader();
            UniversalImageLoader
                    .setImage(data.get(position).getUser_pic_url(), holder.user_pic,
                            null, "");
            Log.d(TAG, "Comment_pic " + data.get(position).user_pic_url);
        } else {
            Log.d(TAG, "onBindViewHolder: Comment data intialiseing error");
        }

        //like_btn state
        if (data.get(position).comment_liked == null) {

            Log.d(TAG, "onBindViewHolder: " + data.get(position).post_id);

            DocumentReference mDocref = db
                    .collection("post")
                    .document(data.get(position).post_id);
            if (mDocref != null) {
                mDocref =
                        mDocref
                                .collection("comments")
                                .document(data.get(position).getComment_id());
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
                                            holder.redlike_btn.setVisibility(View.VISIBLE);
                                            holder.whitelike_btn.setVisibility(View.INVISIBLE);
                                            data.get(position).setComment_liked(true);
                                        } else {
                                            Log.d(TAG, "onComplete: false");
                                            data.get(position).setComment_liked(false);
                                            holder.redlike_btn.setVisibility(View.INVISIBLE);
                                            holder.whitelike_btn.setVisibility(View.VISIBLE);
                                        }
                                    } else Log.d(TAG, "onComplete: Comment_like not found");
                                } else {
                                    Log.d(TAG, "Not complete");
                                }
                            }
                        });

                Log.d(TAG, "onBindViewHolder11: " + holder.comment.getText());
            }
        }
        else {
                Log.d(TAG, "onBindViewHolder: " + data.get(position).comment_liked.toString());
                if (data.get(position).comment_liked == true) {
                    holder.redlike_btn.setVisibility(View.VISIBLE);
                    holder.whitelike_btn.setVisibility(View.INVISIBLE);
                } else {
                    holder.redlike_btn.setVisibility(View.INVISIBLE);
                    holder.whitelike_btn.setVisibility(View.VISIBLE);
                }
            }


    }
        public int getItemCount () {

            Log.d(TAG, "getItemCount: " + Integer.toString(data.size()));
            return data.size();
        }




    public class comment_fragment_recycler_adapter_viewholder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        TextView user_name,comment,time_stamp,likes_txt;
        ImageButton redlike_btn,whitelike_btn;
        ImageView user_pic;
        OnNoteListner_Comment onNoteListner_comment;
        public comment_fragment_recycler_adapter_viewholder(@NonNull View itemView,
                                                            OnNoteListner_Comment onNoteListner_comment)
        {
            super(itemView);
            this.onNoteListner_comment = onNoteListner_comment;
            user_name= itemView.findViewById(R.id.user_name_comment_fragment);
            comment= itemView.findViewById(R.id.comment_txt);
            time_stamp= itemView.findViewById(R.id.time_stamp_comment_fragment);
            likes_txt= itemView.findViewById(R.id.comment_fragment_likes_cnt);
            redlike_btn= itemView.findViewById(R.id.redlike_btn_comment_fragment);
            whitelike_btn=itemView.findViewById(R.id.whitelike_btn_comment_fragment);
            user_pic= itemView.findViewById(R.id.user_pic_comment_fragment);

            user_name.setOnClickListener(this);
            redlike_btn.setOnClickListener(this);
            whitelike_btn.setOnClickListener(this);
            likes_txt.setOnClickListener(this);
            itemView.setOnClickListener(this);


        }



        @Override
        public void onClick(View view) {
            onNoteListner_comment.OnNoteClick_Comment(getAdapterPosition(),view.getId());
        }
    }



    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mcontext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public interface OnNoteListner_Comment
    {
        public void OnNoteClick_Comment(int position, int ID);
    }


}
