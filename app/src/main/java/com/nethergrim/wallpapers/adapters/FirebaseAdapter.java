package com.nethergrim.wallpapers.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.nethergrim.wallpapers.model.Rating;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrej on 01.01.16.
 */
public abstract class FirebaseAdapter<F extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<F> {

    protected LayoutInflater mInflater;
    private Query mRef;
    private List<Rating> mModels;
    private List<String> mKeys;
    private ChildEventListener mListener;

    public FirebaseAdapter(Query mRef, Activity activity) {
        this.mRef = mRef;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<>();
        mKeys = new ArrayList<>();
        // Look for all child events. We will then map them to our own internal ArrayList, which
        // backs ListView
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Rating rating = getRating(dataSnapshot);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    mModels.add(0, rating);
                    mKeys.add(0, rating.getId());
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(rating);
                        mKeys.add(rating.getId());
                    } else {
                        mModels.add(nextIndex, rating);
                        mKeys.add(nextIndex, rating.getId());
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // One of the mModels changed. Replace it in our list and name mapping
                Rating rating = getRating(dataSnapshot);
                int index = mKeys.indexOf(rating.getId());
                if (index == -1){
                    return;
                }
                mModels.set(index, rating);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                Rating rating = getRating(dataSnapshot);

                int index = mKeys.indexOf(rating.getId());

                mKeys.remove(index);
                mModels.remove(index);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                Rating rating = getRating(dataSnapshot);

                int index = mKeys.indexOf(rating.getId());
                if (index == -1){
                    return;
                }
                mModels.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, rating);
                    mKeys.add(0, rating.getId());
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(rating);
                        mKeys.add(rating.getId());
                    } else {
                        mModels.add(nextIndex, rating);
                        mKeys.add(nextIndex, rating.getId());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    @Override
    public void onBindViewHolder(F holder, int i) {
        Rating rating = mModels.get(i);
        populateView(holder, rating, i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }

    public Rating getData(int i) {
        return mModels.get(i);
    }

    protected abstract void populateView(F holder, Rating data, int position);

    private Rating getRating(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        Integer intValue = dataSnapshot.getValue(Integer.class);
        return new Rating(key, intValue);
    }

}
