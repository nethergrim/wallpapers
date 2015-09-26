package com.nethergrim.wallpapers.activity;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    private Toast mToast;

    protected void showToast(String s){
        if (mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showToast(@StringRes int s){
        showToast(getString(s));
    }

}
