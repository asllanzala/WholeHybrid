package com.honeywell.wholesale.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.utils.FileManager;
import com.honeywell.wholesale.framework.utils.SquareImageView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by H155935 on 16/6/7.
 * Email: xiaofei.he@honeywell.com
 */
public class ImageUploadActivity extends Activity {
    private ArrayList<String> selectedPhotosPaths;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        ArrayList<String> paths = FileManager.getThumbnailsPath(this);

        selectedPhotosPaths = new ArrayList<>();

        GridAdapter gridAdapter = new GridAdapter(this, paths);

        gridView = (GridView)findViewById(R.id.image_select_grid_view);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // take photo
            if (position == 0){
                return;
            }
            // show photo detail

//            Intent intent = new Intent(ImageUploadActivity.this, GalleryActivity.class);
//            intent.putExtra("PATH", selectedPhotosPaths.get(position - 1));
//            startActivity(intent);
        }
    };

    private class GridAdapter extends BaseAdapter {
        private ArrayList <String> mPaths;
        private Context mContext;

        GridAdapter(Context context, ArrayList<String> paths) {
            mPaths = paths;
            mContext = context;
        }


        @Override
        public int getCount() {
            return mPaths.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return mPaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            }

            SquareImageView image = (SquareImageView) convertView.findViewById(R.id.grid_photo);

            if (position == 0){
                image.setImageResource(R.mipmap.icon_upload_pic);
                return convertView;
            }

            final String path = mPaths.get(position - 1);
            Bitmap bitmap = BitmapFactory.decodeFile(path, null);
            image.setImageBitmap(bitmap);

//            AppCompatCheckBox checkBox = (AppCompatCheckBox)convertView.findViewById(R.id.image_checkbox);
//            checkBox.setVisibility(View.VISIBLE);
//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked && !selectedPhotosPaths.contains(path)){
//                        selectedPhotosPaths.add(path);
//                    }else {
//                        selectedPhotosPaths.remove(path);
//                    }
//                }
//            });
            return convertView;
        }
    }

}
