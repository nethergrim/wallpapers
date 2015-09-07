package com.nethergrim.wallpapers;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class Picture extends RealmObject {

    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
