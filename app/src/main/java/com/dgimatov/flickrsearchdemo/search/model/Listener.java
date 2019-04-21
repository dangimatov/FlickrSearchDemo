package com.dgimatov.flickrsearchdemo.search.model;

/**
 * General listener interface
 */
public interface Listener<T> {

    /**
     * Callback on successful result
     *
     * @param value successfully returned value
     */
    void onNext(T value);

    /**
     * Callback on error occurrence
     *
     * @param e occurred error
     */
    void onError(Throwable e);
}
