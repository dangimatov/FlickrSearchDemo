package com.dgimatov.flickrsearchdemo.search.view;

/**
 * Interface of a general presenter
 *
 * @param <View> view which presenter presents
 */
public interface Presenter<View> {

    /**
     * Callback from a view that it's shown/will be shown
     *
     * @param view view itself
     */
    void onStart(View view);

    /**
     * Callback from a view that it's hidden/will be hidden
     */
    void onStop();
}
