package com.nethergrim.wallpapers;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.client.Config;
import com.firebase.client.Firebase;
import com.nethergrim.wallpapers.inject.DaggerMainComponent;
import com.nethergrim.wallpapers.inject.MainComponent;
import com.nethergrim.wallpapers.inject.ProviderModule;
import com.yandex.metrica.YandexMetrica;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public class App extends Application {

    private static App _app;
    private MainComponent mMainComponent;
    public static float density;

    @Override
    public void onCreate() {
        super.onCreate();
        density = getResources().getDisplayMetrics().density;
        _app = this;
        this.mMainComponent = DaggerMainComponent.builder()
                .providerModule(new ProviderModule(this))
                .build();
        Firebase.setAndroidContext(this);
        Config config = new Config();
        config.setPersistenceEnabled(true);
        Firebase.setDefaultConfig(config);
        YandexMetrica.activate(this, "c382286b-24f3-48e0-a834-294f47c4756f");
        YandexMetrica.setTrackLocationEnabled(false);
        YandexMetrica.setCollectInstalledApps(false);
        Fresco.initialize(this);
    }

    public MainComponent getMainComponent() {
        return mMainComponent;
    }

    public static App getApp() {
        return _app;
    }
}
