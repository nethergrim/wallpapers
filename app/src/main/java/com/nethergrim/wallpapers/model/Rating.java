package com.nethergrim.wallpapers.model;

/**
 * Created by andrej on 01.01.16.
 */
public class Rating {

    private int mRating;
    private String mId;

    public Rating(String id, int rating) {
        mId = id;
        mRating = rating;
    }

    public Rating() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Rating rating = (Rating) o;

        return mId != null ? mId.equals(rating.mId) : rating.mId == null;

    }

    @Override
    public int hashCode() {
        return mId != null ? mId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "mRating=" + mRating +
                ", mId='" + mId + '\'' +
                '}';
    }
}
