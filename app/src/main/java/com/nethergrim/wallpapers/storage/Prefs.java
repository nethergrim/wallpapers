package com.nethergrim.wallpapers.storage;

import java.util.List;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public interface Prefs {

    void addUrl(String url);
    List<String> getAllUrls();
    void persistList(List<String> data);
    void setAutoRefresh(boolean autoRefresh);
    boolean isAutoRefreshEnabled();

}
