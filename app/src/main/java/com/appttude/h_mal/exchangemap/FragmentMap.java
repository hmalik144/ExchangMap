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

import static com.appttude.h_mal.exchangemap.MapsJsonCall.*;

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
        protected void onPostExecute(MapItem mapItems) {
            super.onPostExecute(mapItems);

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLngBounds newBounds = null;

            try {
                ArrayList<MapItem.result> results = mapItems.getResults();

                for (MapItem.result mapItem1 : results){
                    boundsBuilder.include(mapItem1.getGeometry().getLocation());

                    MarkerOptions marker = new MarkerOptions().position(
                            mapItem1.getGeometry().getLocation())
                            .title(mapItem1.getName());
                    mMap.addMarker(marker);
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

}
