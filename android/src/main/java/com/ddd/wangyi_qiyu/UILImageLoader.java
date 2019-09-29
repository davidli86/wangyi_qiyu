package com.ddd.wangyi_qiyu;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;

/**
 * Created by hzwangchenyan on 2016/3/31.
 */
public class UILImageLoader implements UnicornImageLoader {
    private static final String TAG = "UILImageLoader";

    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        // check cache
        boolean cached = true;
        ImageDownloader.Scheme scheme = ImageDownloader.Scheme.ofUri(uri);
        if (scheme == ImageDownloader.Scheme.HTTP
                || scheme == ImageDownloader.Scheme.HTTPS
                || scheme == ImageDownloader.Scheme.UNKNOWN) {
            // non local resource
            cached = MemoryCacheUtils.findCachedBitmapsForImageUri(uri, ImageLoader.getInstance().getMemoryCache()).size() > 0
                    || DiskCacheUtils.findInCache(uri, ImageLoader.getInstance().getDiskCache()) != null;
        }

        if (cached) {
            ImageSize imageSize = (width > 0 && height > 0) ? new ImageSize(width, height) : null;
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(uri, imageSize, options);
            if (bitmap == null) {
                Log.e(TAG, "load cached image failed, uri =" + uri);
            }
            return bitmap;
        }

        return null;
    }

    @Override
    public void loadImage(String uri, int width, int height, final ImageLoaderListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageSize imageSize = (width > 0 && height > 0) ? new ImageSize(width, height) : null;

        ImageLoader.getInstance().loadImage(uri, imageSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (listener != null) {
                    listener.onLoadComplete(loadedImage);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                if (listener != null) {
                    listener.onLoadFailed(failReason.getCause());
                }
            }
        });
    }
}
