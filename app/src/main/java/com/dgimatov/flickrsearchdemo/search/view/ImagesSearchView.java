package com.dgimatov.flickrsearchdemo.search.view;

/**
 * Interface which view should implement to be able to communicate with Presenter
 */
public interface ImagesSearchView {

    /**
     * Single point of updating ui on a view using given state with all needed info
     *
     * @param state one of possibly many ui states
     */
    void updateState(ImagesSearchViewState state);

}
