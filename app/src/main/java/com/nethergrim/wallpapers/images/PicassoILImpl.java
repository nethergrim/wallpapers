package com.nethergrim.wallpapers.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class PicassoILImpl implements IL {

    private Context mContext;
    private Picasso mPicasso;

    public PicassoILImpl(Context context, OkHttpClient okHttpClient) {
        mContext = context;

        mPicasso = new Picasso.Builder(mContext)
                .memoryCache(new LruCache(mContext))
                .defaultBitmapConfig(Bitmap.Config.ARGB_8888)
                .downloader(new OkHttpDownloader(okHttpClient))
                .executor(Executors.newCachedThreadPool())
                .build();

    }

    @Override
    public void displayImage(String url, ImageView imageView) {
        mPicasso.cancelRequest(imageView);
        mPicasso.load(url).into(imageView);
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
        return Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(final Subscriber<? super Bitmap> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                            return;
                        }
                        try {
                            Bitmap bitmap = mPicasso.load(url).get();
                            subscriber.onNext(bitmap);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
