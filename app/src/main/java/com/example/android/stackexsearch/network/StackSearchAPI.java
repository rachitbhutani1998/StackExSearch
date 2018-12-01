package com.example.android.stackexsearch.network;

import com.example.android.stackexsearch.data.StackQuestion;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface StackSearchAPI {

    String BASE_URL="https://api.stackexchange.com/2.2/";

    @GET("search")
    Call<StackQuestion> getQuestions(@QueryMap Map<String,String> query);

}
