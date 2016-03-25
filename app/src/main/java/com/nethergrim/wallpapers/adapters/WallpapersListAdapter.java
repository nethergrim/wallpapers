package com.nethergrim.wallpapers.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Query;
import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.adapters.viewholders.WallpapersListViewHolder;
import com.nethergrim.wallpapers.images.IL;
import com.nethergrim.wallpapers.model.Rating;
import com.nethergrim.wallpapers.util.PrefetchScrollListener;

import javax.inject.Inject;

/**
 * Created by andrej on 01.01.16.
 */
public class WallpapersListAdapter extends FirebaseAdapter<WallpapersListViewHolder>
        implements PrefetchScrollListener.PrefetchListener {


    public static final String PREVIEW_URL = "https://www.gstatic.com/prettyearth/assets/preview/";
    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";
    private static final String TAG = "WallpapersListAdapter";
    @Inject
    IL mIL;
    private OnDataLoaderListener mCallback;
    private boolean mCallbackIsFired;


    public interface OnDataLoaderListener {

        void onDataLoaded();
    }

    public WallpapersListAdapter(Query mRef,
            Activity activity,
            @NonNull OnDataLoaderListener callback) {
        super(mRef, activity);
        this.mCallback = callback;
        App.getApp().getMainComponent().inject(this);
    }

    @Override
    public WallpapersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.vh_wallpaper, parent, false);
        return new WallpapersListViewHolder(v);
    }

    @Override
    public void prefetchItemAtPosition(int position) {
        try {
            mIL.cacheImage(PREVIEW_URL + getData(position).getId() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void populateView(WallpapersListViewHolder holder, Rating data, int i) {
        if (!mCallbackIsFired) {
            mCallbackIsFired = true;
            mCallback.onDataLoaded();
        }
        String url = PREVIEW_URL + data.getId() + ".jpg";
        mIL.displayImage(url, holder.mDraweeView, Bitmap.Config.RGB_565);
    }

}
