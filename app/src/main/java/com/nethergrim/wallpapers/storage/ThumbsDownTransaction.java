package com.nethergrim.wallpapers.storage;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.yandex.metrica.YandexMetrica;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 26.09.15.
 */
public class ThumbsDownTransaction implements Transaction.Handler {

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        try {
            if (mutableData.getValue() == null) {
                mutableData.setValue(-1);
            } else {
                Integer rating = mutableData.getValue(Integer.class);
                mutableData.setValue(rating - 1);
            }

            return Transaction.success(mutableData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Transaction.abort();
    }

    @Override
    public void onComplete(FirebaseError firebaseError,
            boolean b,
            DataSnapshot dataSnapshot) {
        if (firebaseError != null) {
            YandexMetrica.reportEvent(
                    firebaseError.getMessage() + " " + firebaseError.getDetails());
        }

    }
}
