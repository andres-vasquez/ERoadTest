package com.boliviaontouch.eroadtest.Clases;

/**
 * Created by andresvasquez on 9/11/15.
 */
public class Bubble {
    double latitude;
    double longitude;
    String timezone;
    String UTC_time;
    String local_time;

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUTC_time() {
        return UTC_time;
    }

    public void setUTC_time(String UTC_time) {
        this.UTC_time = UTC_time;
    }

    public String getLocal_time() {
        return local_time;
    }

    public void setLocal_time(String local_time) {
        this.local_time = local_time;
    }
}
