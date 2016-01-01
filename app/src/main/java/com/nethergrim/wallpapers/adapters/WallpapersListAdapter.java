package com.nethergrim.wallpapers.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Query;
import com.nethergrim.wallpapers.BuildConfig;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.adapters.viewholders.WallpapersListViewHolder;
import com.nethergrim.wallpapers.model.Rating;

/**
 * Created by andrej on 01.01.16.
 */
public class WallpapersListAdapter extends FirebaseAdapter<WallpapersListViewHolder> {


    public static final String PREVIEW_URL = "https://www.gstatic.com/prettyearth/assets/preview/";
    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";
    private static final String TAG = "WallpapersListAdapter";
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
    }

    @Override
    public WallpapersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.vh_wallpaper, parent, false);
        return new WallpapersListViewHolder(v);
    }

    @Override
    protected void populateView(WallpapersListViewHolder holder, Rating data, int i) {
        if (!mCallbackIsFired) {
            mCallbackIsFired = true;
            mCallback.onDataLoaded();
        }
        if (BuildConfig.DEBUG) {
            holder.mTextView.setText(String.valueOf(data.getRating()));
        }
        String url = PREVIEW_URL + data.getId() + ".jpg";
        holder.mDraweeView.setImageURI(Uri.parse(url));
    }
}
