package com.dgimatov.flickrsearchdemo.search.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dgimatov.flickrsearchdemo.R;
import com.dgimatov.flickrsearchdemo.search.model.ImageLoader;
import com.dgimatov.flickrsearchdemo.search.model.ImageUrl;
import com.dgimatov.flickrsearchdemo.search.model.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for images list
 */
public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ImageViewHolder> {
    private final ImageLoader imageLoader;

    ImagesListAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    List<ImageUrl> imageUrls = new ArrayList<>();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.iv.setImageResource(android.R.color.transparent);
        imageLoader.unsubscribe(holder.currentUrl);
        holder.currentUrl = imageUrls.get(position).getUrl();

        imageLoader.subscribe(holder.currentUrl, new Listener<Bitmap>() {
            @Override
            public void onNext(Bitmap bitmap) {
                holder.iv.setImageBitmap(bitmap);
            }

            @Override
            public void onError(Throwable e) {
                holder.iv.setImageDrawable(getErrorDrawable());
            }
        });
    }

    /**
     * Callback from a view that all running activities should be stopped
     */
    void onStop() {
        imageLoader.unsubscribeAll();
    }

    private Drawable getErrorDrawable() {
        Random rnd = new Random();
        return new ColorDrawable(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
    }


    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * View Holder for sigle image in the list
     */
    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv = itemView.findViewById(R.id.image);
        String currentUrl;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
