package com.machinestalk.stayhome.responses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

public class LastVersionResponse extends BaseResponse {
    private String valueLastVersion;
    @Override
    public void loadJson(JsonElement jsonElement) {
        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray != null && jsonArray.size() > 0){
            JsonElement jsonEl = jsonArray.get(0);
            if (jsonEl != null){
                JsonObject jsonObject = jsonEl.getAsJsonObject();
                valueLastVersion = JsonUtility.getString(jsonObject, "value");
            }
        }
    }

    public String getValueLastVersion() {
        return valueLastVersion;
    }
}
