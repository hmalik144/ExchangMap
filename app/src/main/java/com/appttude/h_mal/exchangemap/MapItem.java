package com.appttude.h_mal.exchangemap;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.net.URL;
import java.util.ArrayList;

public class MapItem {

    private String next_page_token;
    private ArrayList<result> results;

    public static class result{
        private String formatted_Address;
        private Geometry geometry;
        private URL icon;
        private String placeId;
        private String name;
        private OpeningHours openingHours;
        private ArrayList<Photo> photos;
        private String place_id;
        private PlusCode plusCode;
        private Double rating;
        private String reference;
        private ArrayList<String> types;

        public result(String formatted_Address, Geometry geometry, URL icon, String placeId, String name,
                      @Nullable OpeningHours openingHours, @Nullable ArrayList<Photo> photos, String place_id, PlusCode plusCode, Double rating,
                      String reference, ArrayList<String> types) {
            this.formatted_Address = formatted_Address;
            this.geometry = geometry;
            this.icon = icon;
            this.placeId = placeId;
            this.name = name;
            this.openingHours = openingHours;
            this.photos = photos;
            this.place_id = place_id;
            this.plusCode = plusCode;
            this.rating = rating;
            this.reference = reference;
            this.types = types;
        }

        public String getFormatted_Address() {
            return formatted_Address;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public URL getIcon() {
            return icon;
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getName() {
            return name;
        }

        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public ArrayList<Photo> getPhotos() {
            return photos;
        }

        public String getPlace_id() {
            return place_id;
        }

        public PlusCode getPlusCode() {
            return plusCode;
        }

        public Double getRating() {
            return rating;
        }

        public String getReference() {
            return reference;
        }

        public ArrayList<String> getTypes() {
            return types;
        }
    }

    private String status;

    public MapItem(String next_page_token, ArrayList<result> results, String status) {
        this.next_page_token = next_page_token;
        this.results = results;
        this.status = status;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public ArrayList<result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class Geometry{
        private LatLng location;
        private LatLngBounds viewport;

        public Geometry(LatLng location, LatLngBounds viewport) {
            this.location = location;
            this.viewport = viewport;
        }

        public LatLng getLocation() {
            return location;
        }

        public LatLngBounds getViewport() {
            return viewport;
        }
    }

    public static class OpeningHours{
        private Boolean openNow;

        public OpeningHours(Boolean openNow) {
            this.openNow = openNow;
        }

        public Boolean getOpenNow() {
            return openNow;
        }
    }

    public static class Photo{
        private int height;
        private String htmlAttributions;
        private String photoReference;
        private int width;

        public Photo(int height, String htmlAttributions, String photoReference, int width) {
            this.height = height;
            this.htmlAttributions = htmlAttributions;
            this.photoReference = photoReference;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public String getHtmlAttributions() {
            return htmlAttributions;
        }

        public String getPhotoReference() {
            return photoReference;
        }

        public int getWidth() {
            return width;
        }
    }

    public static class PlusCode {

        private String globalCode;
        private String compoundCode;

        public PlusCode(String globalCode, String compoundCode) {
            this.globalCode = globalCode;
            this.compoundCode = compoundCode;
        }

        public String getGlobalCode() {
            return globalCode;
        }

        public String getCompoundCode() {
            return compoundCode;
        }

    }
}


