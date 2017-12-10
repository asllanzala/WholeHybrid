package com.honeywell.wholesale.ui.selectpic.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.selectpic.Adapter.FolderRecyclerViewAdapter;
import com.honeywell.wholesale.ui.selectpic.Listener.OnFolderRecyclerViewInteractionListener;
import com.honeywell.wholesale.ui.selectpic.model.FolderListContent;


/**
 * Created by shushunsakai on 16/6/16.
 */
public class FolderPopWindow extends PopupWindow {


    private static final String TAG = "FolderPopupWindow";
    private Context mContext;
    private View conentView;
    private RecyclerView recyclerView;
    private OnFolderRecyclerViewInteractionListener mListener = null;


    public void initPopupWindow(final Activity context) {
        mContext = context;
        if (context instanceof OnFolderRecyclerViewInteractionListener) {
            mListener = (OnFolderRecyclerViewInteractionListener) context;
        } else {
            Log.d(TAG, "initPopupWindow: " + "context does not implement OnFolderRecyclerViewInteractionListener");
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = layoutInflater.inflate(R.layout.popup_folder_recyclerview, null, false);

        recyclerView = (RecyclerView) conentView.findViewById(R.id.folder_recyclerview);


        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(new FolderRecyclerViewAdapter(FolderListContent.FOLDERS, mListener));

        Display display = context.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        this.setContentView(conentView);
        this.setWidth(size.x);
        this.setHeight((int) (size.y * 0.618));
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_background));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
    }
}
