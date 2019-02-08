package com.thiagomatheusms.famousmovies.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Review;
import com.thiagomatheusms.famousmovies.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mReviews;

    public ReviewsAdapter(List<Review> mReviews) {
        this.mReviews = mReviews;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayoutItemReviewList = R.layout.item_review_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayoutItemReviewList, parent, false);
        ReviewsAdapterViewHolder reviewsAdapterViewHolder = new ReviewsAdapterViewHolder(view);

        return reviewsAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
        holder.mAuthorName.setText(mReviews.get(position).getAuthor());
        holder.mReview.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews == null){
            return 0;
        }
        return mReviews.size();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView mAuthorName, mReview;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);

            mAuthorName = (TextView) itemView.findViewById(R.id.tv_review_author_full);
            mReview = (TextView) itemView.findViewById(R.id.tv_review_full);
        }
    }

    public void setReviewsList(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
}
