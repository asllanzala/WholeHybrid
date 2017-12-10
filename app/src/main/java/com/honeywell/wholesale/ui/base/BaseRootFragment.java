package com.honeywell.wholesale.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.eventbus.MainEvent;

import de.greenrobot.event.EventBus;

public abstract class BaseRootFragment extends Fragment {
    private static final String TAG = BaseRootFragment.class.getSimpleName();
    private View mRootView;// 缓存Fragment view

    View mLayoutTitleBar;
    ImageView mImagePortrait;
    ImageView mImageTitle;
    ImageView mImageMore;
    View mLayoutSearch;
    protected boolean bHasTitleBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("alinmi", getIndex() + "  onCreateView");
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayout(), container, false);
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            initView(mRootView);
            getData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("alinmi", getIndex() + "  onViewCreated");
    }

    public void initView(View view) {
        mLayoutTitleBar = view.findViewById(R.id.main_title_bar);
        bHasTitleBar = mLayoutTitleBar != null;
        if (bHasTitleBar) {
            mImagePortrait = (ImageView) view.findViewById(R.id.iv_portrait);
            mImageTitle = (ImageView) view.findViewById(R.id.iv_title);
            mImageMore = (ImageView) view.findViewById(R.id.iv_more);
            mLayoutSearch = view.findViewById(R.id.rl_search_layout);
            if (getIndex() == 4) {
                mImageTitle.setVisibility(View.VISIBLE);
                mLayoutSearch.setVisibility(View.GONE);
                mImageMore.setVisibility(View.GONE);
            }
            initImageMore(mImageMore);
            initLayoutSearch(mLayoutSearch);
        } else {
            mImagePortrait = (ImageView) view.findViewById(R.id.main_page_avatar);
        }
        initImagePortrait(mImagePortrait);
        updateHeadPortrait(mImagePortrait);
    }

    public abstract int getLayout();

    public abstract int getIndex();

    public void initImagePortrait(ImageView view) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    syncShopListFromServer();  do nothing
                    ((MainActivity) getActivity()).openLeftMenu();
                }
            });

        }
    }

    public void initImageMore(ImageView view) {
        if (view != null) {
        }
    }

    public void initLayoutSearch(View view) {
        if (view != null) {

        }
    }

    public void getData() {

    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity) getActivity()).reflashCurrentPic(mImagePortrait);
        Log.e("alinmi", getIndex() + "  onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(MainEvent event) {

        Log.e("alinmi", "onEventMainThread收到了消息：" + event.getMessage());
        final int msg = event.getMessage();
        switch (msg) {
            case MainEvent.REFRESH_HEAD_PORTRAIT:
                updateHeadPortrait(mImagePortrait);
                break;
            default:
                break;
        }
    }

    private void updateHeadPortrait(ImageView imageView) {
        ((MainActivity) getActivity()).refreshHeadPortrait(imageView);
    }
//    public void showToastShort(int res) {
//        showToastShort(getString(res));
//    }
//
//    public void showToastShort(String text) {
//        ToastUtil.showShort(getContext(), text, true);
//    }
//
//    public void showToastLong(int res) {
//        showToastLong(getString(res));
//    }
//
//    public void showToastLong(String text) {
//        ToastUtil.showLong(getContext(), text, true);
//    }

    public abstract void reLoadData();

    public abstract String getTitle();
}
