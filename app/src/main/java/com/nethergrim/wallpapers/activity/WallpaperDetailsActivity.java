package com.nethergrim.wallpapers.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.firebase.client.Firebase;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.images.IL;
import com.nethergrim.wallpapers.storage.Prefs;
import com.nethergrim.wallpapers.storage.ThumbsDownTransaction;
import com.nethergrim.wallpapers.storage.ThumbsUpTransaction;
import com.nethergrim.wallpapers.util.AlarmReceiver;
import com.nethergrim.wallpapers.util.FileUtils;
import com.nethergrim.wallpapers.util.PictureHelper;
import com.nethergrim.wallpapers.util.RetryWithDelay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by andrej on 02.01.16.
 */
public class WallpaperDetailsActivity extends BaseActivity {

    public static final String EXTRA_ID = "id";

    @Inject
    IL mImageLoader;
    @Inject
    Tracker mTracker;
    @Inject
    Firebase mFirebase;
    @Inject
    Prefs mPrefs;

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
    @InjectView(R.id.btn_layout)
    LinearLayout mBtnLayout;
    @InjectView(R.id.shadow)
    View mShadow;
    @InjectView(R.id.progressBar2)
    ProgressBar mProgressBar2;
    @InjectView(R.id.imageView)
    SubsamplingScaleImageView mImageView;
    private String id;
    private boolean mVoted;

    public static void start(@NonNull Context context, @NonNull String id) {
        Intent intent = new Intent(context, WallpaperDetailsActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.id = getIntent().getStringExtra(EXTRA_ID);
        setContentView(R.layout.activity_wallpapers_details);
        ButterKnife.inject(this);
        App.getApp().getMainComponent().inject(this);
        getWindow().setBackgroundDrawable(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }
        mImageLoader.getBitMap(getCurrentUrl())
                .subscribeOn(Schedulers.io())
                .map(FileUtils::persistBitmapToDisk)
                .retryWhen(new RetryWithDelay(10, 300))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {
                    mImageView.setImage(ImageSource.uri(uri));
                }, throwable -> {
                    onBackPressed();
                });
    }

    @OnClick(R.id.btn_download)
    void onDownloadClick() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Download")
                .build());
        showOverlay();
        mImageLoader.getBitMap(getCurrentUrl())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(FileUtils::persistBitmapToDownloads)
                .map(file -> "Image was saved at:\n" + file.toString() + "\nEnjoy!")
                .retryWhen(new RetryWithDelay(10, 300))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Toast.makeText(WallpaperDetailsActivity.this, s, Toast.LENGTH_LONG).show(),
                        throwable -> {
                            hideOverlay();
                            Log.e(TAG, "call: ", throwable);
                            showToast(R.string.network_error);
                        }, this::hideOverlay);
    }

    private static final String TAG = "WallpaperDetailsActivity";

    @OnClick(R.id.btn_share)
    void onShareClick() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
        showOverlay();
        mImageLoader.getBitMap(getCurrentUrl())
                .map(FileUtils::persistBitmapToDisk)
                .observeOn(AndroidSchedulers.mainThread())
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
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Set_Wallpaper")
                .build());
        mPrefs.setAutoRefresh(false);
        showOverlay();
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        mImageLoader.getBitMap(getCurrentUrl())
                .subscribeOn(Schedulers.newThread())
                .map(bitmap -> {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    bitmap.recycle();
                    return new ByteArrayInputStream(bitmapdata);
                })
                .doOnNext(inputStream -> {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(
                            App.getApp());
                    try {
                        wallpaperManager.setStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    hideOverlay();
                    showToast(R.string.wallpaper_set);
                }, throwable -> {
                    Log.e("TAG", throwable.toString());
                    hideOverlay();
                });

    }

    @OnClick(R.id.btn_thumps_up)
    void onThumbsUpClick() {
        if (mVoted) {
            return;
        }
        mVoted = true;
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("ThumbsUp")
                .build());
        showToast("+1");

        mFirebase.child("ratings")
                .child(String.valueOf(id))
                .runTransaction(new ThumbsUpTransaction());

    }

    @OnClick(R.id.btn_thumps_down)
    void onThumbsDownClick() {
        if (mVoted) {
            return;
        }
        mVoted = true;
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("ThumbsDown")
                .build());
        showToast("-1");
        mFirebase.child("ratings")
                .child(String.valueOf(id))
                .runTransaction(new ThumbsDownTransaction());
    }

    private String getCurrentUrl() {
        return PictureHelper.FULL_URL + id + ".jpg";
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
}
