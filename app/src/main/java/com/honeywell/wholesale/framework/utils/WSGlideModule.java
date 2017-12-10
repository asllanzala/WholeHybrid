package com.honeywell.wholesale.framework.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.http.OkHttpUrlLoader;

import java.io.File;
import java.io.InputStream;

/**
 * Created by xiaofei on 12/28/16.
 *
 */

public class WSGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int memoryCacheSize = maxMemory / 8;
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));

        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));

        File cacheDir = context.getExternalCacheDir();

        LogHelper.e("WSGlideModule", cacheDir.getPath());
        if (cacheDir != null){
            int diskCacheSize = 1024 * 1024 * 30;
            builder.setDiskCache(new DiskLruCacheFactory(cacheDir.getPath(), "glide", diskCacheSize));
        }
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }

}
