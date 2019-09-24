package com.dgimatov.flickrsearchdemo.search.domain;

import android.util.Log;

import com.dgimatov.flickrsearchdemo.search.model.ImageUrl;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.Listener;
import com.dgimatov.flickrsearchdemo.search.model.Page;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchActionsPresenter;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchView;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState;
import com.dgimatov.flickrsearchdemo.search.view.Presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState.*;

/**
 * Interactor for {@link com.dgimatov.flickrsearchdemo.search.view.ImagesSearchView} which is
 * responsible for determining view's state at any given moment as long as view is visible
 */
public class ImagesSearchInteractor implements ImagesSearchActionsPresenter, Presenter<ImagesSearchView> {

    private final ImagesSearchRepository imagesSearchRepository;

    private final AtomicInteger currentPage = new AtomicInteger(1);
    private String currentQuery = "";
    private AtomicBoolean canLoadMore = new AtomicBoolean(false);
    private final List<ImageUrl> currentUrls = new ArrayList<>();

    private ImagesSearchView view;

    public ImagesSearchInteractor(ImagesSearchRepository imagesSearchRepository) {
        this.imagesSearchRepository = imagesSearchRepository;
    }

    private void pushStateToView(ImagesSearchViewState state) {
        if (view != null) {
            view.updateState(state);
        }
    }

    @Override
    public void newSearch(String text) {
        currentPage.set(1);
        currentUrls.clear();
        currentQuery = text;
        pushStateToView(new ShowImages(Collections.emptyList(), 0));

        if (text.isEmpty()) {
            imagesSearchRepository.unsubscribeAll();
            return;
        }

        pushStateToView(Loading.INSTANCE);
        imagesSearchRepository.subscribe(text, 1, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                synchronized (currentUrls) {
                    Log.i("test_", "page: " + value);
                    currentUrls.addAll(value.getImageUrls());
                    pushStateToView(new ShowImages(new ArrayList<>(currentUrls), 0));
                }
                canLoadMore.set(value.getTotalPages() > 1);
            }

            @Override
            public void onError(Throwable e) {
                pushStateToView(new ImagesSearchViewState.Error(e));
            }
        });
    }

    @Override
    public void nextPage() {
        if (!canLoadMore.get()) {
            pushStateToView(LastPage.INSTANCE);
            return;
        }

        pushStateToView(Loading.INSTANCE);

        imagesSearchRepository.subscribe(currentQuery, currentPage.get() + 1, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                synchronized (currentUrls) {
                    currentPage.set(value.getCurrentPage());
                    int updateFromIndex = currentUrls.size();
                    currentUrls.addAll(value.getImageUrls());
                    pushStateToView(new ShowImages(new ArrayList<>(currentUrls), updateFromIndex));
                }
                canLoadMore.set(value.getTotalPages() > currentPage.get());
            }

            @Override
            public void onError(Throwable e) {
                pushStateToView(new ImagesSearchViewState.Error(e));
            }
        });
    }

    @Override
    public void onStart(ImagesSearchView imagesSearchView) {
        this.view = imagesSearchView;
    }

    @Override
    public void onStop() {
        this.view = null;
        imagesSearchRepository.unsubscribeAll();
    }
}
