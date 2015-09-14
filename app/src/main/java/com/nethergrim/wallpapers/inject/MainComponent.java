package com.nethergrim.wallpapers.inject;

import com.nethergrim.wallpapers.activity.MainActivity;
import com.nethergrim.wallpapers.fragment.BaseFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
@Singleton
@Component(
        modules = {
                ProviderModule.class
        }
)
public interface MainComponent {

    void inject(BaseFragment baseFragment);

    void inject(MainActivity mainActivity);
}
