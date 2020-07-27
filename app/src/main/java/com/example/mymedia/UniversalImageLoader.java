package com.example.mymedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalImageLoader {
    private static final int defaultImage=R.drawable.blank_profile;
    private Context mcontext;

    public UniversalImageLoader(Context context)
    {
        mcontext=context;
    }
    public ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOptions = new   DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

                ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mcontext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100*1024*1024).build();
        return configuration;
    }

    public static void setImage(String imgURL , ImageView image , final ProgressBar mprogressBar , String append)
    {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mprogressBar != null)
                    mprogressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(mprogressBar != null)
                    mprogressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mprogressBar != null)
                    mprogressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(mprogressBar != null)
                    mprogressBar.setVisibility(View.VISIBLE);

            }
        });
    }


}

/**
 * Intialisig the universal image loader
 *
 * private void initImageLoader()
 *     {
 *         UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
 *         ImageLoader.getInstance().init(universalImageLoader.getConfig());
 *     }
 *
 * */