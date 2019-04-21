package com.dgimatov.flickrsearchdemo.search.model;

import java.util.List;

/**
 * Single page of images
 */
public class Page {
    private final int currentPage;
    private final int totalPages;
    private final List<ImageUrl> imageUrls;

    public Page(int currentPage, int totalPages, List<ImageUrl> imageUrls) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.imageUrls = imageUrls;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public String toString() {
        return "Page{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", imageUrls=" + imageUrls +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (currentPage != page.currentPage) return false;
        if (totalPages != page.totalPages) return false;
        return imageUrls != null ? imageUrls.equals(page.imageUrls) : page.imageUrls == null;
    }

    @Override
    public int hashCode() {
        int result = currentPage;
        result = 31 * result + totalPages;
        result = 31 * result + (imageUrls != null ? imageUrls.hashCode() : 0);
        return result;
    }

    /**
     * @return list of {@link ImageUrl}
     */
    public List<ImageUrl> getImageUrls() {
        return imageUrls;
    }


}
