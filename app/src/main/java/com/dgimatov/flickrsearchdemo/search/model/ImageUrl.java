package com.dgimatov.flickrsearchdemo.search.model;

/**
 * Single image url
 */
public class ImageUrl {
    private final String url;

    public ImageUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageUrl imageUrl = (ImageUrl) o;

        return url != null ? url.equals(imageUrl.url) : imageUrl.url == null;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ImageUrl{" +
                "url='" + url + '\'' +
                '}';
    }
}
