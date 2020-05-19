package com.machinestalk.stayhome.responses;

import com.google.gson.JsonElement;
import com.machinestalk.android.service.response.BaseResponse;

/**
 * Created by AhmedElyakoubiMachin on 23/01/2018.
 */

public class ChangePhoneNumberResponse extends BaseResponse {
    private String securityToken;

    @Override
    public void loadJson(JsonElement jsonElement) {
        if (jsonElement == null) {
            return;
        }
        securityToken = jsonElement.getAsJsonObject().get( "SecurityToken" ).getAsString();
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}
