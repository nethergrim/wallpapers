package com.nethergrim.wallpapers.inject;

import com.firebase.client.Firebase;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.images.IL;
import com.nethergrim.wallpapers.images.UILILImpl;
import com.nethergrim.wallpapers.storage.Prefs;
import com.nethergrim.wallpapers.storage.PrefsImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */

@Module
public class ProviderModule {

    private App mApp;

    public ProviderModule(App app) {
        mApp = app;
    }

    @Provides
    @Singleton
    IL provideImageLoader() {
        return new UILILImpl();
    }

    @Provides
    @Singleton
    Prefs providePrefs() {
        return new PrefsImpl();
    }

    @Provides
    @Singleton
    Firebase provideBaseRef() {
        return new Firebase("https://wallpapers-nethergrim.firebaseio.com");
    }

    @Provides
    @Singleton
    Tracker provideAnalyticsTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApp);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        return analytics.newTracker(R.xml.global_tracker);
    }

}
