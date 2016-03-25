package com.nethergrim.wallpapers.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by andrej on 25.03.16.
 */
public class GlideIlImpl implements IL {

    private Context mContext;

    public GlideIlImpl(Context context) {
        mContext = context;
    }

    @Override
    public void displayImage(String url, ImageView imageView, Bitmap.Config config) {
        Glide.with(mContext).load(url).into(imageView);
    }

    @Override
    public void cacheImage(@NonNull String url) {
        Glide.with(mContext).load(url).preload();
    }

    @Override
    public Observable<Bitmap> getBitMap(String url) {
        return Observable.fromCallable(() -> Glide.with(mContext).load(url).asBitmap().into(
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL).get())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> isImageAccessible(String url) {
        return null;
    }
}
