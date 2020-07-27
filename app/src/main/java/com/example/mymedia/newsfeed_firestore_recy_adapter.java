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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;

public class newsfeed_firestore_recy_adapter
        extends FirestoreRecyclerAdapter<post_data,newsfeed_firestore_recy_adapter.viewholder> {

    OnNoteListner monNoteListner;
    final  String TAG = "firestore_recy_adapter";

    Context mContext;
    public newsfeed_firestore_recy_adapter(@NonNull FirestoreRecyclerOptions options, Context context,OnNoteListner onNoteListner) {
        super(options);
        mContext=context;
        monNoteListner=onNoteListner;
        initImageLoader();
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater .from (parent.getContext());
        View view = inflater.inflate(R.layout.newsfeed_post,parent,false);

        return new newsfeed_firestore_recy_adapter.viewholder(view,monNoteListner);

    }



    @Override
    protected void onBindViewHolder(@NonNull final viewholder holder, final int position, @NonNull final post_data model) {

        holder.user_name.setText(model.getUser_name());
        holder.description.setText(model.getDescription());
        holder.like.setText(Integer.toString(model.getLikes_cnt())+" Likes");
        holder.comment.setText("View all "+Integer.toString(model.getComment_cnt())+" Comments");
        final Date date=model.getTime_stamp().toDate();
        holder.time_stamp.setText(Time_stamp_converter.time_Converter(model.getTime_stamp()));

        //Image setting
        UniversalImageLoader
                .setImage(model.getPost_image_url(),holder.post_pic,null,"");
        UniversalImageLoader
                .setImage(model.user_pic_url,holder.user_pic,null,"");

        final String TAG = "Newsfeed_Bind";

        if(model.post_liked!=null)
        {
            if (model.post_liked) {
                holder.whitelike_btn.setVisibility(View.INVISIBLE);
                holder.redlike_btn.setVisibility(View.VISIBLE);
            } else {
                holder.redlike_btn.setVisibility(View.INVISIBLE);
                holder.whitelike_btn.setVisibility(View.VISIBLE);
            }
        }

    }

    public class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView description,time_stamp,like,comment;
        Button user_name;
        ImageButton redlike_btn,whitelike_btn,comment_btn,share_btn,bookmark_btn;
        ImageView user_pic,post_pic;
        OnNoteListner onNoteListner;

        public viewholder(@NonNull View itemView, OnNoteListner monNoteListner) {
            super(itemView);

            onNoteListner=monNoteListner;

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



            comment.setOnClickListener(this);
            like.setOnClickListener(this);
            user_name.setOnClickListener(this);
            redlike_btn.setOnClickListener(this);
            whitelike_btn.setOnClickListener(this);
            share_btn.setOnClickListener(this);
            comment_btn.setOnClickListener(this);
            bookmark_btn.setOnClickListener(this);
            itemView.setOnClickListener(this);


            //Todo: Apply gestures
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: "+Integer.toString(view.getId()));
            onNoteListner.OnNoteClick(getAdapterPosition(),view.getId());
        }
    }

    public interface OnNoteListner{
        void OnNoteClick(int position, int ID);
    }

    //Universal Image Loader Intialiser(I think)

    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


}
