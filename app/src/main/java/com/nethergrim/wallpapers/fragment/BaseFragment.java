package com.nethergrim.wallpapers.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.images.ImageLoader;

import javax.inject.Inject;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public abstract class BaseFragment extends Fragment {

    @Inject
    protected ImageLoader mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp().getMainComponent().inject(this);
    }
}
