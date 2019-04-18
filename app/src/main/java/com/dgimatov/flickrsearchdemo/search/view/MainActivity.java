package com.dgimatov.flickrsearchdemo.search.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dgimatov.flickrsearchdemo.R;
import com.dgimatov.flickrsearchdemo.di.DependenciesProvider;

import static com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState.*;

public class MainActivity extends AppCompatActivity implements ImagesSearchView {

    private ImagesSearchPresenter presenter;
    private ImagesListAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        presenter = DependenciesProvider.provideImagesSearchPresenter();

        progressBar = findViewById(R.id.progress);

        ((EditText) findViewById(R.id.searchEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.newSearch(s.toString());
            }
        });
    }

    private void initRecyclerView() {
        adapter = new ImagesListAdapter(DependenciesProvider.provideImagesLoader());
        recyclerView = findViewById(R.id.list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (userAsksForMoreImages(newState, layoutManager)) {
                    presenter.nextPage();
                }
            }
        });
    }

    private boolean userAsksForMoreImages(int newState, GridLayoutManager layoutManager) {
        return newState == RecyclerView.SCROLL_STATE_IDLE
                && layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void updateState(ImagesSearchViewState state) {
        if (state instanceof ShowImages) {
            adapter.imageUrls = ((ShowImages) state).imageUrls;
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setAlpha(1);
        }

        if (state instanceof ImagesSearchViewState.Error) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAlpha(1);
        }

        if (state instanceof Loading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(0.3f);
        }

        if (state instanceof LastPage) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAlpha(1);
            Toast.makeText(this, "These are no more images based on your search", Toast.LENGTH_SHORT).show();
        }
    }
}
