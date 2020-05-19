package com.machinestalk.stayhome.responses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

/**
 * Created on 5/3/2017.
 */

public class LocationResponse extends BaseResponse {

    String address;

    @Override
    public void loadJson( JsonElement jsonElement ) {

        if ( jsonElement == null ) {
            return;
        }

        JsonArray arr = JsonUtility.getJsonArray( jsonElement.getAsJsonObject(), "results" );
        if ( ( arr != null ) && ( arr.size() > 0 ) ) {
            JsonObject locationObj = arr.get( 0 ).getAsJsonObject();
            address = JsonUtility.getString( locationObj, "formatted_address" );
        }
    }

    public String getAddress() {
        return address;
    }
}
