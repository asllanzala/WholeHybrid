package com.honeywell.wholesale.ui.selectpic;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.WebServerConfigManager;
import com.honeywell.wholesale.framework.http.VolleyErrorHelper;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.ui.selectpic.Adapter.ImageRecyclerViewAdapter;
import com.honeywell.wholesale.ui.selectpic.Listener.OnFolderRecyclerViewInteractionListener;
import com.honeywell.wholesale.ui.selectpic.Listener.OnImageRecyclerViewInteractionListener;
import com.honeywell.wholesale.ui.selectpic.Utils.FileUtils;
import com.honeywell.wholesale.ui.selectpic.Utils.FolderPopWindow;
import com.honeywell.wholesale.ui.selectpic.Utils.StringUtils;
import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;
import com.honeywell.wholesale.ui.selectpic.model.FolderItem;
import com.honeywell.wholesale.ui.selectpic.model.FolderListContent;
import com.honeywell.wholesale.ui.selectpic.model.ImageItem;
import com.honeywell.wholesale.ui.selectpic.model.ImageListContent;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ImageSelectorActivity extends Activity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener,
        View.OnClickListener {

    private WebClient webClient;

    private ImageView mButtonBack;

    private Button mButtonAllSrc;

    private RecyclerView mRecyclerView;

    private int mColumnCount = 3;

    private ArrayList<String> selected;

    private ContentResolver contentResolver;

    private FolderPopWindow mFolderPopupWindow;

    private View mPopupAnchorView;

    private File mTempImageFile;

    private ProgressDialog progress;

    private final String[] projections = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    private static final int MY_PERMISSIONS_REQUEST_STORAGE_CODE = 197;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA_CODE = 341;

    private static final int CAMERA_REQUEST_CODE = 694;

//    private static final String SERVER_IP_ADDRESS = "http://115.159.152.188:80";

    private static final String API_IMAGE_UPLOAD_PATH = "pic/upload";

    private static String product_id = "";

    private static String shop_id = "";

    private static final String TAG = "ImageSelectorActivity";

    private static boolean mIsRefreshing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_select_image);
        webClient = new WebClient();
        initIntent();
        initView();
        initDate();
        initEvent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        SelectorSetting.mMaxImageNumber = intent
                .getIntExtra(SelectorSetting.SELECTOR_MAX_IMAGE_NUMBER,
                        SelectorSetting.mMaxImageNumber);
        SelectorSetting.isShowCamera = intent.getBooleanExtra(SelectorSetting.SELECTOR_SHOW_CAMERA,
                SelectorSetting.isShowCamera);
        SelectorSetting.mMinImageSize = intent.getIntExtra(SelectorSetting.SELECTOR_MIN_IMAGE_SIZE,
                SelectorSetting.mMinImageSize);
//        selected = intent.getStringArrayListExtra(SelectorSetting.SELECTOR_INITIAL_SELECTED_LIST);

        product_id = intent.getStringExtra(SelectorSetting.SELECTOR_PRODUCT_ID);
        shop_id = intent.getStringExtra(SelectorSetting.SELECTOR_SHOP_ID);

        ImageListContent.clearSelectImages();
        ImageListContent.clear();
    }


    private void initView() {
        mButtonBack = (ImageView) findViewById(R.id.selector_button_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.image_recycerview);

        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mIsRefreshing) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );

        mButtonAllSrc = (Button) findViewById(R.id.btn_all);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        progress = new ProgressDialog(this);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);

        progress.setCanceledOnTouchOutside(false);

