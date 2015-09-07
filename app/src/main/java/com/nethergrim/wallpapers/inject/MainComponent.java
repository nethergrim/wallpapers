package com.nethergrim.wallpapers.inject;

import com.nethergrim.wallpapers.fragment.BaseFragment;
import com.nethergrim.wallpapers.util.UrlHelper;

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

    void inject(UrlHelper urlHelper);

    void inject(BaseFragment baseFragment);
}
