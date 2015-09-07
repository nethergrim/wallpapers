package com.nethergrim.wallpapers.storage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nethergrim.wallpapers.App;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class PrefsImpl implements Prefs {

    public static final String KEY_URLS = "urls";
    private SharedPreferences mPreferences;

    public PrefsImpl() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(App.getApp());
    }

    @Override
    public void addUrl(String url) {
        List<String> urls = getAllUrls();
        urls.add(url);
        mPreferences.edit().putStringSet(KEY_URLS, new HashSet<>(urls)).commit();
    }

    @Override
    public List<String> getAllUrls() {
        Set<String> urls = mPreferences.getStringSet(KEY_URLS, new HashSet<>(0));
        return new ArrayList<>(urls);
    }

    @Override
    public void persistList(List<String> data) {
        mPreferences.edit().putStringSet(KEY_URLS,new HashSet<>(data)).apply();
        Log.e("TAG", "persisted");
    }
}
