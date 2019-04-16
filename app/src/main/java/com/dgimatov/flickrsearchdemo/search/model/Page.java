package com.dgimatov.flickrsearchdemo.search.model;

import java.util.List;

/**
 * Single page of images
 */
public class Page {
    private final int page;
    private final int pages;
    private final List<ImageUrl> imageUrls;

    public Page(int page, int pages, List<ImageUrl> imageUrls) {
        this.page = page;
        this.pages = pages;
        this.imageUrls = imageUrls;
    }

    public int getTotalPages() {
        return pages;
    }

    public int getCurrentPage() {
        return page;
    }

    /**
     * @return list of {@link ImageUrl}
     */
    public List<ImageUrl> getImageUrls() {
        return imageUrls;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page1 = (Page) o;

        if (page != page1.page) return false;
        if (pages != page1.pages) return false;
        return imageUrls != null ? imageUrls.equals(page1.imageUrls) : page1.imageUrls == null;
    }

    @Override
    public int hashCode() {
        int result = page;
        result = 31 * result + pages;
        result = 31 * result + (imageUrls != null ? imageUrls.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImageUrlsPage{" +
                "page=" + page +
                ", pages=" + pages +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
