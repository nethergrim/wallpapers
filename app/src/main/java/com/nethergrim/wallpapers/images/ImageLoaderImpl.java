package com.nethergrim.wallpapers.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class ImageLoaderImpl implements ImageLoader {

    private Context mContext;

    public ImageLoaderImpl(Context context) {
        mContext = context;
    }

    @Override
    public void displayImage(String url, ImageView imageView) {
        Picasso.with(mContext).load(url).into(imageView);
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
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                subscriber.onNext(Boolean.TRUE);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                subscriber.onNext(Boolean.FALSE);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        };
                        Picasso.with(mContext).load(url)
                                .config(Bitmap.Config.RGB_565)
                                .into(target);
                    }
                });
    }

    private Observable<Bitmap> getBitmapObservable(String url) {
        Observable<Bitmap> bitmapObservable = Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(final Subscriber<? super Bitmap> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                            return;
                        }
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            Log.i("ISSUE5", " call - main thread ");
                        }
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Log.i("picasso", " onBitmapLoaded ");
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    Log.i("ISSUE5", " Target - onBitmapLoaded - main thread ");
                                }
                                subscriber.onNext(bitmap);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                subscriber.onError(new Exception(" Failed to load bitmap"));
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        };
                        Picasso.with(mContext).load(url)
                                .config(Bitmap.Config.RGB_565)
                                .into(target);
                    }
                });
        bitmapObservable.subscribeOn(AndroidSchedulers.mainThread());
        bitmapObservable.observeOn(Schedulers.io());
        return bitmapObservable;

    }
}
