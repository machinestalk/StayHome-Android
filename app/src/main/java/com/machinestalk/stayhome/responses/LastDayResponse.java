package com.machinestalk.stayhome.responses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;
import com.machinestalk.stayhome.constants.KeyConstants;

public class LastDayResponse extends BaseResponse {

    private Long mLastDay;
    @Override
    public void loadJson(JsonElement jsonElement) {

        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        JsonObject rootObject = jsonElement.getAsJsonObject();

        JsonArray jsonArray = rootObject.getAsJsonArray(KeyConstants.KEY_LAST_DAY);

        if (jsonArray != null && jsonArray.size() > 0){
            mLastDay = jsonArray.get(0).getAsJsonObject().get("value").getAsLong() ;
        }
    }

    public Long getLastDay() {
        return mLastDay;
    }
}
