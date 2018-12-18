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

import com.example.android.stackexsearch.R;
import com.example.android.stackexsearch.adapter.QuestionAdapter;
import com.example.android.stackexsearch.model.StackQuestion;
import com.example.android.stackexsearch.model.StackQuestion.SingleQuestion;
import com.example.android.stackexsearch.network.NetworkUtils;
import com.example.android.stackexsearch.network.StackSearchAPI;
import com.example.android.stackexsearch.presenter.StackPresenter;
import com.example.android.stackexsearch.presenter.StackPresenterImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements StackView {

    RecyclerView mQuestionsRV;
    ProgressBar mLoadingPB;
    TextView mErrorTV;
    SwipeRefreshLayout mRefreshLayout;
    SearchView searchView;

    Map<String, String> queryParameters;
    ArrayList<SingleQuestion> questionList;
    QuestionAdapter mAdapter;
    SharedPreferences preferences;
    StackPresenter stackPresenter;


    String sortParameter;
    String orderParameter;
    String defaultSearchString;
    String defaultSearchSite = "stackoverflow";
    String searchString;

    int pageNo = 1;
    boolean isDefaultStringSearched;

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

        //Preparing the list
        mQuestionsRV.setLayoutManager(new LinearLayoutManager(this));
        queryParameters = new HashMap<>();
        questionList = new ArrayList<>();
        mAdapter = new QuestionAdapter(MainActivity.this, questionList);

        //Initializing the StackPresenter
        StackSearchAPI searchAPI = NetworkUtils.getRetrofit().create(StackSearchAPI.class);
        stackPresenter = new StackPresenterImplementation(this, searchAPI);

        //Getting data from SharedPref
        preferences = getSharedPreferences(getString(R.string.preference_root_key), Context.MODE_PRIVATE);
        getDataFromSharedPreference();



        //Checking for favorite category in SharedPref
        if (!defaultSearchString.isEmpty()) {
            loadData(defaultSearchString);
            isDefaultStringSearched = true;
            searchString = defaultSearchString;
        } else mErrorTV.setText(R.string.start_search);


        mQuestionsRV.setAdapter(mAdapter);

        //Adding infinite scroll
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

        //Adding swipe down to refresh
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                questionList.clear();
                getDataFromSharedPreference();
                if (isDefaultStringSearched)
                    loadData(defaultSearchString);
                else loadData(searchString);
                mRefreshLayout.setRefreshing(false);
            }
        });

    }

    //Getting saved data from SharedPreferences
    private void getDataFromSharedPreference() {
        sortParameter = getResources().getStringArray(R.array.sort_array)[preferences.getInt(getString(R.string.sort_preference), 0)];
        orderParameter = getResources().getStringArray(R.array.order_array)[preferences.getInt(getString(R.string.order_preference), 0)];
        defaultSearchString = preferences.getString(getString(R.string.category_pref), getString(R.string.dummy_category));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (!searchString.equals(s)) {
                    questionList.clear();
                    pageNo = 1;
                    isDefaultStringSearched = false;
                    searchString = s;
                    loadData(s);
                } else
                    Toast.makeText(MainActivity.this, R.string.query_repeated, Toast.LENGTH_SHORT).show();
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


    //Loading the data from presenter
    private void loadData(String s) {

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {

            queryParameters.put(SEARCH_PARAM, s);
            queryParameters.put(SITE_PARAM, defaultSearchSite);
            queryParameters.put(SORT_PARAM, sortParameter);
            queryParameters.put(ORDER_PARAM, orderParameter);

            stackPresenter.getQuestions(queryParameters);

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

    //Overriding presenter callbacks
    @Override
    public void updateRecycleView(StackQuestion sq) {

        //Populating the list
        questionList.addAll(questionList.size(), sq.getItems());

        if (questionList.isEmpty()) {
            questionList.clear();
            mErrorTV.setVisibility(View.VISIBLE);
            mErrorTV.setText(R.string.empty_response);
        } else mErrorTV.setVisibility(View.INVISIBLE);

        //Updating the list
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void showError(String error) {
        mErrorTV.setVisibility(View.VISIBLE);
        mErrorTV.setText(error);
    }

    @Override
    public void showProgressDialog() {
        mErrorTV.setVisibility(View.VISIBLE);
        mErrorTV.setText(R.string.loading_tv);
        mLoadingPB.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressDialog() {
        mLoadingPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void observeOnData(StackQuestion sq) {

    }
}
