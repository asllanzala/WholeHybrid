package com.honeywell.wholesale.ui.selectpic.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.selectpic.Listener.OnFolderRecyclerViewInteractionListener;
import com.honeywell.wholesale.ui.selectpic.Utils.DraweeUtils;
import com.honeywell.wholesale.ui.selectpic.model.FolderItem;
import com.honeywell.wholesale.ui.selectpic.model.FolderListContent;

import java.io.File;
import java.util.List;


/**
 * Created by zhujunyu on 16/6/16.
 */
public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {

    private final List<FolderItem> mValues;
    private final OnFolderRecyclerViewInteractionListener mListener;

    public FolderRecyclerViewAdapter(List<FolderItem> items, OnFolderRecyclerViewInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        FolderItem folderItem = mValues.get(position);
        holder.mItem = folderItem;
        holder.folderName.setText(folderItem.name);
        holder.folderPath.setText(folderItem.path);
        holder.folderSize.setText(folderItem.getNumOfImages());

        if (position == FolderListContent.selectedFolderIndex) {
            holder.folderIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.folderIndicator.setVisibility(View.GONE);
        }

        DraweeUtils.showThunb(Uri.fromFile(new File(folderItem.coverImagePath)), holder.folderCover);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d(TAG, "onClick: " + holder.mItem.toString());
                // pass the selected result to FolderListContent
                int previousSelectedIndex = FolderListContent.selectedFolderIndex;
                FolderListContent.setSelectedFolder(holder.mItem, position);
                // we should notify previous item and current time to change
                notifyItemChanged(previousSelectedIndex);
                notifyItemChanged(position);
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFolderItemInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FolderItem mItem;
        View mView;
        SimpleDraweeView folderCover;
        TextView folderName;
        TextView folderPath;
        TextView folderSize;
        ImageView folderIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            folderCover = (SimpleDraweeView) itemView.findViewById(R.id.folder_cover_image);
            folderName = (TextView) itemView.findViewById(R.id.folder_name);
            folderPath = (TextView) itemView.findViewById(R.id.folder_path);
            folderSize = (TextView) itemView.findViewById(R.id.folder_size);
            folderIndicator = (ImageView) itemView.findViewById(R.id.folder_selected_indicator);
        }
    }


}
