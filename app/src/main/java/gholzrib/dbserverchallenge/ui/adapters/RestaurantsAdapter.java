package gholzrib.dbserverchallenge.ui.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import gholzrib.dbserverchallenge.R;
import gholzrib.dbserverchallenge.core.models.Restaurant;
import gholzrib.dbserverchallenge.core.models.User;
import gholzrib.dbserverchallenge.core.utils.PreferencesManager;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantHolder>  {

    private Context mContext;
    private ArrayList<Restaurant> mRestaurantsList;
    private Location mUserLocation;

    public RestaurantsAdapter(Context context, ArrayList<Restaurant> restaurantsList) {
        mContext = context;
        this.mRestaurantsList = restaurantsList;
    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_restaurants, parent, false);
        return new RestaurantHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantHolder holder, int position) {
        User user = PreferencesManager.getUser(mContext);
        Restaurant restaurant = mRestaurantsList.get(position);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        holder.mTxtName.setText(restaurant.getName());
        if (restaurant.getVicinity() != null) holder.mTxtAddress.setText(restaurant.getVicinity());
        if (mUserLocation != null) {
            int distance = getDistanceToRestaurant(restaurant.getGeometry().getLocation());
            if (distance != 0) {
                String meters = distance + " " + mContext.getString(R.string.distance);
                holder.mTxtDistance.setText(meters);
            }
        }
        String votes = restaurant.getVotes() + " " + mContext.getString(R.string.votes);
        holder.mTxtVotes.setText(votes);

        boolean restaurantVotedByUser = user.getVotesOfTheDay().contains(restaurant.getId());
        boolean restaurantAlreadyChosenThisWeek = PreferencesManager.getWeeklyWinners(mContext).contains(restaurant.getId());
        boolean insideVotingHours = hour < 13;
        holder.mBtnVote.setEnabled(!restaurantVotedByUser && !restaurantAlreadyChosenThisWeek && insideVotingHours);
    }

    @Override
    public int getItemCount() {
        return mRestaurantsList.size();
    }

    public void updateList(ArrayList<Restaurant> restaurantsList) {
        mRestaurantsList.clear();
        mRestaurantsList.addAll(restaurantsList);
        notifyDataSetChanged();
    }

    public void updateUserLocation(Location userLocation) {
        mUserLocation = userLocation;
        notifyDataSetChanged();
    }

    private int getDistanceToRestaurant(Restaurant.ItemGeometry.ItemCoordinates coordinates) {
        float[] distances = new float[1];
        Location.distanceBetween(mUserLocation.getLatitude(), mUserLocation.getLongitude(), coordinates.getLat(), coordinates.getLng(), distances);
        if (distances.length > 0) {
            return (int) distances[0];
        }
        return 0;
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTxtName;
        TextView mTxtAddress;
        TextView mTxtDistance;
        TextView mTxtVotes;
        TextView mBtnVote;

        public RestaurantHolder(View itemView) {
            super(itemView);

            mTxtName = (TextView) itemView.findViewById(R.id.adp_restaurants_txt_name);
            mTxtAddress = (TextView) itemView.findViewById(R.id.adp_restaurants_txt_address);
            mTxtDistance = (TextView) itemView.findViewById(R.id.adp_restaurants_txt_distance);
            mTxtVotes = (TextView) itemView.findViewById(R.id.adp_restaurants_txt_votes);
            mBtnVote = (TextView) itemView.findViewById(R.id.adp_restaurants_btn_vote);

            mBtnVote.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Restaurant restaurant = mRestaurantsList.get(getAdapterPosition());
            int votes = restaurant.getVotes() + 1;
            restaurant.setVotes(votes);

            User user = PreferencesManager.getUser(mContext);
            user.getVotesOfTheDay().add(restaurant.getId());
            PreferencesManager.setUser(mContext, user);

            notifyDataSetChanged();
        }
    }

}
