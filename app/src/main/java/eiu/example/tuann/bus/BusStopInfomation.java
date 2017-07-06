package eiu.example.tuann.bus;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tuann on 4/28/2017.
 */

public class BusStopInfomation {
    private String address;
    private double latitude;
    private double longitude;
    private LatLng latLng;

    public BusStopInfomation(String address, double latitude, double longitude, LatLng latLng) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
