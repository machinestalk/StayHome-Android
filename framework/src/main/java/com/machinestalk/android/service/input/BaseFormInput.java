package com.machinestalk.android.service.input;

import com.machinestalk.android.interfaces.FormServiceRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17/03/2016.
 */
public abstract class BaseFormInput implements FormServiceRequest {

    @Override
    public String toString() {
        HashMap<String, Object> paramsMap = getParams();
        if(paramsMap == null) {
            return super.toString();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry: paramsMap.entrySet()) {
            stringBuilder.append("&")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        stringBuilder.deleteCharAt(0);
        return stringBuilder.toString();
    }
}
