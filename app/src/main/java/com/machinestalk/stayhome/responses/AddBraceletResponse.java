package com.machinestalk.stayhome.responses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

public class AddBraceletResponse extends BaseResponse {
    private long creationDate;
    @Override
    public void loadJson(JsonElement jsonElement) {
        if ( jsonElement == null ) {
            return;
        }

        JsonObject rootObj = jsonElement.getAsJsonObject();

        if (rootObj != null){
            creationDate = JsonUtility.getLong(rootObj, "createdTime");
        }
    }

    public long getCreationDate() {
        return creationDate;
    }
}
