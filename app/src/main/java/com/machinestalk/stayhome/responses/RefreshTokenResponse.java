package com.machinestalk.stayhome.responses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

public class RefreshTokenResponse extends BaseResponse {

    private String token;
    private String refreshToken;

    @Override
    public void loadJson( JsonElement jsonElement ) {

        if ( jsonElement == null ) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        token = JsonUtility.getString(jsonObject, "token");
        refreshToken = JsonUtility.getString(jsonObject, "refreshToken");

    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
