package com.dgimatov.flickrsearchdemo.di;

import com.dgimatov.flickrsearchdemo.search.model.FlickrSearchApiClient;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.PageDeserializer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides dependencies
 */
public class DependenciesProvider {

    private PageDeserializer pageDeserializer() {
        return new PageDeserializer();
    }

    private ThreadPoolExecutor singleThreadPoolExecutor() {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public ImagesSearchRepository imagesSearchRepository() {
        return new FlickrSearchApiClient(
                singleThreadPoolExecutor(), pageDeserializer());
    }
}
