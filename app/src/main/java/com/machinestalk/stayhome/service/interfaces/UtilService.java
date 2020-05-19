package com.machinestalk.stayhome.service.interfaces;

import com.machinestalk.android.service.ServiceCall;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.constants.ServiceConstants;
import com.machinestalk.stayhome.responses.LocationResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created on 1/13/17.
 */

public interface UtilService {
    @GET( ServiceConstants.GET_ADDRESS_BY_LOCATION )
    ServiceCall<LocationResponse> getAddress(@Query(ServiceConstants.PATH_LATLNG) String latlng, @Query(ServiceConstants.PATH_LANGUAGE) String language , @Query(KeyConstants.KEY_GUIDANCE_GOOGLE_MAP_API) String key);

}
