package com.nethergrim.wallpapers.inject;

import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.images.ImageLoader;
import com.nethergrim.wallpapers.images.ImageLoaderImpl;
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

    @Provides
    @Singleton
    ImageLoader provideImageLoader() {
        return new ImageLoaderImpl(App.getApp());
    }

    @Provides
    @Singleton
    Prefs providePrefs() {
        return new PrefsImpl();
    }

}
