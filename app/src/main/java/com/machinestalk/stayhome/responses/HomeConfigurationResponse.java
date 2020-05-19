package com.machinestalk.stayhome.responses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.machinestalk.android.service.response.BaseResponse;
import com.machinestalk.android.utilities.JsonUtility;

public class HomeConfigurationResponse extends BaseResponse<Configuration> {
    @Override
    public void loadJson(JsonElement jsonElement) {
        if (JsonUtility.isJsonElementNull(jsonElement)) {
            return;
        }

        if (list != null && list.size() > 0) {
            list.clear();
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (JsonElement conf : jsonArray) {
            Configuration configuration = new Configuration();
            configuration.loadJson(conf);
            //if(!isLoggedInUser(userEntity)) {
            if (list != null)
                list.add(configuration);

        }
    }
}