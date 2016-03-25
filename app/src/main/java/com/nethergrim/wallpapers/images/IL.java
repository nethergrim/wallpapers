package com.nethergrim.wallpapers.images;

import android.graphics.Bitmap;

import rx.Observable;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public interface IL {

//    void displayImage(String url, ImageView imageView, Bitmap.Config config);

//    void cacheImage(@NonNull String url);

    Observable<Bitmap> getBitMap(String url);

}
