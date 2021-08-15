package com.example.compass.utils;

import com.example.compass.models.DestinationInputStatus;

public class DestinationInputValidator {

    static public DestinationInputStatus validateDestinationInput(String lat, String lon) {
        if (lat.equals("") || lon.equals("")) {
            return DestinationInputStatus.EMPTY;
        }
        if (Double.parseDouble(lat) < -90.0 || Double.parseDouble(lat) > 90.0 ) {
            return DestinationInputStatus.OUT_OF_RANGE;
        }
        if (Double.parseDouble(lon) < -180.0 || Double.parseDouble(lon) > 180.0) {
            return DestinationInputStatus.OUT_OF_RANGE;
        }
        return DestinationInputStatus.VALID;
    }
}
