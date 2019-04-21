package com.dgimatov.flickrsearchdemo.search.model;

/**
 * Single image url
 */
public class ImageUrl {
    private final String baseUrl;
    private final String extensionPostfix;
    private final StringBuilder stringBuilder = new StringBuilder();

    public ImageUrl(String baseUrl, String extensionPostfix) {
        this.baseUrl = baseUrl;
        this.extensionPostfix = extensionPostfix;
    }

    /**
     * @return url-string of a thumbnail size image
     */
    public String getUrl() {
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(baseUrl);
        stringBuilder.append(SIZE_SUFFIX);
        stringBuilder.append(extensionPostfix);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageUrl imageUrl = (ImageUrl) o;

        if (baseUrl != null ? !baseUrl.equals(imageUrl.baseUrl) : imageUrl.baseUrl != null)
            return false;
        return extensionPostfix != null ? extensionPostfix.equals(imageUrl.extensionPostfix) : imageUrl.extensionPostfix == null;
    }

    @Override
    public int hashCode() {
        int result = baseUrl != null ? baseUrl.hashCode() : 0;
        result = 31 * result + (extensionPostfix != null ? extensionPostfix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImageUrl{" +
                "url='" + getUrl() + '}';
    }

    private static final String SIZE_SUFFIX = "_m";

}
