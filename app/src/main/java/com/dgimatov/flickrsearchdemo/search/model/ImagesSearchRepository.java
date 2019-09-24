package com.dgimatov.flickrsearchdemo.search.model;


/**
 * Repository which gives us list of {@link ImageUrl} and some metadata we can use
 */
public interface ImagesSearchRepository {

    /**
     * Makes a request for given params and returns a result throw given callback
     *
     * @param text     search text
     * @param page     page
     * @param listener listener to return result through
     */
    void subscribe(String text, int page, Listener<Page> listener);


    /**
     * Unsubscribe from repository. Meaning we are no longer interested in results and repo should
     * drop anything it's currently busy with and remove references to our listeners
     */
    void unsubscribeAll();
}
