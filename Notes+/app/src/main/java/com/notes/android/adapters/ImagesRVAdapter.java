package com.notes.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.notes.android.R;
import com.notes.android.models.NoteModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aniket on 28-03-2018.
 */

public class ImagesRVAdapter extends RecyclerView.Adapter<ImagesRVAdapter.ImagesViewHolder> {

    private Context mContext;
    private List<Uri> imagesList;
    private RVImageClickevents imageClickEvents;

    public void setImageClickEvents(RVImageClickevents imageClickEvents) {
        this.imageClickEvents = imageClickEvents;
    }

    public void setImagesList(List<Uri> imagesList) {
        this.imagesList = imagesList;
    }

    public ImagesRVAdapter(Context mContext) {
        this.mContext = mContext;
        imagesList = new ArrayList<Uri>();
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_image, null);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, final int position) {
        Glide.with(mContext).load(imagesList.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClickEvents.onClickImage(imagesList.get(position));
            }
        });
        holder.delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClickEvents.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,delete_image;

        public ImagesViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivAddedImage);
            delete_image= (ImageView) itemView.findViewById(R.id.delete_image);
        }
    }

    public interface RVImageClickevents {
        void onClickImage(Uri clickedImageUri);

        void onDeleteClick(int position);
    }
}
