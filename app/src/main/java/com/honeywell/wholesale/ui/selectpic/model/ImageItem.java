package com.honeywell.wholesale.ui.selectpic.model;


import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;

/**
 * Created by zhujunyu on 16/6/14.
 *
 */
public class ImageItem {
    private static final String TAG = "imageItem";
    public static final String CAMERA_PATH = "camera";

    public String imagePath;
    private String imageName;
    private long time;

    public ImageItem(String name, String path, long time) {
        imageName = name;
        imagePath = path;
        this.time = time;
    }

    public boolean isCamera() {
        return this.imagePath.equals(SelectorSetting.CAMERA_ITEM_PATH);
    }

    @Override
    public boolean equals(Object o) {

        try {
            ImageItem other = (ImageItem) o;
            return imagePath.equalsIgnoreCase(other.imagePath);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        return super.equals(o);
    }


    @Override
    public String toString() {
        return "ImageItem{" +
                "name='" + imageName + '\'' +
                ", path='" + imagePath + '\'' +
                ", time=" + time +
                '}';
    }
}
