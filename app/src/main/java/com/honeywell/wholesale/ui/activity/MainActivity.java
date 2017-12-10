package com.honeywell.wholesale.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.application.view.DialogUtil;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AppUpgradeInfo;
import com.honeywell.wholesale.framework.utils.FileManager;
import com.honeywell.wholesale.framework.utils.SharePreferenceUtil;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.base.WholesaleBaseFragmentActivity;
import com.honeywell.wholesale.ui.dashboard.fragment.FriendFragment;
import com.honeywell.wholesale.ui.dashboard.fragment.InventoryNativeFragment;
import com.honeywell.wholesale.ui.dashboard.fragment.MainPageFragment;
import com.honeywell.wholesale.ui.dashboard.fragment.NewReportNativeFragment;
import com.honeywell.wholesale.ui.dashboard.fragment.TransactionFragment;
import com.honeywell.wholesale.ui.eventbus.MainEvent;
import com.honeywell.wholesale.ui.util.ResourceUtil;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import de.greenrobot.event.EventBus;


/**
 * Created by milton_lin on 17/5/3.
 */

public class MainActivity extends WholesaleBaseFragmentActivity implements android.widget.TabHost.OnTabChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_KEY_CURRENT_FRAGMENT = "INTENT_KEY_CURRENT_FRAGMENT";
    private static final String SHARE_PREFERENCE_APP_USER_PICS = "userPics";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_URL = "userPicsUrl";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_HD_URL = "userPicsHdUrl";

    private static final int SHOP = 111;

    public static final int INTENT_VALUE_CURRENT_FRAGMENT_HOME = 1;
    public static final int INTENT_VALUE_CURRENT_FRAGMENT_CART = 2;

    private static final String SHARE_PREFERENCE_APP_UPGRADE_TIME = "AppUpgradeTime";
    private static final String SHARE_PREFERENCE_KEY_LAST_CANCEL_TIME = "AppUpgradeLastCancelTime";
    private ImageView homeTabbarItem;
    private DrawerLayout mDrawerLayout;
    private String mHomeText;
    /**
     * FragmentTabhost
     */
    private FragmentTabHost mTabHost;

    /**
     * 布局填充器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * Fragment数组界面
     */
    public Class mFragmentArray[] = {
            InventoryNativeFragment.class,
            TransactionFragment.class,
            MainPageFragment.class,
            FriendFragment.class,
            NewReportNativeFragment.class
    };

    private Bitmap mHeadPortrait;

    private void getHeadPortrait() {
        final String picUrl = SharePreferenceUtil.getPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_HD_URL, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                 mHeadPortrait = getHttpBitmap(picUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new MainEvent(MainEvent.REFRESH_HEAD_PORTRAIT));
                    }
                });

            }
        }).start();
    }

    public void refreshHeadPortrait(final ImageView userImageView) {
        Log.e("alinmi", "userImageView = " + userImageView + " , mHeadPortrait = " + mHeadPortrait);
        if (mHeadPortrait == null) {
            userImageView.setImageResource(R.drawable.user_icon);
        } else {
            userImageView.setImageBitmap(mHeadPortrait);
        }

    }

//    public void reflashCurrentPic(final ImageView userImageView) {
//        final String picUrl = SharePreferenceUtil.getPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_HD_URL, "");
//
//        if (picUrl.equals("")) {
//            userImageView.setImageResource(R.drawable.user_icon);
//        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final Bitmap bitmap = getHttpBitmap(picUrl);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (bitmap == null) {
//                                userImageView.setImageResource(R.drawable.user_icon);
//                            } else {
//                                userImageView.setImageBitmap(bitmap);
//                            }
//                        }
//                    });
//
//                }
//            }).start();
//        }
//    }

    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }

