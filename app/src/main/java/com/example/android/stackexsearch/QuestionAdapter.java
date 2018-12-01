package com.example.android.stackexsearch;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.stackexsearch.StackQuestion.SingleQuestion;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context mContext;
    private ArrayList<SingleQuestion> questions;

    public QuestionAdapter(Context mContext, ArrayList<SingleQuestion> questions) {
        this.mContext = mContext;
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new QuestionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.question_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder viewHolder, int i) {
        final SingleQuestion currentQuestion = questions.get(i);

        if (currentQuestion != null) {
            Glide.with(mContext).load(currentQuestion.getOwner().getProfile_image()).into(viewHolder.ownerImageIV);
            viewHolder.ownerNameTV.setText(currentQuestion.getOwner().getDisplay_name());
            viewHolder.questionTV.setText(currentQuestion.getTitle());

            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .setToolbarColor(mContext
                                    .getResources()
                                    .getColor(R.color.colorPrimary));
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(mContext, Uri.parse(currentQuestion.getLink()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {

        ImageView ownerImageIV;
        TextView ownerNameTV;
        TextView questionTV;
        View rootView;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.q_root_view);
            ownerImageIV = itemView.findViewById(R.id.owner_image);
            ownerNameTV = itemView.findViewById(R.id.owner_tv);
            questionTV = itemView.findViewById(R.id.que_title_tv);

        }
    }
}
