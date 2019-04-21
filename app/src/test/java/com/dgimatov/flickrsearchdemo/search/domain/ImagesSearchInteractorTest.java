package com.dgimatov.flickrsearchdemo.search.domain;

import com.dgimatov.flickrsearchdemo.search.model.ImageUrl;
import com.dgimatov.flickrsearchdemo.search.model.ImagesSearchRepository;
import com.dgimatov.flickrsearchdemo.search.model.Listener;
import com.dgimatov.flickrsearchdemo.search.model.Page;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchView;
import com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImagesSearchInteractorTest {

    private final Page expectedPage = new Page(1, 2, Arrays.asList(
            new ImageUrl("https://farm2.staticflickr.com/1801/43052905061_ae07ea2755", ".jpg"),
            new ImageUrl("https://farm2.staticflickr.com/1638/26405063166_085f53c4d0", ".jpg")));
    private final Page expectedPage2 = new Page(2, 2, Arrays.asList(
            new ImageUrl("https://farm3.staticflickr.com/1801/43052905061_ae07ea2755", ".jpg"),
            new ImageUrl("https://farm3.staticflickr.com/1638/26405063166_085f53c4d0", ".jpg")));

    @Mock
    private ImagesSearchRepository imagesSearchRepository;

    @Mock
    private ImagesSearchView view;

    @InjectMocks
    private ImagesSearchInteractor testee;

    @Test
    public void newSearch_HappyCase() {
        //Given
        doAnswer(invocation -> {
            String requestParamText = invocation.getArgument(0);
            assertEquals("toyota", requestParamText);
            int requestParamPage = invocation.getArgument(1);
            assertEquals(1, requestParamPage);
            Listener<Page> listener = invocation.getArgument(2);
            listener.onNext(expectedPage);
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));

    }

    @Test
    public void newSearch_NoViewCallsAfterUnsubscribe() {
        //Given
        doAnswer(invocation -> {
            Listener<Page> listener = invocation.getArgument(2);
            listener.onNext(expectedPage);
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");
        testee.onStop();
        //In reality should not happen
        testee.newSearch("toyota");

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));

    }

    @Test
    public void newSearch_EmptySearchText() {
        //Given
        doAnswer(invocation -> {
            Listener<Page> listener = invocation.getArgument(2);
            listener.onNext(expectedPage);
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");
        testee.newSearch("");

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(Collections.emptyList(), 0));

    }

    @Test
    public void newSearch_ErrorOccurred() {
        //Given
        Throwable error = new JSONException("");
        doAnswer(invocation -> {
            Listener<Page> listener = invocation.getArgument(2);
            listener.onError(error);
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.Error(error));
    }

    @Test
    public void nextPage_HappyCase() {
        //Given
        List<ImageUrl> expectedCombinedList = new ArrayList<>();
        expectedCombinedList.addAll(expectedPage.getImageUrls());
        expectedCombinedList.addAll(expectedPage2.getImageUrls());

        doAnswer(invocation -> {
            String requestParamText = invocation.getArgument(0);
            int requestParamPage = invocation.getArgument(1);
            Listener<Page> listener = invocation.getArgument(2);
            if (requestParamPage == 1) {
                listener.onNext(expectedPage);
            }
            if (requestParamPage == 2) {
                listener.onNext(expectedPage2);
                assertEquals("toyota", requestParamText);
                assertEquals(2, requestParamPage);
            }
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");
        testee.nextPage();

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedCombinedList, 2));
    }

    @Test
    public void nextPage_NotPossible_LastPage() {
        //Given
        final Page expectedOnlyPage = new Page(1, 1, Arrays.asList(
                new ImageUrl("https://farm2.staticflickr.com/1801/43052905061_ae07ea2755", ".jpg"),
                new ImageUrl("https://farm2.staticflickr.com/1638/26405063166_085f53c4d0", ".jpg")));

        doAnswer(invocation -> {
            Listener<Page> listener = invocation.getArgument(2);
            listener.onNext(expectedOnlyPage);
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");
        testee.nextPage();

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));
        inOrder.verify(view).updateState(ImagesSearchViewState.LastPage.INSTANCE);
    }

    @Test
    public void nextPageErrorOccurred() {
        //Given
        Throwable error = new JSONException("");
        doAnswer(invocation -> {
            int requestParamPage = invocation.getArgument(1);
            Listener<Page> listener = invocation.getArgument(2);
            if (requestParamPage == 1) {
                listener.onNext(expectedPage);
            }
            if (requestParamPage == 2) {
                listener.onError(error);
            }
            return null;
        }).when(imagesSearchRepository).subscribe(anyString(), anyInt(), ArgumentMatchers.<Listener<Page>>any());

        //When
        testee.onStart(view);
        testee.newSearch("toyota");
        testee.nextPage();

        //Then
        InOrder inOrder = inOrder(view);
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.ShowImages(expectedPage.getImageUrls(), 0));
        inOrder.verify(view).updateState(ImagesSearchViewState.Loading.INSTANCE);
        inOrder.verify(view).updateState(new ImagesSearchViewState.Error(error));
    }

}