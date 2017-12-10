package com.honeywell.wholesale.framework.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.ui.selectpic.Utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by H155935 on 16/6/7.
 * Email: xiaofei.he@honeywell.com
 */
public class FileManager {
    private static final String TAG = "FileManager";
    private static final String HTML_FOLDER_PATH = "/honeywell/Wholesale/html/";
    private static final String APK_FOLDER_PATH = "/honeywell/Wholesale/apk/";
    private static String downloadHtmlPath;

    private static String downloadApkPath;

    private static long downloadApkRefId = -1;


    public static ArrayList<String> getPhotoPath(Context context){
        ArrayList <String> paths = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(photoUri, null, null, null, null);

        if (cursor == null || cursor.getCount() <= 0){
            return paths;
        }
        cursor.moveToFirst();

        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index);
            File file = new File(path);
            if (file.exists()){
                paths.add(path);
            }
        }

        cursor.close();
        return paths;
    }

    public static ArrayList<String> getThumbnailsPath(Context context){
        ArrayList <String> paths = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri photoUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(photoUri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0){
            return paths;
        }
        cursor.moveToFirst();

        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index);
            File file = new File(path);
            if (file.exists()){
                paths.add(path);
            }
        }
        cursor.close();
        return paths;
    }

    public static String getHtmlFolder(){
        String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(extStorageDirectory, HTML_FOLDER_PATH);

        if(!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getPath();
    }

    public static String getApkFolder(){
        String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(extStorageDirectory, APK_FOLDER_PATH);

        if(!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getPath();
    }

    public static long downloadHtmlFile(Context context, String urlString, String fileName){
        final DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(urlString);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        String pathUrl = getHtmlFolder() + "/" + fileName;

        File file = new File(pathUrl);

        if (file.exists()){
            return -1;
        }

        Uri destinationUri = Uri.fromFile(file);
        request.setDestinationUri(destinationUri);

        request.setVisibleInDownloadsUi(true);
        final long reference = mDownloadManager.enqueue(request);
        downloadHtmlPath = pathUrl;
        return reference;
    }

    public static String getApkFilePath(String versionName, int versionCode) {
        return getApkFolder() + "/" + generateApkFileName(versionName, versionCode);
    }

    public static boolean isApkExist(String versionName, int versionCode) {
        String pathUrl = getApkFilePath(versionName, versionCode);
        File file = new File(pathUrl);
        return file.exists();
    }

    public static String generateApkFileName(String versionName, int versionCode) {
        return "Wholesale" + versionName + "-" + versionCode + ".apk";
    }

    public static long downloadApkFile(Context context, String urlString, String versionName, int versionCode){
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(urlString);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        String pathUrl = getApkFolder() + "/" + generateApkFileName(versionName, versionCode);
        File file = new File(pathUrl);
        if (file.exists()){
            file.delete();
        }

        Uri destinationUri = Uri.fromFile(file);
        request.setDestinationUri(destinationUri);

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
//        request.setTitle("new Version");
        final long reference = downloadManager.enqueue(request);
        downloadApkPath = pathUrl;
        downloadApkRefId = reference;
        return reference;
    }

    public static boolean removeFolder(String folderPath){
        File file = new File(folderPath);
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    Log.e("folder", files[i].getPath());
                    removeFolder(files[i].getPath());
                }
                else {
                    boolean isDeleted = files[i].delete();
                    Log.e("isDeleted", String.valueOf(isDeleted));
                }
            }
        }
        return file.delete();
    }

    public static void unzip(String zipFilePath, String location) throws IOException {
        File f = new File(location);
        if (!f.isDirectory()){
            f.mkdirs();
        }

        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry ze = null;
        try{
            while ((ze = zis.getNextEntry()) != null){
                String path = location + ze.getName();

                if (ze.isDirectory()){
                    File unzipFile = new File(path);

                    if(!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                }else {
                    FileOutputStream fout = null;
                    fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zis.read(); c != -1; c = zis.read()) {
                            fout.write(c);
                        }
                    } finally {
                        fout.close();
                    }

                    zis.closeEntry();
                }
            }
        }finally {
            zis.close();
        }
    }

    public static void moveAssets2HtmlPath(Context context, String path) throws IOException {
        // initial html folder for assets
        File assetsFolder = new File(getHtmlFolder(), "assets");

        if(!assetsFolder.exists()) {
            if (!assetsFolder.mkdirs()){
                throw new IOException("Can not create assets folder in SD card");
            }
        }

        AssetManager assetManager = context.getAssets();
        // TODO why folder with subfolder always return 0;
        String[] files = assetManager.list(path);

        if (files.length == 0) {
            copyFile(path, context, assetsFolder);
        } else{
            String fullPath = assetsFolder.getPath() + "/" + path;
            File dir = new File(fullPath);
            if (!dir.exists())
                dir.mkdir();
            for (int i = 0; i < files.length; ++i) {
                moveAssets2HtmlPath(context, path + "/" + files[i]);
            }
        }
    }

    private static void copyFile(String filename, Context context, File baseFile) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = baseFile.getPath() + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            LogHelper.getInstance().e(TAG, e.getMessage());
        }
    }

    private static void copyFile(File sourceFile, File targetFile) throws IOException {
        if (sourceFile.isDirectory()) {
            if (!targetFile.exists()) {
                targetFile.mkdir();
            }

            String[] children = sourceFile.list();
            for (int i = 0; i < sourceFile.listFiles().length; i++) {
                copyFile(new File(sourceFile, children[i]),
                        new File(targetFile, children[i]));
            }
        }else {
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(targetFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static long getDownloadApkRefId() {
        return downloadApkRefId;
    }

    public static String getDownloadApkPath() {
        return downloadApkPath;
    }

}
