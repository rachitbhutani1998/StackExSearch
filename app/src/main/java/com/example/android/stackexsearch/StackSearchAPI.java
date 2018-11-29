package com.example.android.stackexsearch;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface StackSearchAPI {

    String BASE_URL="https://api.stackexchange.com/2.2";

    @GET("search")
    Call<ArrayList<StackQuestion>> getQuestions(@QueryMap Map<String,String> query);

}
