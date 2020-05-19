package com.machinestalk.stayhome.responses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

public class BraceletListResponse extends BaseResponse {
    private String mMacAddress;
    private long connectedTime;
    @Override
    public void loadJson(JsonElement jsonElement) {
        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonObject rootObject = jsonElement.getAsJsonObject();

        JsonArray dataJsonArray = JsonUtility.getJsonArray(rootObject, "data");

        if (dataJsonArray != null && dataJsonArray.size() > 0){

            JsonObject jsonObject = dataJsonArray.get(0).getAsJsonObject();
            JsonObject inFoJsonObject = JsonUtility.getJsonObject(jsonObject, "additionalInfo");
            connectedTime = JsonUtility.getLong(inFoJsonObject, "connectDateTime");
            mMacAddress = JsonUtility.getString(jsonObject, "name");
        }
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public long getConnectedTime() {
        return connectedTime;
    }
}
