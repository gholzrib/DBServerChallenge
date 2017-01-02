package gholzrib.dbserverchallenge.ui.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gholzrib.dbserverchallenge.R;
import gholzrib.dbserverchallenge.core.handlers.RequestsHandler;
import gholzrib.dbserverchallenge.core.listeners.RequestsListener;
import gholzrib.dbserverchallenge.core.models.Restaurant;
import gholzrib.dbserverchallenge.core.models.User;
import gholzrib.dbserverchallenge.core.requests.RequestRestaurants;
import gholzrib.dbserverchallenge.core.utils.CheckConnection;
import gholzrib.dbserverchallenge.core.utils.Constants;
import gholzrib.dbserverchallenge.core.utils.PreferencesManager;
import gholzrib.dbserverchallenge.ui.adapters.RestaurantsAdapter;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class Restaurants extends Fragment implements OnMapReadyCallback, RequestsListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    private ArrayList<Restaurant> mRestaurantsList = new ArrayList<>();
    private ProgressDialog mLoadingDialog;

    private RecyclerView mRvRestaurants;
    private RestaurantsAdapter mAdapter;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private LinearLayout mLnrNoInternet;
    private LinearLayout mLnrNoData;

    public int mCurrentMode = Constants.VISUALIZATION_MODE_MAP;
    private RequestsHandler mCurrentRequest;
    private Location mCurrentLocation;

    public static Restaurants newInstance() {
        return new Restaurants();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mLnrNoInternet = (LinearLayout) view.findViewById(R.id.cnt_no_internet_lnr_warning);
        mLnrNoData = (LinearLayout) view.findViewById(R.id.cnt_no_data_lnr_warning);

        mLoadingDialog = new ProgressDialog(getActivity());
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setMessage(getString(R.string.message_loading));

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg_restaurants_map);
        mMapFragment.getMapAsync(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.background_divider));
        mAdapter = new RestaurantsAdapter(getActivity(), mRestaurantsList);

        mRvRestaurants = (RecyclerView) view.findViewById(R.id.frg_restaurants_list);
        mRvRestaurants.setLayoutManager(linearLayoutManager);
        mRvRestaurants.addItemDecoration(dividerItemDecoration);
        mRvRestaurants.setAdapter(mAdapter);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        view.findViewById(R.id.cnt_no_internet_btn_try_again).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCurrentRequest != null) {
            mCurrentRequest.cancelRequest();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(12f);
    }

    @Override
    public void onClick(View view) {
        changeVisualizationMode();
        attemptToGetRestaurants();
    }

    @Override
    public void onPreExecute() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void onRequestEnds(int operation, boolean isSuccess, String parsedData) {
        if (isSuccess) {
            try {
                JSONObject jsonData = new JSONObject(parsedData);
                String status = jsonData.getString(Constants.REQUEST_RESPONSE_STATUS);
                if (status.equals(Constants.REQUEST_STATUS_OK)) {
                    JSONArray jsonResults = jsonData.getJSONArray(Constants.REQUEST_RESPONSE_RESULTS);
                    Gson gson = new Gson();
                    User user = PreferencesManager.getUser(getActivity());
                    mRestaurantsList = new ArrayList<>();
                    for (int i = 0; i < jsonResults.length(); i++) {
                        Restaurant restaurant = gson.fromJson(jsonResults.get(i).toString(), Restaurant.class);
                        // TODO: 02/01/2017 Remove the next line when the API for the Restaurant Votes is available
                        if (user.getVotesOfTheDay().contains(restaurant.getId())) restaurant.setVotes(1);
                        mRestaurantsList.add(restaurant);
                    }
                    if (mRestaurantsList.size() == 0) {
                        View mapView = mMapFragment.getView();
                        if (mapView != null) mapView.setVisibility(View.GONE);
                        mRvRestaurants.setVisibility(View.GONE);
                        mLnrNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (mCurrentMode == Constants.VISUALIZATION_MODE_MAP) {
                            addRestaurantsToMap();
                        } else {
                            updateList();
                        }
                    }
                } else {
                    String errorMessage = jsonData.getString(Constants.REQUEST_RESPONSE_ERROR_MESSAGE);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.error_request_unsuccessful, Toast.LENGTH_SHORT).show();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // TODO: 01/01/2017 Treat denied permission
        }
    }

    public void changeVisualizationMode() {
        View mapView = mMapFragment.getView();
        if (mCurrentMode == Constants.VISUALIZATION_MODE_MAP) {
            mRvRestaurants.setVisibility(View.GONE);
            if (mapView != null) mapView.setVisibility(View.VISIBLE);
            addRestaurantsToMap();
        } else {
            mRvRestaurants.setVisibility(View.VISIBLE);
            if (mapView != null) mapView.setVisibility(View.GONE);
            updateList();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestAccessLocationPermission();
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(icon));
            mAdapter.updateUserLocation(mCurrentLocation);
            attemptToGetRestaurants();
        }
    }

    private void requestAccessLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                0);
    }

    private void attemptToGetRestaurants() {
        if (CheckConnection.hasInternetConnection(getActivity(), false)) {
            mLnrNoInternet.setVisibility(View.GONE);
            RequestRestaurants request = new RequestRestaurants(this, 0);
            mCurrentRequest = request;
            String[] params = new String[] { String.valueOf(mCurrentLocation.getLatitude()),
                    String.valueOf(mCurrentLocation.getLongitude()),
                    getString(R.string.google_maps_key)};
            request.doRequest(params);
        } else {
            View mapView = mMapFragment.getView();
            if (mapView != null) mapView.setVisibility(View.GONE);
            mRvRestaurants.setVisibility(View.GONE);
            mLnrNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void addRestaurantsToMap() {
        for (int i = 0; i < mRestaurantsList.size(); i++) {
            Restaurant.ItemGeometry.ItemCoordinates coordinates = mRestaurantsList.get(i).getGeometry().getLocation();
            LatLng restaurant = new LatLng(coordinates.getLat(), coordinates.getLng());
            mMap.addMarker(new MarkerOptions().position(restaurant).title(mRestaurantsList.get(i).getName()));
        }
    }

    private void updateList() {
        mAdapter.updateList(mRestaurantsList);
    }

}
