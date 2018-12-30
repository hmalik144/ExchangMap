package com.appttude.h_mal.exchangemap;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.appttude.h_mal.exchangemap.MapItem.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapsJsonCall {

    private static String TAG = MapsJsonCall.class.getSimpleName();

    private static Context context;

    protected static String UriBuilder(LatLng l){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("textsearch")
                .appendPath("json")
                .appendQueryParameter("query","currency exchange")
                .appendQueryParameter("location",l.latitude+","+l.longitude)
                .appendQueryParameter("radius","3")
                .appendQueryParameter("key","QUl6YVN5QThEZERadkc2aWhTclI1VGxrRzRGWGI2ZmZ5dDE5X1Bn");

        return builder.build().toString().replace("%2C",",").replace("%20", "+");

    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("ERROR", "Error with creating URL ", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("", "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.d("", output.toString());
        return output.toString();

    }

    public static MapItem extractFeatureFromJson(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        String next_page_token = "";
        ArrayList<MapItem.result> results = new ArrayList<>();
        String status = "";

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
//            next_page_token = baseJsonResponse.getString("next_page_token");

            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentResult = resultsArray.getJSONObject(i);

                String formatted_Address = currentResult.getString("formatted_address");

                JSONObject geometryObject = currentResult.getJSONObject("geometry");
                JSONObject locationObject = geometryObject.getJSONObject("location");
                LatLng location = new LatLng(locationObject.getDouble("lat"),locationObject.getDouble("lng"));

                JSONObject viewPointObject = geometryObject.getJSONObject("viewport");
//                LatLngBounds viewPort= new LatLngBounds(
                LatLng northEast = new LatLng(viewPointObject.getJSONObject("northeast").getDouble("lat"),
                                viewPointObject.getJSONObject("northeast").getDouble("lng"));
                LatLng southWest = new LatLng(viewPointObject.getJSONObject("southwest").getDouble("lat"),
                                viewPointObject.getJSONObject("southwest").getDouble("lng"));
                LatLngBounds viewPort = LatLngBounds.builder().include(northEast).include(southWest).build();

                MapItem.Geometry geometry = new Geometry(location,viewPort);

                URL icon = createUrl(currentResult.getString("icon"));
                String placeId = currentResult.getString("id");
                String name = currentResult.getString("name");

                JSONObject openingHoursObject = null;
                OpeningHours openingHours = null;
                try {
                    openingHoursObject = currentResult.getJSONObject("opening_hours");
                }catch (Exception e){
                    Log.i(TAG, "extractFeatureFromJson: " + "no opening hours");
                }finally {
                    if (openingHoursObject != null){
                        openingHours = new OpeningHours(openingHoursObject.getBoolean("open_now"));
                    }
                }



                JSONArray photosArray = null;
                ArrayList<Photo> photos = null;
                try {
                    photosArray = currentResult.getJSONArray("photos");
                }catch (Exception e){
                    Log.i(TAG, "extractFeatureFromJson: " + "no photo");
                }finally {
                    if (photosArray != null){
                        photos = new ArrayList<>();
                        for (int p = 0; p < photosArray.length(); p++) {
                            JSONObject photoObject = photosArray.getJSONObject(p);

                            int height = photoObject.getInt("height");
                            String htmlAttributions = photoObject.getString("html_attributions");
                            String photoReference = photoObject.getString("photo_reference");
                            int width = photoObject.getInt("width");

                            photos.add(new Photo(height,htmlAttributions,photoReference,width));
                        }
                    }
                }



                String place_id = currentResult.getString("place_id");
                JSONObject plusCodeObject = currentResult.getJSONObject("plus_code");
                PlusCode plusCode = new PlusCode(plusCodeObject.getString("compound_code"),
                        plusCodeObject.getString("global_code"));

                Double rating = currentResult.getDouble("rating");
                String reference = currentResult.getString("reference");
                JSONArray typesObject = currentResult.getJSONArray("types");
                ArrayList<String> types = new ArrayList<>();
                for (int t = 0; t < typesObject.length(); t++) {
                    types.add(typesObject.get(t).toString());
                }


                MapItem.result result = new MapItem.result(formatted_Address,geometry,icon,placeId,name,openingHours,photos,place_id,plusCode,rating,reference,types);
                results.add(result);
            }

            status = baseJsonResponse.getString("status");

        } catch (JSONException e) {
            Log.e("Error", "Problem parsing the book JSON results", e);
        }

        return new MapItem(next_page_token, results, status);
    }
}
