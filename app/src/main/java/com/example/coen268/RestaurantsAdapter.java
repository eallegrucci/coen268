package com.example.coen268;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        public TextView mName, mAddress, mRating, mDistance, mPrice, mReviewCount;

        public restaurantViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
//            mImage = itemView.findViewById(R.id.tvImage);
            mName = itemView.findViewById(R.id.tvName);
//            mAddress = itemView.findViewById(R.id.tvAddress);
//            mRating = itemView.findViewById(R.id.tvRating);
//            mDistance = itemView.findViewById(R.id.tvDistance);
//            mPrice = itemView.findViewById(R.id.tvPrice);
//            mReviewCount = itemView.findViewById(R.id.tvReviewCount);

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
//            mImage.setImageResource(r.image_url);
            mName.setText(r.name);
            //mAddress.setText(r.location);
//            mRating.setText((int) r.rating);
//            mDistance.setText(r.distanceInMiles());
//            mPrice.setText(r.price);
//            mReviewCount.setText(r.review_count);
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
