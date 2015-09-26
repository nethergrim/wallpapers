package com.nethergrim.wallpapers.activity;

import android.support.v7.app.AppCompatActivity;

import com.yandex.metrica.YandexMetrica;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        YandexMetrica.onPauseActivity(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.onResumeActivity(this);
    }

}
