package com.honeywell.wholesale.ui.selectpic.model;

import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;

import java.util.ArrayList;


/**
 * Created by zhujunyu on 16/6/14.
 */
public class ImageListContent {

    //ImageRecycleViewAdapter.onClick will set it to True  activity.onImageInteraction will show the alert.and set it to false;

    public static boolean bReachMaxNumber = false;

    public static final ArrayList<ImageItem> IMAGES = new ArrayList<ImageItem>();

    public static void clear() {
        IMAGES.clear();
    }

    public static void addItem(ImageItem item) {
        IMAGES.add(item);
    }

    public static final ArrayList<String> SELECTED_IMAGES = new ArrayList<>();

    public static void clearSelectImages(){
        SELECTED_IMAGES.clear();
    }


    public static boolean isImageSelected(String filename) {
        return SELECTED_IMAGES.contains(filename);
    }

    public static void toggleImageSelected(String filename) {
        if (SELECTED_IMAGES.contains(filename)) {
//            SELECTED_IMAGES.remove(filename);
        } else {
            SELECTED_IMAGES.add(filename);
        }
    }

    public static final ImageItem cameraItem = new ImageItem("", SelectorSetting.CAMERA_ITEM_PATH, 0);


}
