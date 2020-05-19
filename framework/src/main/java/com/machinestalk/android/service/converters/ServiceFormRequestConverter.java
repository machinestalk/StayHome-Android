package com.machinestalk.android.service.converters;

import com.machinestalk.android.interfaces.FormServiceRequest;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created on 17/03/2016.
 */
public class ServiceFormRequestConverter<T extends FormServiceRequest> implements Converter<T, RequestBody> {

    static final ServiceFormRequestConverter INSTANCE = new ServiceFormRequestConverter();
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");
    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, value.toString());
    }
}
