package com.boliviaontouch.eroadtest.Clases;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andresvasquez on 9/11/15.
 */
public class TimezoneResponse {

    @SerializedName("dstOffset")
    public int dstOffset;

    @SerializedName("rawOffset")
    public int rawOffset;

    @SerializedName("status")
    public String status;

    @SerializedName("timeZoneId")
    public String timeZoneId;

    @SerializedName("timeZoneName")
    public String timeZoneName;

    public int getDstOffset() {
        return dstOffset;
    }

    public int getRawOffset() {
        return rawOffset;
    }

    public String getStatus() {
        return status;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }
}
