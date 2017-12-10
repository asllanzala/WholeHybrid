package com.honeywell.wholesale.framework.search;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by xiaofei on 8/31/16.
 *
 */
public class SearchManager{

    private static SearchManager instance = new SearchManager();

    public static SearchManager getInstance() {
        return instance;
    }

    private SearchManager() {
    }

    public void searchInLocal(View view) {
        if (view instanceof TextView){
            TextView targetView = (TextView)view;

            RxTextView.textChanges(targetView)
                .debounce(600, TimeUnit.MILLISECONDS)
                .map(new Func1<CharSequence, String>() {
                    @Override
                    public String call(CharSequence charSequence) {
                        Log.e("search in local", charSequence.toString());
                        return charSequence.toString();
                    }})
                .map(new Func1<String, ArrayList>(){
                    @Override
                    public ArrayList call(String s) {
                        Log.e("search in local", s);
                        if (apiSearch != null){
                            return apiSearch.queryFromLocal(s);
                        }
                        LogHelper.getInstance().e("search in local", "api search is null");
                        return null;
                    }})
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList>() {
                    @Override
                    public void call(ArrayList ts) {
                        apiSearch.onSearchResultListener(ts);
                    }
                });
        }else {
            throw new RuntimeException("view is not instance of textview");
        }
    }

    public void setApiSearch(ApiSearch apiSearch) {
        this.apiSearch = apiSearch;
    }

    private ApiSearch apiSearch;
}
