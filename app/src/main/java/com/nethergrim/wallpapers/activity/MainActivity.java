package com.nethergrim.wallpapers.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.fragment.ImageFragment;
import com.nethergrim.wallpapers.util.FileUtils;
import com.nethergrim.wallpapers.util.PictureHelper;

import org.json.JSONArray;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

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
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mPager.setOffscreenPageLimit(15);
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
                    mProgressBar.setVisibility(View.GONE);
                });
    }

    @OnClick(R.id.btn_download)
    void onDownloadClick() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(
                Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(mPagerAdapter.getCurrentUrl(mPager.getCurrentItem())));
        request.setTitle(getString(R.string.wallpaper) + " " + mPagerAdapter.getCurrentId(
                mPager.getCurrentItem()));
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    @OnClick(R.id.btn_share)
    void onShareClick() {
    }

    @OnClick(R.id.btn_set_wallpaper)
    void onWallpaperClick() {
    }

    @OnClick(R.id.btn_thumps_up)
    void onThumbsUpClick() {
    }

    @OnClick(R.id.btn_thumps_down)
    void onThumbsDownClick() {
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
