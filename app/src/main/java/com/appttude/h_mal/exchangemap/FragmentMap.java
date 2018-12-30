package com.appttude.h_mal.exchangemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;
import static com.appttude.h_mal.exchangemap.MapsJsonCall.*;

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;
import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    LatLngBounds bounds;

    private String TAG = getClass().getSimpleName();

    public FragmentMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_maps, container, false);


        FloatingActionButton fab = rootview.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = (MapsActivity.fragmentManager).beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container,new FragmentSearch())
                        .addToBackStack("search").commit();
            }
        });

        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(getLatLong.latitude, getLatLong.longitude);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            mMap.setMyLocationEnabled(true);
        }


        mMap.addCircle(new CircleOptions().center(current));
//        mMap.addMarker(new MarkerOptions().position(current).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

        double radiusDegrees = 0.;
        LatLng northEast = new LatLng(current.latitude + radiusDegrees, current.longitude + radiusDegrees);
        LatLng southWest = new LatLng(current.latitude - radiusDegrees, current.longitude - radiusDegrees);
        bounds = LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();
        mGeoDataClient = Places.getGeoDataClient(getContext());

        getLocationOnMap locationOnMap = new getLocationOnMap();
        locationOnMap.execute(current);

//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(TYPE_FILTER_ESTABLISHMENT )
//                .build();
//
//        Task<AutocompletePredictionBufferResponse> results =
//                mGeoDataClient.getAutocompletePredictions("currency", bounds, typeFilter);
//
//
//            LocationAsyncTask locationAsyncTask = new LocationAsyncTask();
//            locationAsyncTask.execute(results);


    }

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

//        final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//        LatLngBounds newBounds = null;

        try{
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();


                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    Place myPlace = placeLikelihood.getPlace();
                                    Log.i(TAG, "Place found: " + myPlace.getName());
                                    mMap.addMarker(new MarkerOptions().position(myPlace.getLatLng()).title(myPlace.getName().toString()));
//                                    boundsBuilder.include(placeLikelihood.getPlace().getLatLng());
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();


                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }

                    });
//            newBounds = boundsBuilder.build();
        }catch (Exception e){
            Log.e(TAG, "showCurrentPlace: ", e);
        }finally {
//            try {
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(newBounds, 10);
//                mMap.animateCamera(cameraUpdate);
//            }catch (Exception e){
//                Log.e(TAG, "onPostExecute: ", e);
//            }finally {
//                if (newBounds == null){
//                    mMap.animateCamera( CameraUpdateFactory.newLatLngBounds(bounds, 0) );
//                }
//            }
        }


    }

    private class getLocationOnMap extends AsyncTask<LatLng,Void,MapItem>{

        @Override
        protected MapItem doInBackground(LatLng... latLngs) {
            String json = null;
            MapItem mapItem = null;

            try {
                Log.i(TAG, "doInBackground: " + UriBuilder(latLngs[0]) );
                URL url = createUrl(UriBuilder(latLngs[0]));
                json = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (json != null){
                    mapItem = extractFeatureFromJson(json);
                }
            }

            return mapItem;
        }

        @Override
        protected void onPostExecute(MapItem mapItem) {
            super.onPostExecute(mapItem);

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLngBounds newBounds = null;

            try {

                ArrayList<MapItem.result> results = mapItem.getResults();

                for (int i=0; i <results.size(); i++){
                    String id = results.get(i).getPlace_id();
                    boundsBuilder.include(mapItem.getResults().get(i).getGeometry().getLocation());

                    mGeoDataClient.getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful()) {
                                PlaceBufferResponse places = task.getResult();
                                Place myPlace = places.get(0);

                                Log.i(TAG, "Place found: " + myPlace.getName());
                                mMap.addMarker(new MarkerOptions().position(myPlace.getLatLng()).title(myPlace.getName().toString()));
                                places.release();
                            } else {
                                Log.e(TAG, "Place not found.");
                            }
                        }
                    });
                }
                newBounds = boundsBuilder.build();
            }catch (Exception e){
                Log.e(TAG, "onPostExecute: ",e );
            }finally {
                try {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(newBounds, 10);
                    mMap.animateCamera(cameraUpdate);
                }catch (Exception e){
                    Log.e(TAG, "onPostExecute: ", e);
                }finally {
                    if (newBounds == null){
                        mMap.animateCamera( CameraUpdateFactory.newLatLngBounds(bounds, 0) );
                    }
                }

            }

        }
    }

    private class LocationAsyncTask extends AsyncTask<Task<AutocompletePredictionBufferResponse>, Void, AutocompletePredictionBufferResponse>{

        @Override
        protected AutocompletePredictionBufferResponse doInBackground(Task<AutocompletePredictionBufferResponse>... tasks) {

            try {
                Tasks.await(tasks[0], 60, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            return tasks[0].getResult();
        }

        @Override
        protected void onPostExecute(AutocompletePredictionBufferResponse response) {
            super.onPostExecute(response);

            try {

            Log.i(TAG, "Query completed. Received " + response.getCount()
                    + " predictions.");

            String [] ids = {"ChIJndkxNgNakWsRjHGZBzHxu8M","ChIJUcxf7YxbkWsRsndgEcBNlLQ","ChIJk-7r9ARakWsRv16cDh3GXzU","ChIJh4SRLgNakWsRuyWBDLouw-4","ChIJL533ngRakWsRUp51ucHAp5Q","ChIJy1uXvQRakWsRT6xLCK9LmzY","ChIJcTZWngRakWsRm8Cz7egsDm8","ChIJDfur3xxakWsR4WP10zl01Hg"};

            for (int i=0; i <ids.length; i++){
                mGeoDataClient.getPlaceById(ids[i]).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            Place myPlace = places.get(0);
                            Log.i(TAG, "Place found: " + myPlace.getName());
                            mMap.addMarker(new MarkerOptions().position(myPlace.getLatLng()).title(myPlace.getName().toString()));
                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
            }

//            for (int i = 0; i < response.getCount(); i++){
//                final String retrievedId = response.get(i).getPlaceId();
//
//                mGeoDataClient.getPlaceById(retrievedId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                        if (task.isSuccessful()) {
//                            PlaceBufferResponse places = task.getResult();
//                            Place myPlace = places.get(0);
//                            Log.i(TAG, "Place found: " + myPlace.getName());
//                            mMap.addMarker(new MarkerOptions().position(myPlace.getLatLng()).title(myPlace.getName().toString()));
//                            places.release();
//                        } else {
//                            Log.e(TAG, "Place not found.");
//                        }
//                    }
//                });
//            }

//                // Freeze the results immutable representation that can be stored safely.
//                ArrayList<AutocompletePrediction> al = DataBufferUtils.freezeAndClose(response);
//
//                for (AutocompletePrediction p : al) {
//                    CharSequence cs = p.getFullText(new CharacterStyle() {
//                        @Override
//                        public void updateDrawState(TextPaint tp) {
//                            mMap.addMarker(new MarkerOptions().position().title("Marker in Sydney"));
//                        }
//                    });
//                    Log.i(TAG, cs.toString());
//                }

            } catch (RuntimeExecutionException e) {
                // If the query did not complete successfully return null
                Log.e(TAG, "Error getting autocomplete prediction API call", e);
            } finally {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
            }
        }
    }

}
