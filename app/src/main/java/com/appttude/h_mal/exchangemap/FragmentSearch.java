package com.appttude.h_mal.exchangemap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.appttude.h_mal.exchangemap.MapsActivity.getLocationName;
import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;
import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_CITIES;
import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT;


public class FragmentSearch extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;
    public static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-160),new LatLng(71,136)
    );


    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_search, container, false);

        String[] airports = getResources().getStringArray(R.array.airports);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, airports);

        final AutoCompleteTextView homeLocationEditText = rootView.findViewById(R.id.location_home);

        ImageView myLocation = rootView.findViewById(R.id.my_loc);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myLocation = getLocationName(getContext(),getLatLong.latitude,getLatLong.longitude);
                homeLocationEditText.setText(myLocation);
            }
        });

        AutoCompleteTextView departureEditText = rootView.findViewById(R.id.location_home_departure);
        departureEditText.setAdapter(adapter);

        AutoCompleteTextView arrivalEditText = rootView.findViewById(R.id.location_arrival_airport);
        arrivalEditText.setAdapter(adapter);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_FILTER_CITIES )
//                .setTypeFilter(TYPE_FILTER_ADDRESS)
                .build();

        mAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(),googleApiClient,LAT_LNG_BOUNDS,typeFilter);

        AutoCompleteTextView destinationEditText = rootView.findViewById(R.id.location_arrival_);

        destinationEditText.setAdapter(mAutocompleteAdapter);
        homeLocationEditText.setAdapter(mAutocompleteAdapter);

        return rootView;
    }



}
