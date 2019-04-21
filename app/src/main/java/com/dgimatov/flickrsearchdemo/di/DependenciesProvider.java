package com.dgimatov.flickrsearchdemo.di;

import com.dgimatov.flickrsearchdemo.search.domain.ImagesSearchInteractor;
import com.dgimatov.flickrsearchdemo.search.model.CachingRemoteImageLoader;
import com.dgimatov.flickrsearchdemo.search.model.FlickrSearchApiClient;
import com.dgimatov.flickrsearchdemo.search.model.ImageLoaderRepository;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.PageDeserializer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides dependencies
 */
public final class DependenciesProvider {

    private static final ThreadPoolExecutor singleThreadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    private static final ImagesSearchRepository imagesSearchRepository = new FlickrSearchApiClient(
            singleThreadPoolExecutor, providePageDeserializer());

    private static PageDeserializer providePageDeserializer() {
        return new PageDeserializer();
    }

    private static ImagesSearchRepository provideImagesSearchRepository() {
        return imagesSearchRepository;
    }

    /**
     * @return instance of {@link ImagesSearchInteractor}
     */
    public static ImagesSearchInteractor provideImagesSearchInteractor() {
        return new ImagesSearchInteractor(provideImagesSearchRepository());
    }

    /**
     * @return instance of {@link ImageLoaderRepository}
     */
    public static ImageLoaderRepository provideImagesLoader() {
        return CachingRemoteImageLoader.getInstance();
    }
}
