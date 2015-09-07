package com.nethergrim.wallpapers.util;

import android.util.Log;

import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.images.ImageLoader;
import com.nethergrim.wallpapers.storage.Prefs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class UrlHelper {

    public static final String PREVIEW_URL = "https://www.gstatic.com/prettyearth/assets/preview/";
    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";

    @Inject
    Prefs mPrefs;

    @Inject
    ImageLoader mImageLoader;

    public UrlHelper() {
        App.getApp().getMainComponent().inject(this);
    }

    public void getAndPersistAllAccessibleUrls() {
        int min = 0;
        int max = 10000;
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = min; i < max; i++) {
            final int finalI = i;
            executorService.submit(() -> {
                String url = PREVIEW_URL + finalI;
                mImageLoader.isImageAccessible(url)
                        .observeOn(Schedulers.immediate())
                        .subscribeOn(Schedulers.immediate())
                        .subscribe(aBoolean -> {
                            Log.e("good", url);
                            mPrefs.addUrl(url);
                        }, throwable -> {
                            Log.e("TAG", throwable.toString());
                        });
            });
        }
    }

}
