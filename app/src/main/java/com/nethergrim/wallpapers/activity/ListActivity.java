package com.nethergrim.wallpapers.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.adapters.WallpapersListAdapter;
import com.nethergrim.wallpapers.util.ConnectionUtils;
import com.nethergrim.wallpapers.util.PrefetchScrollListener;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andrej on 01.01.16.
 */
public class ListActivity extends BaseActivity
        implements WallpapersListAdapter.OnDataLoaderListener {

    private static final String TAG = "ListActivity";
    @InjectView(R.id.recycler)
    public RecyclerView mRecyclerView;
    @InjectView(R.id.progressBar)
    public ProgressBar mProgressBar;
    @Inject
    Firebase mFirebase;
    private boolean mLoaded;
    private WallpapersListAdapter mAdapter;

    @Override
    public void onDataLoaded() {
        Log.d(TAG, "onDataLoaded() called with: " + "");
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp().getMainComponent().inject(this);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        observeConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    private void observeConnectivity() {
        ConnectionUtils.isConnected()
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        showToast(R.string.network_error);
                    } else {
                        if (!mLoaded) {
                            mLoaded = true;
                            initList();
                        }
                    }
                });
    }

    private void initList() {
        Query query = mFirebase.child("ratings").orderByValue();
        mAdapter = new WallpapersListAdapter(query, this, this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        PrefetchScrollListener prefetchScrollListener = new PrefetchScrollListener(
                linearLayoutManager, mAdapter);
        mRecyclerView.addOnScrollListener(prefetchScrollListener);
    }
}
