package com.nethergrim.wallpapers.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class PicassoILImpl implements IL {

    private Picasso mPicasso;

    public PicassoILImpl(Context context, OkHttpClient okHttpClient) {

        mPicasso = new Picasso.Builder(context)
                .memoryCache(new LruCache(context))
                .executor(Executors.newFixedThreadPool(5))
                .defaultBitmapConfig(Bitmap.Config.ARGB_8888)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();

    }

    @Override
    public void displayImage(String url, ImageView imageView, Bitmap.Config config) {
        mPicasso.load(url).config(config).into(imageView);
    }

    @Override
    public void cacheImage(@NonNull String url) {
        mPicasso.load(url).fetch();
    }

    @Override
    public Observable<Bitmap> getBitMap(String url) {
        return getBitmapObservable(url);
    }

    @Override
    public Observable<Boolean> isImageAccessible(String url) {
        return getAccessibleObservable(url);
    }

    private Observable<Boolean> getAccessibleObservable(String url) {
        return Observable.create(
                new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(final Subscriber<? super Boolean> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                            return;
                        }
                        try {
                            mPicasso.load(url).get();
                            subscriber.onNext(Boolean.TRUE);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onNext(Boolean.FALSE);
                            subscriber.onCompleted();
                        }
                    }
                });
    }

    private Observable<Bitmap> getBitmapObservable(String url) {
        return Observable.fromCallable(() -> mPicasso.load(url).memoryPolicy(MemoryPolicy.NO_STORE).get())
                .subscribeOn(Schedulers.io());

    }
}
