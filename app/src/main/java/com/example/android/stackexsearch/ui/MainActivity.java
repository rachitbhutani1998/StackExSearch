package com.example.android.stackexsearch.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stackexsearch.network.NetworkUtils;
import com.example.android.stackexsearch.data.QuestionAdapter;
import com.example.android.stackexsearch.R;
import com.example.android.stackexsearch.data.StackQuestion;
import com.example.android.stackexsearch.data.StackQuestion.SingleQuestion;
import com.example.android.stackexsearch.network.StackSearchAPI;

import java.util.ArrayList;
import java.util.HashMap;
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
    SwipeRefreshLayout mRefreshLayout;

    Map<String, String> queryParameters;
    ArrayList<SingleQuestion> questionList;
    QuestionAdapter mAdapter;
    SharedPreferences preferences;


    String sortParameter;
    String orderParameter;
    String defaultSearchString;
    String defaultSearchSite = "stackoverflow";
    String searchString;
    int pageNo = 1;

    static String SITE_PARAM = "site";
    static String SORT_PARAM = "sort";
    static String ORDER_PARAM = "order";
    static String SEARCH_PARAM = "intitle";
    static String PAGE_PARAM = "page";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining UI elements
        mQuestionsRV = findViewById(R.id.questions_rv);
        mLoadingPB = findViewById(R.id.loading_pb);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mErrorTV = findViewById(R.id.error_tv);
        mErrorTV.setVisibility(View.VISIBLE);

        //Preparing the list
        mQuestionsRV.setLayoutManager(new LinearLayoutManager(this));
        queryParameters = new HashMap<>();
        questionList = new ArrayList<>();
        mAdapter = new QuestionAdapter(MainActivity.this, questionList);
        preferences = getSharedPreferences(getString(R.string.preference_root_key), Context.MODE_PRIVATE);

        getDataFromSharedPreference();

        if (!defaultSearchString.isEmpty()) {
            loadData(defaultSearchString);
            searchString = defaultSearchString;
            mErrorTV.setText(getString(R.string.loading_tv));
        } else mErrorTV.setText(R.string.start_search);


        mQuestionsRV.setAdapter(mAdapter);

        mQuestionsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!questionList.isEmpty())
                    if (!recyclerView.canScrollVertically(1)) {
                        pageNo++;
                        queryParameters.put(PAGE_PARAM, String.valueOf(pageNo));
                        loadData(searchString);
                    }
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                questionList.clear();
                getDataFromSharedPreference();
                loadData(searchString);
                mRefreshLayout.setRefreshing(false);
            }
        });

    }

    //Getting saved data from SharedPreferences
    private void getDataFromSharedPreference() {
        sortParameter = getResources().getStringArray(R.array.sort_array)[preferences.getInt(getString(R.string.sort_preference), 0)];
        orderParameter = getResources().getStringArray(R.array.order_array)[preferences.getInt(getString(R.string.order_preference), 0)];
        defaultSearchString = preferences.getString(getString(R.string.category_pref), "Android");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (!searchString.equals(s)) {
                    questionList.clear();
                    pageNo = 1;
                    searchString = s;
                    loadData(s);
                    mErrorTV.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(MainActivity.this, "Same query requested.", Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }


    private void loadData(String s) {
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            mLoadingPB.setVisibility(View.VISIBLE);
            mErrorTV.setText(R.string.loading_tv);

            queryParameters.put(SEARCH_PARAM, s);
            queryParameters.put(SITE_PARAM, defaultSearchSite);
            queryParameters.put(SORT_PARAM, sortParameter);
            queryParameters.put(ORDER_PARAM, orderParameter);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(StackSearchAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            StackSearchAPI searchAPI = retrofit.create(StackSearchAPI.class);
            Call<StackQuestion> questionCall = searchAPI.getQuestions(queryParameters);


            questionCall.enqueue(new Callback<StackQuestion>() {
                @Override
                public void onResponse(@NonNull Call<StackQuestion> call, @NonNull Response<StackQuestion> response) {
                    mLoadingPB.setVisibility(View.INVISIBLE);

                    if (response.body() != null) {
                        Toast.makeText(MainActivity.this, String.valueOf(response.body().getQuota_remaining()), Toast.LENGTH_SHORT).show();

                        //Populating the list
                        questionList.addAll(questionList.size(), response.body().getItems());

                        if (questionList.isEmpty()) {
                            questionList.clear();
                            mErrorTV.setVisibility(View.VISIBLE);
                            mErrorTV.setText(R.string.empty_response);
                        } else mErrorTV.setVisibility(View.INVISIBLE);

                        //Updating the list
                        mAdapter.notifyDataSetChanged();

                    } else {
                        mErrorTV.setVisibility(View.VISIBLE);
                        mErrorTV.setText(String.valueOf(response.raw()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StackQuestion> call, @NonNull Throwable t) {
                    mLoadingPB.setVisibility(View.INVISIBLE);
                    mErrorTV.setVisibility(View.VISIBLE);
                    mErrorTV.setText(t.getLocalizedMessage());
                }
            });
        } else {
            mErrorTV.setText(getString(R.string.no_internet));
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                DialogFragment settingsFragment = new SettingsFragment();
                settingsFragment.showNow(getSupportFragmentManager(), getString(R.string.settings));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
