package com.example.android.stackexsearch.network;

import com.example.android.stackexsearch.model.StackQuestion;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface StackSearchAPI {

    @GET("search")
    Call<StackQuestion> getQuestions(@QueryMap Map<String, String> query);

}
