package com.nethergrim.wallpapers.activity;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yandex.metrica.YandexMetrica;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public abstract class BaseActivity extends AppCompatActivity {


    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(@NonNull Subscription subscription){
        if (mCompositeSubscription == null){
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null){
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

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
