package com.honeywell.wholesale.ui.selectpic.model;

import java.util.ArrayList;

/**
 * Created by zhujunyu on 16/6/15.
 */
public class FolderItem {
    public String name;
    public String path;
    public String coverImagePath;
    public ArrayList<ImageItem> mImages = new ArrayList<ImageItem>();


    public FolderItem(String name, String path, String coverImagePath) {
        this.name = name;
        this.path = path;
        this.coverImagePath = coverImagePath;
    }

    public void addImageItem(ImageItem imageItem) {
        this.mImages.add(imageItem);
    }

    public String getNumOfImages() {
        return String.format("%d", mImages.size());
    }

    @Override
    public String toString() {
        return "FolderItem{" +
                "coverImagePath='" + coverImagePath + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", numOfImages=" + mImages.size() +
                '}';
    }
}
