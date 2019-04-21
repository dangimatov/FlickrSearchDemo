package com.dgimatov.flickrsearchdemo.search.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImageUrlTest {

    @Test
    public void getUrl() {
        ImageUrl testee = new ImageUrl("http://baseUrl.com/image", ".jpg");
        testee.getUrl();
        assertEquals("http://baseUrl.com/image_m.jpg", testee.getUrl());
    }
}