//    /**
//     * 重绘employee的头像
//     */
//    public void reflashCurrentPic2(final ImageView imageView) {
//        final String picUrl = SharePreferenceUtil.getPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_HD_URL, "");
//        Log.e("alinmi", "reflashCurrentPic  ImageView = " + imageView + " , picUrl = " + picUrl);
//
////        Glide
////                .with(getApplicationContext())
////                .load(picUrl)
////                .placeholder(R.drawable.delete)
////                .error(R.drawable.friend_title)
////                .priority(Priority.HIGH)
////                .into(imageView);
////        mDrawerLayout.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                Glide
////                        .with(getApplicationContext())
////                        .load(picUrl)
////                        .placeholder(R.drawable.user_icon)
////                        .error(R.drawable.user_icon)
////                        .priority(Priority.LOW)
////                        .into(imageView);
////            }
////        }, 200);
//
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
//        showLoadingDialog();
        checkAppUpgrade();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        mHomeText = getString(R.string.dashboard_fragment_title_home);
        homeTabbarItem = (ImageView) findViewById(R.id.tab_home_icon);
        homeTabbarItem.setOnClickListener(homeTabClickListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLayoutInflater = LayoutInflater.from(this);
        // 找到TabHost
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        // 得到fragment的个数
        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getResources().getStringArray(R.array.main_tabbar_title)[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            // 设置Tab按钮的背景
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.main_tabbar_item_background);
        }
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(2);
    }

    public void setListener() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("START")) {

                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    /**
     * 给每个Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(ResourceUtil.getResourceIdArray(getResources(), R.array.main_tabbar_icon)[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(ResourceUtil.getStringArray(getResources(), R.array.main_tabbar_title)[index]);
        return view;
    }


    @Override
    public void onTabChanged(String arg0) {
//        LogUtil.e("milton", "onTabChanged -> " + arg0);
        homeTabbarItem.setSelected(mHomeText.equalsIgnoreCase(arg0));
    }

    public void openLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
    }

    public void closeLeftMenu() {
        closeLeftMenu(true);
    }

    public void closeLeftMenu(boolean animate) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, animate);
        }
    }

    private View.OnClickListener homeTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //homeTabbarItem.setSelected(true);
            if (mTabHost.getCurrentTab() != 2) {
                mTabHost.setCurrentTab(2);
            }
//            switchFragmentToHome();
        }
    };
//    private long firstTime = 0;
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                    return true;
//                }
//                long secondTime = System.currentTimeMillis();
//                if (secondTime - firstTime > 2000) {
////                    ToastUtil.showShort(MainActivity.this, R.string.double_click_exit, true);
//                    firstTime = secondTime;
//                    return true;
//                } else {
////                    LogUtil.e("alinmi221", " P2PConn.unInitConnLib() start");
////                    ToastUtil.hideToast();
//                    System.exit(0);
//
//                }
//                break;
//        }
//        return super.onKeyUp(keyCode, event);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("alinmi", " MainActivity onResume");
        reLoadDataForCurrentFragment();
        getHeadPortrait();
    }


    public void reLoadDataForCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());

        if (fragment == null) {
            return;
        }

        if (fragment instanceof BaseRootFragment) {
            ((BaseRootFragment) fragment).reLoadData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHOP && resultCode == RESULT_OK && data != null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("START");
            fragment.onActivityResult(requestCode, resultCode, data);
            ///slidingMenu.showMenu();
            closeLeftMenu();
            //slidingMenu.showSecondaryMenu();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            DialogInterface.OnClickListener positiveBtnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                    dialog.dismiss();
                }
            };

            DialogInterface.OnClickListener negativeBtnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            DialogUtil.showDialog(MainActivity.this, "提示", "确认退出吗？", "确认", "取消", positiveBtnClickListener, negativeBtnClickListener);
        }
    }


    /**
     * 检测版本更新
     */
    private void checkAppUpgrade() {
        Date nowTime = new Date();
        long lastCancelTime = SharePreferenceUtil.getPrefLong(SHARE_PREFERENCE_APP_UPGRADE_TIME, SHARE_PREFERENCE_KEY_LAST_CANCEL_TIME, -1);
        if (lastCancelTime != -1) {
            // 24消失后在提醒
            boolean isTimeToCheckAgain = (nowTime.getTime() - lastCancelTime) > 24 * 60 * 60 * 1000;
//            boolean isTimeToCheckAgain = (nowTime.getTime() - lastCancelTime) > 3* 1000;
            if (!isTimeToCheckAgain) {
                LogHelper.getInstance().d(TAG, "用户上次取消至今,还没到提示更新时间");
                return;
            }
        }

        // Get current app version info
        final Context context = WholesaleApplication.getAppContext();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            return;
        }

        final int currentVersionCode = pInfo.versionCode;
        Response.Listener httpOkResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取版本信息成功：");
                Gson gson = new Gson();
                String upgradeInfo = response.optString("device");
                AppUpgradeInfo info = gson.fromJson(upgradeInfo, AppUpgradeInfo.class);

                if (info.getVersionCodeInt() > currentVersionCode) {
                    showDownloadDialog(info.getVersionDetail(), info.getUrl(), info.getVersionName(), info.getVersionCodeInt());
                } else {
                    LogHelper.getInstance().d(TAG, "不需要更新App");
                }
            }
        };

        // Check version in cloud
        WebClient webClient = new WebClient();
        webClient.httpCheckAppUpgrade(currentVersionCode, httpOkResponseListener);
    }

    /**
     * 显示对话框,让用户确定是否下载
     *
     * @param downloadUrl
     * @param versionCode
     * @param versionName
     */
    private void showDownloadDialog(final String versionDetail, final String downloadUrl, final String versionName, final int versionCode) {
        DialogInterface.OnClickListener positiveBtnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FileManager.isApkExist(versionName, versionCode)) {
                    // 直接弹出更新
                    String apkFilePath = FileManager.getApkFilePath(versionName, versionCode);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
                    startActivity(intent);
                } else {
                    FileManager.downloadApkFile(MainActivity.this, downloadUrl, versionName, versionCode);
                }
                dialog.dismiss();
            }
        };

        DialogInterface.OnClickListener negativeBtnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date currentTime = new Date();
                // 用户取消更新
                SharePreferenceUtil.setPrefLong(SHARE_PREFERENCE_APP_UPGRADE_TIME, SHARE_PREFERENCE_KEY_LAST_CANCEL_TIME, currentTime.getTime());
                dialog.dismiss();
            }
        };

        DialogUtil.showDialog(MainActivity.this, "更新提示", versionDetail, "更新", "暂不更新", positiveBtnClickListener, negativeBtnClickListener);
//        DialogUtil.showDialog(MainActivity.this, "更新提示", versionDetail, "更新", positiveBtnClickListener);
    }
}
