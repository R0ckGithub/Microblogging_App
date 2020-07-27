package com.example.mymedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraFragment extends DialogFragment
{

    private static final String TAG = "CameraFragment";
    private static final int REQUEST_IMAGE_CAPTURE =1 ,GALLERY=2;

    TextView camera,gallery,cancel;

    public static CommentFragment newInstance(Context mContext) {
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //style to get min width of fragment
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fargment_custom_size);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(getActivity(), upload_post.class);
                intent.putExtra("user_pic_url", selectedImage);
                Log.d(TAG, "onActivityResult: " + selectedImage.toString());
                startActivity(intent);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && requestCode == Activity.RESULT_OK) {
                Bitmap img_bm;
                img_bm = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(getActivity(), upload_post.class);
                intent.putExtra("user_pic_bitmap", img_bm);
                Log.d(TAG, "onActivityResult: bitmap");
                startActivity(intent);
            }
        }
        else Log.d(TAG, "onActivityResult: not reachable");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View  view = inflater.inflate(R.layout.camera_fragment, container, false);
        camera = (TextView) view.findViewById(R.id.camera_frag_cam);
        gallery =  view.findViewById(R.id.camera_frag_gallery);
        cancel = view.findViewById(R.id.camera_frag_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().onBackPressed();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });


        return view;
    }

   /* private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                /*    ".jpg",         /* suffix */
                 /*   storageDir      /* directory */
          /*  );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }*/

}
