package com.nethergrim.wallpapers.inject;

import com.firebase.client.Firebase;
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

}
