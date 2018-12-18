package com.example.android.stackexsearch.ui;

import com.example.android.stackexsearch.model.StackQuestion;

public interface StackView {

    void updateRecycleView(StackQuestion sq);

    void showError(String error);

    void showProgressDialog();

    void hideProgressDialog();

    void observeOnData(StackQuestion sq);

}
