package com.nethergrim.wallpapers.images;

import android.content.Context;
import android.graphics.Bitmap;

import com.nethergrim.wallpapers.util.ConnectionUtils;
import com.nethergrim.wallpapers.util.RetryWithDelay;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by andrej on 25.03.16.
 */
public class GlideIlImpl implements IL {


    public GlideIlImpl(Context context) {

    }

    @Override
    public Observable<Bitmap> getBitMap(String url) {
        return Observable.fromCallable(() -> ConnectionUtils.getBitmapFromURL(url))
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(10, 300));
    }
}
