package com.machinestalk.android.service.converters;

import com.machinestalk.android.interfaces.WebServiceResponse;
import com.machinestalk.android.utilities.JavaUtility;
import com.machinestalk.android.utilities.JsonUtility;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created on 11/02/2016.
 */
@SuppressWarnings("unchecked")
final class ServiceResponseConverter<T extends WebServiceResponse> implements Converter<ResponseBody, T>{

    private Type type;

    ServiceResponseConverter(Type type) {

        try {
            this.type = type;
        } catch (Exception e) {
        }
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        T obj = null;
        try {
            obj = (T) JavaUtility.getClassOfTokenType(type).newInstance();
            obj.loadJson(JsonUtility.parse(value.string()));
        } catch (Exception e) {

        }
        return obj;
    }
}
