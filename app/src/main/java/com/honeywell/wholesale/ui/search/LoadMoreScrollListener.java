package com.honeywell.wholesale.ui.search;

import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by xiaofei on 9/13/16.
 *
 */

public class LoadMoreScrollListener implements AbsListView.OnScrollListener {

    private int currentFirstVisibleItem;
    private int currentLastVisibleItem;
    private int totalItem;
    private int currentVisibleItemCount;
    private int currentScrollState;

    private static boolean isLoading = false;


    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentLastVisibleItem = firstVisibleItem+visibleItemCount;
        //this.currentVisibleItemCount = totalItemCount;

        this.currentFirstVisibleItem = firstVisibleItem;
        this.totalItem = totalItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //if(this.currentVisibleItemCount == currentLastVisibleItem && scrollState == SCROLL_STATE_IDLE){
            Log.e("isLoading", "yes");
            this.currentScrollState = scrollState;
            this.isScrollCompleted();
        //}

    }

    private void isScrollCompleted() {
        //if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
        if(this.totalItem == currentLastVisibleItem && this.currentScrollState == SCROLL_STATE_IDLE){
                /*** In this way I detect if there's been a scroll which has completed ***/
                /*** do the work for load more date! ***/
                if(!isLoading){
                    isLoading = true;
                    loadMore.loadMoreData();
                }
            }
    }

    public void setLoadMore(LoadMore loadMore) {
        this.loadMore = loadMore;
    }

    private LoadMore loadMore;

    public interface LoadMore{
        void loadMoreData();
    }

    public static void setIsLoading(boolean isLoading) {
        LoadMoreScrollListener.isLoading = isLoading;
    }
}
