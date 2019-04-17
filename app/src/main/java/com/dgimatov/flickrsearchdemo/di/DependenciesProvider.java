package com.dgimatov.flickrsearchdemo.di;

import com.dgimatov.flickrsearchdemo.search.domain.ImagesSearchInteractor;
import com.dgimatov.flickrsearchdemo.search.model.FlickrSearchApiClient;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.PageDeserializer;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchPresenter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides dependencies
 */
public class DependenciesProvider {

    private static PageDeserializer pageDeserializer() {
        return new PageDeserializer();
    }

    private static ThreadPoolExecutor singleThreadPoolExecutor() {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    private static ImagesSearchRepository imagesSearchRepository() {
        return new FlickrSearchApiClient(
                singleThreadPoolExecutor(), pageDeserializer());
    }

    private static ImagesSearchInteractor imagesSearchInteractor() {
        return new ImagesSearchInteractor(imagesSearchRepository());
    }

    public static ImagesSearchPresenter imagesSearchPresenter() {
        return new ImagesSearchPresenter(imagesSearchInteractor());
    }
}
