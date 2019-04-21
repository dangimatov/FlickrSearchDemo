package com.dgimatov.flickrsearchdemo.search.view;


import com.dgimatov.flickrsearchdemo.search.model.ImageUrl;

import java.util.List;

/**
 * All possible states which {@link ImagesSearchView} can be in.
 */
public class ImagesSearchViewState {

    /**
     * {@link ImageUrl}s are loading
     */
    public static final class Loading extends ImagesSearchViewState {
        public final static Loading INSTANCE = new Loading();

        private Loading() {
        }
    }

    /**
     * {@link ImageUrl}s have been loaded successfully. Show them
     */
    public static final class ShowImages extends ImagesSearchViewState {
        final List<ImageUrl> imageUrls;
        final int updateFromIndex;

        public ShowImages(List<ImageUrl> imageUrls, int updateFromIndex) {
            this.imageUrls = imageUrls;
            this.updateFromIndex = updateFromIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ShowImages that = (ShowImages) o;

            return imageUrls != null ? imageUrls.equals(that.imageUrls) : that.imageUrls == null;
        }

        @Override
        public int hashCode() {
            return imageUrls != null ? imageUrls.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "ShowImages{" +
                    "imageUrls=" + imageUrls.size() +
                    ", updateFromIndex=" + updateFromIndex +
                    '}';
        }
    }

    /**
     * It's the last page for given search query. It's not possible to scroll further
     */
    public static final class LastPage extends ImagesSearchViewState {
        public final static LastPage INSTANCE = new LastPage();

        private LastPage() {
        }
    }

    /**
     * Some error happened during the loading process
     */
    public static final class Error extends ImagesSearchViewState {
        public final Throwable exception;

        public Error(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Error error = (Error) o;

            return exception != null ? exception.equals(error.exception) : error.exception == null;
        }

        @Override
        public int hashCode() {
            return exception != null ? exception.hashCode() : 0;
        }
    }

}
