package com.dgimatov.flickrsearchdemo.search.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.dgimatov.flickrsearchdemo.R;
import com.dgimatov.flickrsearchdemo.di.DependenciesProvider;

public class MainActivity extends AppCompatActivity implements ImagesSearchView {

    private ImagesSearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = DependenciesProvider.imagesSearchPresenter();

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
        Log.i("test_", "state: " + state);
    }
}
