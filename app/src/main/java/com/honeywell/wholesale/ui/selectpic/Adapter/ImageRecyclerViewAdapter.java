package com.honeywell.wholesale.ui.selectpic.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.selectpic.Listener.OnImageRecyclerViewInteractionListener;
import com.honeywell.wholesale.ui.selectpic.Utils.DraweeUtils;
import com.honeywell.wholesale.ui.selectpic.Utils.FileUtils;
import com.honeywell.wholesale.ui.selectpic.model.ImageItem;
import com.honeywell.wholesale.ui.selectpic.model.ImageListContent;

import java.io.File;
import java.util.List;


/**
 * Created by zhujunyu on 16/6/14.
 *
 */
public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {

    private List<ImageItem> items;
    private final OnImageRecyclerViewInteractionListener listener;

    public ImageRecyclerViewAdapter(List<ImageItem> items, OnImageRecyclerViewInteractionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_image_item, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageItem imageItem = items.get(position);
        holder.mItem = imageItem;

        Uri newUri;
        if (imageItem.isCamera()) {

            newUri = FileUtils.getUriByResId(R.mipmap.ic_photo_camera_white_48dp);
            DraweeUtils.showThunb(newUri, holder.mDrawee);
            holder.mImageName.setVisibility(View.VISIBLE);
            holder.mChecked.setVisibility(View.GONE);
            holder.mMask.setVisibility(View.GONE);

        } else {
            File imageFile = new File(imageItem.imagePath);

            if (imageFile.exists()) {
                newUri = Uri.fromFile(imageFile);
            } else {
                newUri = FileUtils.getUriByResId(R.mipmap.default_image);
            }
            DraweeUtils.showThunb(newUri, holder.mDrawee);

            holder.mImageName.setVisibility(View.GONE);
            holder.mChecked.setVisibility(View.GONE);
            if (ImageListContent.isImageSelected(imageItem.imagePath)) {
                holder.mMask.setVisibility(View.VISIBLE);
                holder.mChecked.setImageResource(R.mipmap.image_selected);
            } else {
                holder.mMask.setVisibility(View.GONE);
                holder.mChecked.setImageResource(R.mipmap.image_unselected);
            }
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mItem.isCamera()) {

                } else {
                    //这里实现单选
                    if (ImageListContent.isImageSelected(imageItem.imagePath)) {
                        //继续选中
                        ImageListContent.toggleImageSelected(imageItem.imagePath);
//                        notifyItemChanged(position);
                    } else {
//                        ImageListContent.clearSelectImages();
                        ImageListContent.toggleImageSelected(imageItem.imagePath);
//                        notifyItemChanged(position);
//                        notifyDataSetChanged();
                    }
                }

                if (listener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onImageItemInteraction(holder.mItem);
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        private SimpleDraweeView mDrawee;
        public ImageView mChecked;
        public View mMask;
        public ImageItem mItem;
        public TextView mImageName;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mDrawee = (SimpleDraweeView) mView.findViewById(R.id.image_drawee);
            mMask = mView.findViewById(R.id.image_mask);
            mChecked = (ImageView) mView.findViewById(R.id.image_checked);
            mImageName = (TextView) mView.findViewById(R.id.image_name);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
