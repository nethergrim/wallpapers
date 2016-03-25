package com.nethergrim.wallpapers.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.nethergrim.wallpapers.App;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by andrej on 01.01.16.
 */
public class ConnectionUtils {


    public static Observable<Boolean> isConnected() {
        return new ReactiveNetwork().observeConnectivity(App.getApp())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(st -> st.equals(ConnectivityStatus.MOBILE_CONNECTED)
                        || st.equals(ConnectivityStatus.WIFI_CONNECTED));
    }

    @NonNull
    public static Bitmap getBitmapFromURL(String src) throws Exception {
        URL url = new URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }
}
