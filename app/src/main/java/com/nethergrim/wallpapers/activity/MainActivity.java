package com.nethergrim.wallpapers.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.BuildConfig;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.fragment.ImageFragment;
import com.nethergrim.wallpapers.images.ImageLoader;
import com.nethergrim.wallpapers.storage.Prefs;
import com.nethergrim.wallpapers.util.AlarmReceiver;
import com.nethergrim.wallpapers.util.FileUtils;
import com.nethergrim.wallpapers.util.LayoutAnimator;
import com.nethergrim.wallpapers.util.PictureHelper;
import com.rey.material.widget.Switch;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements Switch.OnCheckedChangeListener {

    @InjectView(R.id.pager)
    ViewPager mPager;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.btn_share)
    ImageButton mBtnShare;
    @InjectView(R.id.btn_download)
    ImageButton mBtnDownload;
    @InjectView(R.id.btn_set_wallpaper)
    ImageButton mBtnSetWallpaper;
    @InjectView(R.id.btn_thumps_down)
    ImageButton mBtnThumpsDown;
    @InjectView(R.id.btn_thumps_up)
    ImageButton mBtnThumpsUp;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    Firebase mFirebase;
    @Inject
    Prefs mPrefs;
    @InjectView(R.id.progressBar2)
    ProgressBar mProgressBar2;
    @InjectView(R.id.shadow)
    View mShadow;
    @InjectView(R.id.btn_layout)
    LinearLayout mBtnLayout;
    @InjectView(R.id.switch_auto_change)
    Switch mSwitchAutoChange;
    @InjectView(R.id.settings_layout)
    LinearLayout mSettingsLayout;
    private PagerAdapter mPagerAdapter;
    private LayoutAnimator mLayoutAnimator;
    private GestureDetector mTapGestureDetector;

    @Override
    public void onCheckedChanged(Switch aSwitch, boolean b) {
        mPrefs.setAutoRefresh(b);
        if (b) {
            Toast.makeText(this, R.string.wallpapers_will_be_switched, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_HOUR,
                    AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        } else {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getApp().getMainComponent().inject(this);
        ButterKnife.inject(this);
        mLayoutAnimator = new LayoutAnimator(mBtnLayout, mSettingsLayout,
                getResources().getDimensionPixelSize(R.dimen.action_bar_height));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mPager.setOffscreenPageLimit(4);
        mTapGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mLayoutAnimator.toggle();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1,
                    MotionEvent e2,
                    float distanceX,
                    float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1,
                    MotionEvent e2,
                    float velocityX,
                    float velocityY) {
                return false;
            }
        });
        mPager.setOnTouchListener((v, event) -> mTapGestureDetector.onTouchEvent(event));
        mSwitchAutoChange.setChecked(mPrefs.isAutoRefreshEnabled());
        mSwitchAutoChange.setOnCheckedChangeListener(this);
        Observable.just(Boolean.TRUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(aBoolean -> {
                    JSONArray jsonArray = FileUtils.getJSONFromAssets(
                            FileUtils.JSON_FILE_NAME);
                    return new PictureHelper(jsonArray);
                })
                .subscribe(ph -> {
                    mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), ph);
                    mPager.setAdapter(mPagerAdapter);
                });
        if (BuildConfig.PAID.equalsIgnoreCase("true")) {
            Toast.makeText(this, "Thank you for purchase! =)))", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_download)
    void onDownloadClick() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(
                Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(getCurrentUrl()));
        request.setTitle(getString(R.string.wallpaper) + " " + mPagerAdapter.getCurrentId(
                mPager.getCurrentItem()));
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    @OnClick(R.id.btn_share)
    void onShareClick() {
        showOverlay();
        mImageLoader.getBitMap(getCurrentUrl())
                .map(FileUtils::persistBitmapToDisk)
                .subscribe(uri -> {
                    hideOverlay();
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent,
                            getResources().getText(R.string.send_to)));
                }, throwable -> {
                    hideOverlay();
                });

    }

    @OnClick(R.id.btn_set_wallpaper)
    void onWallpaperClick() {
        showOverlay();
        mImageLoader.getBitMap(getCurrentUrl())
                .map(bitmap -> {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    return new ByteArrayInputStream(bitmapdata);
                })
                .subscribeOn(Schedulers.newThread())
                .doOnNext(inputStream -> {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(
                            App.getApp());
                    try {
                        wallpaperManager.setStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .subscribe(bitmap -> {
                    hideOverlay();

                }, throwable -> {
                    Log.e("TAG", throwable.toString());
                    hideOverlay();
                });

    }

    @OnClick(R.id.btn_thumps_up)
    void onThumbsUpClick() {
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
        int id = getCurrentId();
        mFirebase.child("ratings").child(String.valueOf(id)).runTransaction(
                new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        try {
                            if (mutableData.getValue() == null) {
                                mutableData.setValue(1);
                            } else {
                                Integer rating = mutableData.getValue(Integer.class);
                                mutableData.setValue(rating + 1);
                            }

                            return Transaction.success(mutableData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return Transaction.abort();
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError,
                            boolean b,
                            DataSnapshot dataSnapshot) {
                        if (firebaseError != null) {
                            YandexMetrica.reportEvent(firebaseError.getMessage() + " " + firebaseError.getDetails());
                        }

                    }
                });
    }

    @OnClick(R.id.btn_thumps_down)
    void onThumbsDownClick() {
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
        int id = getCurrentId();
        mFirebase.child("ratings").child(String.valueOf(id)).runTransaction(
                new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        try {
                            if (mutableData.getValue() == null) {
                                mutableData.setValue(-1);
                            } else {
                                Integer rating = mutableData.getValue(Integer.class);
                                mutableData.setValue(rating - 1);
                            }

                            return Transaction.success(mutableData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return Transaction.abort();
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError,
                            boolean b,
                            DataSnapshot dataSnapshot) {

                    }
                });
    }

    private void showOverlay() {
        mProgressBar2.setVisibility(View.VISIBLE);
        mShadow.setAlpha(0f);
        mShadow.animate().alpha(1f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mShadow.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        }).start();
    }

    private void hideOverlay() {
        mProgressBar2.setVisibility(View.GONE);
        mShadow.setAlpha(1f);
        mShadow.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShadow.setVisibility(View.GONE);
            }
        }).start();

    }

    private String getCurrentUrl() {
        return mPagerAdapter.getCurrentUrl(mPager.getCurrentItem());
    }

    private int getCurrentId() {
        return mPagerAdapter.getCurrentId(mPager.getCurrentItem());
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        private PictureHelper mPictureHelper;

        public PagerAdapter(FragmentManager fm, PictureHelper ph) {
            super(fm);
            this.mPictureHelper = ph;
        }

        public int getCurrentId(int position) {
            return mPictureHelper.getPictureId(position);
        }

        public String getCurrentUrl(int position) {
            return mPictureHelper.getFullResFromId(mPictureHelper.getPictureId(position));
        }

        @Override
        public Fragment getItem(int position) {
            int id = mPictureHelper.getPictureId(position);
            return ImageFragment.getInstance(id);
        }

        public PictureHelper getPictureHelper() {
            return mPictureHelper;
        }

        @Override
        public int getCount() {
            return mPictureHelper.getSize();
        }
    }

}
