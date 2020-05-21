package com.example.coen268;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.restaurantViewHolder> {

    List<YelpRestaurant> restaurants;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class restaurantViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public TextView mName, mAddress, mDistance, mPrice, mReviewCount;
        public RatingBar mRating;

        public restaurantViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.rImage);
            mName = (TextView) itemView.findViewById(R.id.tvName);
            mAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            mRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            mPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            mReviewCount = (TextView) itemView.findViewById(R.id.tvNumRev);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(YelpRestaurant r) {
            mName.setText(r.name);
            mAddress.setText(r.location.address);
            mRating.setRating((float) r.rating);
            mDistance.setText(r.distanceInMiles() + "");
            mPrice.setText(r.price+ "");
            mReviewCount.setText(r.review_count + " Reviews");
            //Glide.with(itemView).load(r.image_url).into(mImage);
        }
    }

    public RestaurantsAdapter(Context context, List<YelpRestaurant> yelpRestaurants) {
        restaurants = yelpRestaurants;
    }

    @NonNull
    @Override
    public restaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        restaurantViewHolder lvh = new restaurantViewHolder(v, mListener);
        return lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull restaurantViewHolder holder, int position) {
        YelpRestaurant r = restaurants.get(position);
        holder.bind(r);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
