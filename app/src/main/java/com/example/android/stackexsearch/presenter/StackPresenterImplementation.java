package com.example.android.stackexsearch.presenter;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.android.stackexsearch.model.StackQuestion;
import com.example.android.stackexsearch.network.NetworkUtils;
import com.example.android.stackexsearch.ui.StackView;
import com.google.gson.Gson;

import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StackPresenterImplementation implements StackPresenter {
    private StackView view;

    public StackPresenterImplementation(StackView v) {
        this.view = v;
    }

    @Override
    public void getQuestions(final Context context, Map<String, String> query) {
        view.showProgressDialog();

//        final RequestQueue queue = Volley.newRequestQueue(context);
        final Uri.Builder uriBuilder = Uri.parse(NetworkUtils.BASE_URL).buildUpon();

        for (Map.Entry<String, String> currentQuery :
                query.entrySet()) {
            uriBuilder.appendQueryParameter(currentQuery.getKey(), currentQuery.getValue());
        }

        Cache cache=new DiskBasedCache(context.getCacheDir(),1024*1024);
        BasicNetwork network= new BasicNetwork(new HurlStack());

        final RequestQueue requestQueue=new RequestQueue(cache,network);
        requestQueue.start();

        Single<StackQuestion> questionSingle=Single.create(new SingleOnSubscribe<StackQuestion>() {
            @Override
            public void subscribe(final SingleEmitter<StackQuestion> emitter) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        uriBuilder.build().toString()
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        StackQuestion sq = new Gson().fromJson(response, StackQuestion.class);
                        emitter.onSuccess(sq);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                emitter.onError(error);
                            }
                        });
                requestQueue.add(stringRequest);
            }
        });

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(questionSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StackQuestion>() {
                    @Override
                    public void accept(StackQuestion stackQuestion) {
                        view.hideProgressDialog();
                        view.updateRecycleView(stackQuestion);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        view.hideProgressDialog();
                        view.showError(throwable.getLocalizedMessage());
                    }
                }));

    }

}
