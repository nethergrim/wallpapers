package com.nethergrim.wallpapers.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.nethergrim.wallpapers.R;
import com.nethergrim.wallpapers.fragment.ImageFragment;
import com.nethergrim.wallpapers.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.pager)
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Log.e("TAG", "fetching");
        JSONArray jsonArray = FileUtils.getJSONArrayFromAssets(FileUtils.JSON_FILE_NAME);
        Log.e("TAG", "fetched");
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), jsonArray);
        mPager.setAdapter(pagerAdapter);
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        JSONArray mJSONArray;

        public PagerAdapter(FragmentManager fm, JSONArray jsonArray) {
            super(fm);
            this.mJSONArray = jsonArray;
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return ImageFragment.getInstance(mJSONArray.getInt(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getCount() {
            return mJSONArray.length();
        }
    }

}
