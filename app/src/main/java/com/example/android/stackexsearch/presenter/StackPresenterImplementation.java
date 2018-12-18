package com.example.android.stackexsearch.presenter;

import android.util.Log;

import com.example.android.stackexsearch.model.StackQuestion;
import com.example.android.stackexsearch.network.StackSearchAPI;
import com.example.android.stackexsearch.ui.StackView;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        Observable<StackQuestion> questionCall = api.getQuestions(query);
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(questionCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StackQuestion>() {
            @Override
            public void accept(StackQuestion stackQuestion) throws Exception {
                Log.e("TAG", "accept: got response");
                view.hideProgressDialog();
                view.updateRecycleView(stackQuestion);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("TAG", "accept: got error");
                view.hideProgressDialog();
                view.showError(throwable.getLocalizedMessage());
            }
        }));

    }

}
