package com.example.mymedia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

public class mrecylceviewAdapter
        extends RecyclerView.Adapter<mrecylceviewAdapter.mRecycleViewHolder>
{

    public  int datanumber=0;
    OnNoteListner monNoteListner;
    static   ArrayList<post_data> data;
    Context mContext;
    DatabaseFun databaseFun = new DatabaseFun();
    private static final String TAG = "mrecylceviewAdapter";
    public static Boolean like_btn_state;


    public mrecylceviewAdapter(){}
    public mrecylceviewAdapter(ArrayList<post_data> data, Context context,@Nullable OnNoteListner monNoteListner)
    {
       this.data=data;
       mContext=context;
       this.monNoteListner=monNoteListner;
        initImageLoader();
        for(int i=0;i<data.size();++i)
        {
           Log.d(TAG, "mrecylceviewAdapter: "+data.get(i).user_name);
        }
    }

    public static void newsfeed_data_setter(ArrayList<post_data> newsfeed_data) {
        data=newsfeed_data;
    }

    @Override
    public mRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: done");
        LayoutInflater inflater = LayoutInflater .from (parent.getContext());
        View view = inflater.inflate(R.layout.newsfeed_post,parent,false);

        return new mRecycleViewHolder(view,monNoteListner);
    }


    @Override
    public void onBindViewHolder(final mRecycleViewHolder holder, final int position)
    {

        Log.d(TAG, "onBindViewHolder: "+Integer.toString(position));
        holder.user_name.setText(data.get(position).getUser_name());
        holder.description.setText(data.get(position).getDescription());
        holder.like.setText(Integer.toString(data.get(position).getLikes_cnt())+" Likes");
        holder.comment.setText("View all "+Integer.toString(data.get(position).getComment_cnt())+" Comments");
        //Converting time stamp to time

        final Date date=data.get(position).getTime_stamp().toDate();
        holder.time_stamp.setText(Time_stamp_converter.time_Converter(data.get(position).getTime_stamp()));
        holder.user_pic.setImageDrawable(null);

        UniversalImageLoader
                .setImage(data.get(position).post_image_url,holder.post_pic,null,"");
        UniversalImageLoader
                .setImage(data.get(position).user_pic_url,holder.user_pic,null,"");



        //Getting state of like btn
       /*if(data.get(position).post_liked==null)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("post")
                    .document(data.get(position).post_id)
                    .collection("likes")
                    .document(MainActivity.user1.authuser)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "Document exists!");
                                    holder.whitelike_btn.setVisibility(View.INVISIBLE);
                                    holder.redlike_btn.setVisibility(View.VISIBLE);
                                    data.get(position).setPost_liked(true);
                                } else {
                                    Log.d(TAG, "Document does not exist!");
                                    holder.whitelike_btn.setVisibility(View.VISIBLE);
                                    holder.redlike_btn.setVisibility(View.INVISIBLE);
                                    data.get(position).setPost_liked(false);
                                }

                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });
        }
        else {
            Log.d(TAG, "onBindViewHolder: intialised");
            if (data.get(position).post_liked == true) {
                holder.whitelike_btn.setVisibility(View.INVISIBLE);
                holder.redlike_btn.setVisibility(View.VISIBLE);
            } else {
                holder.whitelike_btn.setVisibility(View.VISIBLE);
                holder.redlike_btn.setVisibility(View.INVISIBLE);
            }
        }
        */

       //post_like_btn_refresher
       if(data.get(position).post_liked!=null)
        if(data.get(position).post_liked)
        {
            holder.whitelike_btn.setVisibility(View.INVISIBLE);
            holder.redlike_btn.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.redlike_btn.setVisibility(View.INVISIBLE);
            holder.whitelike_btn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ Integer.toString(data.size()));
        return data.size();
    }


    public class mRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView description,time_stamp,like,comment;
        Button user_name;
        ImageButton redlike_btn,whitelike_btn,comment_btn,share_btn,bookmark_btn;
        ImageView user_pic,post_pic;
        OnNoteListner onNoteListner;
        public mRecycleViewHolder(View itemView, final OnNoteListner onNoteListner)  {
            super(itemView);
            this.onNoteListner=onNoteListner;
            user_name=(Button) itemView.findViewById(R.id.username_btn);
            description= (TextView) itemView.findViewById(R.id.caption);
            time_stamp= (TextView) itemView.findViewById(R.id.time_stamp);
            like= (TextView) itemView.findViewById(R.id.likes_count);
            comment=(TextView) itemView.findViewById(R.id.comment_fragment_btn);
            user_pic=(ImageView)itemView.findViewById(R.id.userpic);
            post_pic=(ImageView) itemView.findViewById(R.id.post_image);
            redlike_btn=(ImageButton) itemView.findViewById(R.id.redlike_btn);
            whitelike_btn=itemView.findViewById(R.id.whitelike_btn);
            comment_btn=(ImageButton) itemView.findViewById(R.id.comment_btn);
            share_btn=(ImageButton) itemView.findViewById(R.id.share_btn);
            bookmark_btn=(ImageButton)itemView.findViewById(R.id.bookmark_btn);

        /*    like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: like button clicked");
                    onNoteListner.OnNoteClick(getAdapterPosition(),0);
                }
            });
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNoteListner.OnNoteClick(getAdapterPosition(),1);
                }
            });*/
            comment.setOnClickListener(this);
            like.setOnClickListener(this);
            user_name.setOnClickListener(this);
            redlike_btn.setOnClickListener(this);
            whitelike_btn.setOnClickListener(this);
            share_btn.setOnClickListener(this);
            comment_btn.setOnClickListener(this);
            bookmark_btn.setOnClickListener(this);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: "+Integer.toString(view.getId()));
         onNoteListner.OnNoteClick(getAdapterPosition(),view.getId());
        }
    }

    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public interface OnNoteListner{
        void OnNoteClick(int position, int ID);
    }

}
