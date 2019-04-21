package com.dgimatov.flickrsearchdemo.search.view;

/**
 * Presenter interface for all the actions that might happen on a view and potentially change it's state
 */
public interface ImagesSearchActionsPresenter {

    /**
     * User requests to load a new page
     */
    void nextPage();

    /**
     * User requests to make a search for a new text query
     *
     * @param text query
     */
    void newSearch(String text);
}