//        mPopupAnchorView = findViewById(R.id.selector_footer);
    }


    private void initEvent() {
        mButtonBack.setOnClickListener(this);
        mButtonAllSrc.setOnClickListener(this);
    }


    private void initDate() {
        FolderListContent.clear();
        ImageListContent.clear();

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, 3));
        }
        mRecyclerView.setAdapter(new ImageRecyclerViewAdapter(ImageListContent.IMAGES, this));
        requestReadStorageRuntimePermission();
        currentFolderPath = "";
    }

    public void loadFolderAndImages() {
        //发送一个空数据
        Observable.just("")
                .flatMap(new Func1<String, Observable<ImageItem>>() {
                    @Override
                    public Observable<ImageItem> call(String s) {
                        Log.e(TAG, "call111");
                        List<ImageItem> results = new ArrayList<>();
                        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String where = MediaStore.Images.Media.SIZE + " > "
                                + SelectorSetting.mMinImageSize;
                        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
                        contentResolver = getContentResolver();
                        Cursor cursor = contentResolver
                                .query(contentUri, projections, where, null, sortOrder);
                        if (cursor == null) {

                        } else if (cursor.getCount() == 0) {
                            if (SelectorSetting.isShowCamera) {
                                results.add(ImageListContent.cameraItem);
                            }
                        } else if (cursor.moveToFirst()) {
                            FolderItem allImagesFolderItem = null;
                            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            int nameCol = cursor
                                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                            do {
                                String path = cursor.getString(pathCol);
                                String name = cursor.getString(nameCol);
                                long dateTime = cursor.getLong(DateCol);
                                ImageItem item = new ImageItem(name, path, dateTime);

                                if (FolderListContent.FOLDERS.size() == 0) {
                                    // add folder for all image
                                    Log.e(TAG, "to add size = 0");
                                    FolderListContent.selectedFolderIndex = 0;

                                    // use first image's path as cover image path
                                    allImagesFolderItem = new FolderItem(
                                            getString(R.string.selector_folder_all), "", path);
                                    FolderListContent.addItem(allImagesFolderItem);

                                    // show camera icon ?
                                    if (SelectorSetting.isShowCamera) {
                                        results.add(ImageListContent.cameraItem);
                                        allImagesFolderItem
                                                .addImageItem(ImageListContent.cameraItem);
                                    }
                                }

                                // add image item here, make sure it appears after the camera icon
                                results.add(item);
                                // add current image item to all
                                allImagesFolderItem.addImageItem(item);

                                String folderPath = new File(path).getParentFile()
                                        .getAbsolutePath();

                                FolderItem folderItem = FolderListContent.getItem(folderPath);

                                if (folderItem == null) {
                                    folderItem = new FolderItem(
                                            StringUtils.getLastPathSegment(folderPath), folderPath,
                                            path);
                                    FolderListContent.addItem(folderItem);
                                }
                                folderItem.addImageItem(item);

                            } while (cursor.moveToNext());
                            cursor.close();
                        }

                        return Observable.from(results);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageItem>() {

                    @Override
                    public void onCompleted() {
                        //告诉Subscriber所有的数据都已发射完毕
                        Log.e(TAG, "111111onCompleted");
                        mRecyclerView.getAdapter()
                                .notifyItemChanged(ImageListContent.IMAGES.size() - 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //在发生错误的时候发射一个Throwable对象给Subscriber
                        e.printStackTrace();
                        Log.e(TAG, "22222error" + e.getMessage());
                    }

                    @Override
                    public void onNext(ImageItem item) {
                        //发射处理好的数据
                        ImageListContent.addItem(item);
                        mRecyclerView.getRecycledViewPool().clear();


                    }
                });
    }

    public void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                mTempImageFile = FileUtils.createTmpFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mTempImageFile != null && mTempImageFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempImageFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, R.string.camera_temp_file_error, Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onImageItemInteraction(ImageItem item) {
        if (item.isCamera()) {
            requestCameraRuntimePermissions();
        } else {
            uploadImage();
        }
    }

    public void uploadImage() {
        if (ImageListContent.SELECTED_IMAGES.size() > 0) {
            try {
                product_id = "";
                mIsRefreshing = true;
                progress.show();
                progress.setContentView(R.layout.dialog_loading);
                webClient.httpUpload(WebServerConfigManager.getWebServiceUrl() + API_IMAGE_UPLOAD_PATH,
                        ImageListContent.SELECTED_IMAGES, product_id,
                        shop_id,
                        mProductImageUploadListener, mProductImageUploadErrorListener);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonAllSrc) {
            if (mFolderPopupWindow == null) {
                mFolderPopupWindow = new FolderPopWindow();
                mFolderPopupWindow.initPopupWindow(ImageSelectorActivity.this);
            }

            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mButtonAllSrc, Gravity.BOTTOM, 10, 150);
            }
        } else if (v == mButtonBack) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    //代码中注册存储卡权限
    public void requestReadStorageRuntimePermission() {
        if (ContextCompat.checkSelfPermission(ImageSelectorActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "请求存储卡权限");
            ActivityCompat.requestPermissions(ImageSelectorActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE_CODE);
        } else {
            loadFolderAndImages();
        }
    }

    //代码中注册照相机权限
    public void requestCameraRuntimePermissions() {
        if (ContextCompat
                .checkSelfPermission(ImageSelectorActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ImageSelectorActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ImageSelectorActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA_CODE);
        } else {
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_CODE:
                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFolderAndImages();
                } else {
                    Toast.makeText(ImageSelectorActivity.this, "权限错误", Toast.LENGTH_SHORT).show();
                }

                break;
            case MY_PERMISSIONS_REQUEST_CAMERA_CODE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(ImageSelectorActivity.this, "权限错误", Toast.LENGTH_SHORT).show();
                }

                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTempImageFile != null) {
                    progress.show();
                    // notify system
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(mTempImageFile)));

                    ImageListContent.clear();
                    ImageListContent.SELECTED_IMAGES.add(mTempImageFile.getAbsolutePath());

                    progress.setContentView(R.layout.dialog_loading);
                    try {
                        webClient.httpUpload(WebServerConfigManager.getWebServiceUrl() + API_IMAGE_UPLOAD_PATH, ImageListContent.SELECTED_IMAGES,
                                product_id, shop_id,
                                mProductImageUploadListener, mProductImageUploadErrorListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // if user click cancel, delete the temp file
                while (mTempImageFile != null && mTempImageFile.exists()) {
                    boolean success = mTempImageFile.delete();
                    if (success) {
                        mTempImageFile = null;
                    }
                }
            }
        }


    }

    @Override
    public void onFolderItemInteraction(FolderItem item) {
        OnFolderChange();
    }


    private String currentFolderPath;

    public void OnFolderChange() {
        mFolderPopupWindow.dismiss();

        FolderItem folder = FolderListContent.getSelectedFolder();
        if (!TextUtils.equals(folder.path, this.currentFolderPath)) {
            this.currentFolderPath = folder.path;
            mButtonAllSrc.setText(folder.name);

            ImageListContent.IMAGES.clear();
            ImageListContent.IMAGES.addAll(folder.mImages);
            mRecyclerView.getRecycledViewPool().clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            Log.d(TAG, "OnFolderChange: " + "Same folder selected, skip loading.");
        }
    }

    /**
     * Response body：
     * {
     * "retbody":{"src": "/static/thumb/14659709237134.gif"},
     * "message": "success",
     * "retcode": 200
     * }
     */
    private Response.Listener<NetworkResponse> mProductImageUploadListener
            = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            String jsonString = new String(response.data);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String responseBody = jsonObject.getString("retbody");

                JSONObject imgPathJsonObject = new JSONObject(responseBody);
                String imagePath = imgPathJsonObject.getString("src");
                LogHelper.getInstance().e(TAG, "服务器返回图片地址：" + imagePath);

                JSONObject responseJson = new JSONObject();
                if (ImageListContent.SELECTED_IMAGES.size() > 0){
                    responseJson.put("imgURL", imagePath);
                    responseJson.put("imgLocalPath", ImageListContent.SELECTED_IMAGES.get(0));
                }
                jsonString = responseJson.toString();
            } catch (JSONException e) {
                LogHelper.getInstance().e(TAG, "服务器返回错误：" + jsonString);
                e.printStackTrace();
            }

            if (progress.isShowing()) {
                progress.dismiss();
            }
            mIsRefreshing = false;
            ImageListContent.IMAGES.clear();
            Intent intent = new Intent();
            intent.putExtra(SelectorSetting.SELECTOR_RESULTS, jsonString);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private Response.ErrorListener mProductImageUploadErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            String errorMessage = VolleyErrorHelper.getMessage(error);
            LogHelper.getInstance().e(TAG, "Upload Error! " + errorMessage);
            mIsRefreshing = false;
            if (progress.isShowing()) {
                progress.dismiss();
            }
//            mIsRefreshing = true;
            Toast.makeText(ImageSelectorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    };

    public class WrapContentLinearLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public WrapContentLinearLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public WrapContentLinearLayoutManager(Context context, int spanCount, int orientation,
                boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}