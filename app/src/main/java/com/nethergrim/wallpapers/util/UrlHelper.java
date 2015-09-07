package com.nethergrim.wallpapers.util;

import android.util.Log;

import com.nethergrim.wallpapers.App;
import com.nethergrim.wallpapers.Picture;
import com.nethergrim.wallpapers.images.ImageLoader;
import com.nethergrim.wallpapers.storage.Prefs;

import org.json.JSONArray;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class UrlHelper {

    public static final String PREVIEW_URL = "https://www.gstatic.com/prettyearth/assets/preview/";
    public static final String FULL_URL = "https://www.gstatic.com/prettyearth/assets/full/";
    public static final String TAG = "UrlHelper";

    @Inject
    Prefs mPrefs;

    @Inject
    ImageLoader mImageLoader;

    public UrlHelper() {
        App.getApp().getMainComponent().inject(this);
    }

    public void persistToFIle() {
        Realm realm = Realm.getInstance(App.getApp());
        RealmResults<Picture> pictures = realm.where(Picture.class).findAll();
        JSONArray jsonArray = new JSONArray();
        for (Picture picture : pictures) {
            jsonArray.put(picture.getId());
        }
        Log.e("TAG","saving json");
        FileUtils.writeStringAsFile(jsonArray.toString(), FileUtils.JSON_FILE_NAME);
        Log.e("TAG","json saved");
    }

    public void getAndPersistAllAccessibleUrls() {
        int min = -2000;
        int max = 0;

        Scheduler scheduler = Schedulers.newThread();

        Realm tmp = Realm.getInstance(App.getApp());
        int size = tmp.where(Picture.class).findAll().size();
        Log.e("TAG", "size: " + size);
        for (int i = min; i < max; i++) {
            String url = PREVIEW_URL + i + ".jpg";
            final int finalI = i;
            mImageLoader.isImageAccessible(url)
                    .observeOn(scheduler)
                    .subscribeOn(scheduler)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            Realm realm = Realm.getInstance(App.getApp());
                            realm.beginTransaction();
                            Picture picture = new Picture();
                            picture.setId(finalI);
                            realm.copyToRealmOrUpdate(picture);
                            realm.commitTransaction();
                            realm.close();
                        }

                    }, throwable -> {
                        Log.e(TAG, throwable.getMessage());
                    });

        }
    }

}
