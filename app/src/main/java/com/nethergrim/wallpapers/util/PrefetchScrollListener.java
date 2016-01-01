package com.nethergrim.wallpapers.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by andrej on 02.01.16.
 */
public class PrefetchScrollListener extends RecyclerView.OnScrollListener {


    private LinearLayoutManager mLinearLayoutManager;
    private PrefetchListener mCallback;

    private int maxDeliveredItem;
    private int firstVisible;
    private int visibleItemCount;

    public interface PrefetchListener {

        void prefetchItemAtPosition(int position);
    }

    public PrefetchScrollListener(LinearLayoutManager linearLayoutManager,
            PrefetchListener callback) {
        mLinearLayoutManager = linearLayoutManager;
        mCallback = callback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {

            visibleItemCount = mLinearLayoutManager.getChildCount();
            firstVisible = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisible > maxDeliveredItem) {
                maxDeliveredItem = firstVisible;
                mCallback.prefetchItemAtPosition(firstVisible + visibleItemCount - 2);
            }
        }
    }
}
