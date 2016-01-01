package com.nethergrim.wallpapers.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nethergrim.wallpapers.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andrej on 01.01.16.
 */
public class WallpapersListViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.text)
    public TextView mTextView;
    @InjectView(R.id.image)
    public SimpleDraweeView mDraweeView;

    public WallpapersListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }
}
