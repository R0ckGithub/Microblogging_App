package com.example.mymedia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class like_fragment_recycle_adapter extends RecyclerView.Adapter<like_fragment_recycle_adapter.fragment_recycle_viewholder> {

    private static final String TAG ="like_recycle_adapter" ;
    ArrayList<likes_fragment_data> data= new ArrayList<>();
    private Context mcontext;
    OnNoteListner_like monNoteListner_like;

    like_fragment_recycle_adapter(ArrayList<likes_fragment_data> data,Context context,OnNoteListner_like onNoteListner_like){
        this.data=data;
        mcontext=context;
        monNoteListner_like = onNoteListner_like;
        //this.layout=layout;
    }



    @Override
    public fragment_recycle_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater .from (parent.getContext());
        View view = inflater.inflate(R.layout.like_fragment_recyclerview_layout,parent,false);
        return new fragment_recycle_viewholder(view,monNoteListner_like);
    }

    @Override
    public void onBindViewHolder(@NonNull fragment_recycle_viewholder holder, int position) {
        Log.d(TAG, "onBindViewHolder: inside BindViewHolder");
        holder.user_name.setText(data.get(position).getUser_name());
        holder.name.setText(data.get(position).getReal_name());
        if(data.get(position).follow!=null)
        {
            Log.d(TAG, "onBind follow state: intialised laal dil");
            if (data.get(position).follow) holder.follow.setText("Unfollow");
            else holder.follow.setText("Follow");
        }
        else
        {
            Log.d(TAG, "onBindViewHolder follow stae: Not intialised yet kala dil");
        }

       // Not working
        //Todo: Disable follow button for urself
        if(data.get(position).user_name == MainActivity.user1.authuser)
        {
            Log.d(TAG, "onBindViewHolder: "+data.get(position).user_name);
            holder.follow.setVisibility(View.INVISIBLE);
        }

        initImageLoader();
        UniversalImageLoader.setImage(data.get(position).getUser_pic_url(),holder.user_pic,null,"");

        Log.d(TAG, "onBindViewHolder: "+data.get(position).user_pic_url);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class fragment_recycle_viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user_name;
        TextView name;
        ImageView user_pic;
        Button follow;
        OnNoteListner_like onNoteListner_like;
        fragment_recycle_viewholder(View itemView, OnNoteListner_like monNoteListner_like){
            super(itemView);
            user_name=(TextView) itemView.findViewById(R.id.user_name_like_fragment);
            name=(TextView)itemView.findViewById(R.id.name_like_fragment);
            user_pic=(ImageView)itemView.findViewById(R.id.userpic_like_fragment);
            follow=(Button)itemView.findViewById(R.id.follow_btn_like_fragment);
            onNoteListner_like = monNoteListner_like;

            user_name.setOnClickListener(this);
            follow.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onNoteListner_like.OnNoteClick_like(getAdapterPosition(),view.getId());
        }
    }

    private void initImageLoader()
      {
          UniversalImageLoader universalImageLoader = new UniversalImageLoader(mcontext);
          ImageLoader.getInstance().init(universalImageLoader.getConfig());
      }

      public  interface  OnNoteListner_like
    {
        void OnNoteClick_like(int position,int id);
    }

}
