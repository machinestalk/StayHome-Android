package com.machinestalk.android.service.converters;

import com.machinestalk.android.interfaces.JsonServiceRequest;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created on 11/02/2016.
 */
@SuppressWarnings("TypeMayBeWeakened")
final class ServiceJsonRequestConverter<T extends JsonServiceRequest> implements Converter<T, RequestBody> {

    static final ServiceJsonRequestConverter INSTANCE = new ServiceJsonRequestConverter();
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, value.getJson().toString());
    }
}
