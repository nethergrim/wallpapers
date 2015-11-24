package com.nethergrim.wallpapers.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

import rx.Observable;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public interface IL {

    void displayImage(String url, ImageView imageView);

    Observable<Bitmap> getBitMap(String url);

    Observable<Boolean> isImageAccessible(String url);

}
