package com.dgimatov.flickrsearchdemo.search.domain;

import com.dgimatov.flickrsearchdemo.search.model.ImageUrl;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.Listener;
import com.dgimatov.flickrsearchdemo.search.model.Page;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchActionsPresenter;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState.*;

/**
 * Interactor for {@link com.dgimatov.flickrsearchdemo.search.view.ImagesSearchView} which is
 * responsible for determining view's state at any given moment as long as there are listeners for this state
 */
public class ImagesSearchInteractor implements ImagesSearchActionsPresenter {

    private final ImagesSearchRepository imagesSearchRepository;

    private final Set<Listener<ImagesSearchViewState>> listeners = new HashSet<>();

    private int currentPage = 1;
    private String currentQuery = "";
    private boolean canLoadMore = false;
    private List<ImageUrl> currentUrls = new ArrayList<>();

    public ImagesSearchInteractor(ImagesSearchRepository imagesSearchRepository) {
        this.imagesSearchRepository = imagesSearchRepository;
    }

    /**
     * Subscribes to view's state
     *
     * @param listener listener to get state through
     */
    public void subscribeToState(Listener<ImagesSearchViewState> listener) {
        listeners.add(listener);
    }

    /**
     * Unsubscribes from a state when managing a view is not needed anymore
     */
    public void unsubscribeFromState() {
        listeners.clear();
        imagesSearchRepository.unsubscribeAll();
    }

    private void pushStateToListeners(ImagesSearchViewState state) {
        if (!listeners.isEmpty()) {
            for (Listener<ImagesSearchViewState> listener : listeners) {
                listener.onNext(state);
            }
        }
    }

    @Override
    public void newSearch(String text) {
        currentPage = 1;

        if (!currentUrls.isEmpty()) {
            currentUrls.clear();
            pushStateToListeners(new ShowImages(Collections.emptyList()));
        }

        if (text.isEmpty() || text.equals(currentQuery)) {
            imagesSearchRepository.unsubscribeAll();
            return;
        }

        currentQuery = text;
        pushStateToListeners(Loading.INSTANCE);

        imagesSearchRepository.subscribe(text, 1, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                currentUrls.addAll(value.getImageUrls());
                pushStateToListeners(new ShowImages(new ArrayList<>(currentUrls)));
                canLoadMore = value.getTotalPages() > 1;
            }

            @Override
            public void onError(Throwable e) {
                pushStateToListeners(new ImagesSearchViewState.Error(e));
            }
        });
    }

    @Override
    public void nextPage() {
        if (!canLoadMore) {
            pushStateToListeners(LastPage.INSTANCE);
            return;
        }

        pushStateToListeners(Loading.INSTANCE);

        imagesSearchRepository.subscribe(currentQuery, ++currentPage, new Listener<Page>() {
            @Override
            public void onNext(Page value) {
                currentUrls.addAll(value.getImageUrls());
                pushStateToListeners(new ShowImages(new ArrayList<>(currentUrls)));
                canLoadMore = value.getTotalPages() > currentPage;
            }

            @Override
            public void onError(Throwable e) {
                pushStateToListeners(new ImagesSearchViewState.Error(e));
            }
        });
    }
}
