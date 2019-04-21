package com.dgimatov.flickrsearchdemo.search.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dgimatov.flickrsearchdemo.R;
import com.dgimatov.flickrsearchdemo.di.DependenciesProvider;
import com.dgimatov.flickrsearchdemo.search.domain.ImagesSearchInteractor;

import java.util.Collections;

import static com.dgimatov.flickrsearchdemo.search.view.ImagesSearchViewState.*;

/**
 * Main activity which holds references to and initializes views.
 * Also injects an interactor and maps it's states to updates of activity's views
 */
public class MainActivity extends AppCompatActivity implements ImagesSearchView {

    private ImagesSearchInteractor interactor;
    private ImagesListAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private Handler uiHandler = new Handler();
    private Runnable newSearchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        interactor = DependenciesProvider.provideImagesSearchInteractor();

        searchEditText = findViewById(R.id.searchEditText);

        progressBar = findViewById(R.id.progress);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                uiHandler.removeCallbacks(newSearchRunnable);
                newSearchRunnable = () -> interactor.newSearch(s.toString());
                uiHandler.postDelayed(newSearchRunnable, 500);
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
                    interactor.nextPage();
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
        interactor.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        interactor.onStop();
    }

    private void showErrorDialog(Throwable e) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setCancelable(true)
                .setMessage("Images loading failed: " + e.getLocalizedMessage())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void updateState(ImagesSearchViewState state) {
        uiHandler.post(() -> {
            if (state instanceof ShowImages) {
                adapter.imageUrls = ((ShowImages) state).imageUrls;
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setAlpha(1);
            }

            if (state instanceof ImagesSearchViewState.Error) {
                recyclerView.setAlpha(1);
                progressBar.setVisibility(View.GONE);
                adapter.imageUrls = Collections.emptyList();
                adapter.notifyDataSetChanged();
                searchEditText.setText("");
                showErrorDialog(((ImagesSearchViewState.Error) state).exception);
            }

            if (state instanceof Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setAlpha(0.5f);
            }

            if (state instanceof LastPage) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setAlpha(1);
                Toast.makeText(this, "These are no more images based on your search", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
