package com.dgimatov.flickrsearchdemo.search.view;

import android.os.Handler;
import android.os.Looper;

import com.dgimatov.flickrsearchdemo.search.domain.ImagesSearchInteractor;
import com.dgimatov.flickrsearchdemo.search.model.Listener;

/**
 * Presenter for {@link ImagesSearchView}
 */
public class ImagesSearchPresenter implements Presenter<ImagesSearchView>, ImagesSearchActionsPresenter {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final ImagesSearchInteractor imagesSearchInteractor;

    public ImagesSearchPresenter(ImagesSearchInteractor imagesSearchInteractor) {
        this.imagesSearchInteractor = imagesSearchInteractor;
    }

    @Override
    public void onStart(ImagesSearchView view) {
        imagesSearchInteractor.subscribeToState(new Listener<ImagesSearchViewState>() {
            @Override
            public void onNext(ImagesSearchViewState value) {
                uiHandler.post(() -> view.updateState(value));
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Override
    public void onStop() {
        imagesSearchInteractor.unsubscribeFromState();
    }

    @Override
    public void nextPage() {
        imagesSearchInteractor.nextPage();
    }

    @Override
    public void newSearch(String text) {
        imagesSearchInteractor.newSearch(text);
    }
}
