package com.nethergrim.wallpapers.util;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.nethergrim.wallpapers.App;

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
}
