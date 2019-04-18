package com.dgimatov.flickrsearchdemo.search.model;

import android.graphics.Bitmap;

/**
 * Loads image by given id
 */
public interface ImageLoader {

    /**
     * Loads image with given id and subscribes to a result with given listener
     *
     * @param id       id of the image
     * @param callback callback to return a result
     */
    void subscribe(String id, Listener<Bitmap> callback);

    /**
     * Unsubscribes from result for given id
     *
     * @param id id of the image
     */
    void unsubscribe(String id);
}
