package com.nethergrim.wallpapers;

import android.app.Application;

import com.nethergrim.wallpapers.inject.DaggerMainComponent;
import com.nethergrim.wallpapers.inject.MainComponent;
import com.nethergrim.wallpapers.inject.ProviderModule;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public class App extends Application {

    private static App _app;
    private MainComponent mMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        _app = this;
        this.mMainComponent = DaggerMainComponent.builder()
                .providerModule(new ProviderModule())
                .build();
    }

    public MainComponent getMainComponent() {
        return mMainComponent;
    }

    public static App getApp() {
        return _app;
    }
}
