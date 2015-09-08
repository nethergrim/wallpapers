package com.nethergrim.wallpapers.util;

import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Andrew Drobyazko (andrey.drobyazko@applikeysolutions.com) on 08.09.15.
 */
public class PictureHelper {

    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";

    private List<Integer> mIdsList;
    private int mSize;
    private SparseIntArray mSparseIntArray = new SparseIntArray(100);
    private Random mRandom;

    public PictureHelper(JSONArray jsonArray) {
        mIdsList = new ArrayList<>(jsonArray.length());
        mSize = jsonArray.length();
        mRandom = new Random();
        for (int i = 0; i < mSize; i++) {
            try {
                mIdsList.add(jsonArray.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(mIdsList);
    }

    public int getSize() {
        return mSize;
    }

    public int getPictureId(int position) {
        int id = mSparseIntArray.get(position);
        if (id == 0) {
            id = mIdsList.get(mRandom.nextInt(mSize));
            mSparseIntArray.put(position, id);
        }
        return id;
    }


    public String getFullResFromId(int id) {
        return FULL_URL + id + ".jpg";
    }

}
