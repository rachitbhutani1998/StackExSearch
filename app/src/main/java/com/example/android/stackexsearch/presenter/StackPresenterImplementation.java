package com.example.android.stackexsearch.presenter;

import com.example.android.stackexsearch.model.StackQuestion;
import com.example.android.stackexsearch.network.StackSearchAPI;
import com.example.android.stackexsearch.ui.StackView;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StackPresenterImplementation implements StackPresenter {
    private StackView view;
    private StackSearchAPI api;

    public StackPresenterImplementation(StackView v, StackSearchAPI searchAPI) {
        this.view = v;
        this.api = searchAPI;
    }

    @Override
    public void getQuestions(Map<String, String> query) {
        view.showProgressDialog();
        Call<StackQuestion> questionCall = api.getQuestions(query);
        questionCall.enqueue(new Callback<StackQuestion>() {
            @Override
            public void onResponse(Call<StackQuestion> call, Response<StackQuestion> response) {
                view.updateRecycleView(response.body());
                view.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<StackQuestion> call, Throwable t) {
                view.showError(t.getLocalizedMessage());
                view.hideProgressDialog();
            }
        });
    }
}
