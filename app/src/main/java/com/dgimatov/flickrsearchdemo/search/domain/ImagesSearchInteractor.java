package com.dgimatov.flickrsearchdemo.search.domain;

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

import static com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState.*;

/**
 * Interactor for {@link com.dgimatov.flickrsearchdemo.search.view.ImagesSearchView} which is
 * responsible for determining view's state at any given moment as long as view is visible
 */
public class ImagesSearchInteractor implements ImagesSearchActionsPresenter, Presenter<ImagesSearchView> {

    private final ImagesSearchRepository imagesSearchRepository;

    private int currentPage = 1;
    private String currentQuery = "";
    private boolean canLoadMore = false;
    private List<ImageUrl> currentUrls = new ArrayList<>();

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
        currentPage = 1;

        if (!currentUrls.isEmpty()) {
            currentUrls.clear();
            pushStateToView(new ShowImages(Collections.emptyList()));
        }

        if (text.isEmpty() || text.equals(currentQuery)) {
            imagesSearchRepository.unsubscribeAll();
            return;
        }

        currentQuery = text;
        pushStateToView(Loading.INSTANCE);

        imagesSearchRepository.subscribe(text, 1, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                currentUrls.addAll(value.getImageUrls());
                pushStateToView(new ShowImages(new ArrayList<>(currentUrls)));
                canLoadMore = value.getTotalPages() > 1;
            }

            @Override
            public void onError(Throwable e) {
                pushStateToView(new ImagesSearchViewState.Error(e));
            }
        });
    }

    @Override
    public void nextPage() {
        if (!canLoadMore) {
            pushStateToView(LastPage.INSTANCE);
            return;
        }

        pushStateToView(Loading.INSTANCE);

        imagesSearchRepository.subscribe(currentQuery, ++currentPage, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                currentUrls.addAll(value.getImageUrls());
                pushStateToView(new ShowImages(new ArrayList<>(currentUrls)));
                canLoadMore = value.getTotalPages() > currentPage;
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
