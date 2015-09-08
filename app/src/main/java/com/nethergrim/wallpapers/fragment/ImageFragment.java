package com.nethergrim.wallpapers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nethergrim.wallpapers.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 07.09.15.
 */
public class ImageFragment extends BaseFragment {

    public static final String KEY_ID = "id";
    public static final String PREVIEW_URL = "https://www.gstatic.com/prettyearth/assets/preview/";
    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";


    @InjectView(R.id.image)
    ImageView mImage;
    private int mId;

    public static ImageFragment getInstance(int id) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, id);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mId = getArguments().getInt(KEY_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageLoader.displayImage(FULL_URL + mId + ".jpg", mImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}