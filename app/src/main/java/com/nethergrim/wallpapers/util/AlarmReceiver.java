package com.nethergrim.wallpapers.util;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.images.IL;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 15.09.15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmReceiver.class.getSimpleName();
    @Inject
    IL mIL;

    public AlarmReceiver() {
        App.getApp().getMainComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent");
        Observable.just(Boolean.TRUE)
                .subscribeOn(Schedulers.io())
                .map(aBoolean -> {
                    JSONArray jsonArray = FileUtils.getJSONFromAssets(
                            FileUtils.JSON_FILE_NAME);
                    return new PictureHelper(jsonArray);
                })
                .map(pictureHelper -> pictureHelper.getFullResFromId(
                        pictureHelper.getPictureId(17)))
                .flatMap(mIL::getBitMap)
                .map(bitmap -> {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    return new ByteArrayInputStream(bitmapdata);
                })
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream is) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(
                                App.getApp());
                        try {
                            wallpaperManager.setStream(is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d(TAG, "changed wallpaper successfully");
                }, throwable -> {
                    Log.e(TAG, throwable.toString());
                });
    }
}
