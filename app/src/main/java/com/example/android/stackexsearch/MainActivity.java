package com.example.android.stackexsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView mQuestionsRV;
    ProgressBar mLoadingPB;
    TextView mErrorTV;
    Map<String, String> queryParameters;


    //TODO: Add Shared Preferences
    String sortParameter;
    String orderParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mQuestionsRV = findViewById(R.id.questions_rv);
        mLoadingPB = findViewById(R.id.loading_pb);
        mErrorTV = findViewById(R.id.error_tv);

        mQuestionsRV.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryParameters.put("tagged", s);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(StackSearchAPI.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                StackSearchAPI searchAPI = retrofit.create(StackSearchAPI.class);
                Call<ArrayList<StackQuestion>> questionCall = searchAPI.getQuestions(queryParameters);


                //TODO: Callback as list of StackQuestion
                questionCall.enqueue(new Callback<ArrayList<StackQuestion>>() {
                    @Override
                    public void onResponse(Call<ArrayList<StackQuestion>> call, Response<ArrayList<StackQuestion>> response) {

                    }

                    @Override
                    public void onFailure(Call<ArrayList<StackQuestion>> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }
}
