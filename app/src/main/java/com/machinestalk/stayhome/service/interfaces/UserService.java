package com.machinestalk.stayhome.service.interfaces;

import com.machinestalk.android.service.ServiceCall;
import com.machinestalk.stayhome.constants.ServiceConstants;
import com.machinestalk.stayhome.entities.User;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.responses.BraceletListResponse;
import com.machinestalk.stayhome.responses.ChangePhoneNumberResponse;
import com.machinestalk.stayhome.responses.HomeConfigurationResponse;
import com.machinestalk.stayhome.responses.LastDayResponse;
import com.machinestalk.stayhome.responses.RefreshTokenResponse;
import com.machinestalk.stayhome.responses.StringResponse;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.service.body.DeviceTokenBody;
import com.machinestalk.stayhome.service.body.OTPBody;
import com.machinestalk.stayhome.service.body.RefreshTokenForm;
import com.machinestalk.stayhome.service.body.SignInBodyForm;
import com.machinestalk.stayhome.service.body.UserLocationBody;
import com.machinestalk.stayhome.service.body.UserStatusBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created on 12/19/2016.
 */

public interface UserService {

    @POST( ServiceConstants.POST_URL_LOGIN )
    ServiceCall<ChangePhoneNumberResponse> signIn(@Body SignInBodyForm body );

    @POST( ServiceConstants.POST_URL_LOGOUT )
    ServiceCall< String > logoutUser();

   @POST( ServiceConstants.POST_REFRESH_TOKEN )
   ServiceCall<RefreshTokenResponse> refreshToken(@Body RefreshTokenForm fields);

    @POST( ServiceConstants.POST_SIGN_IN_VERIFY_OTP )
    ServiceCall< User > VerifySignOTP(@Body OTPBody body );

    @POST( ServiceConstants.POST_UPDATE_DEVICE_TOKEN )
    ServiceCall< StringResponse > updateDeviceToken(@Body DeviceTokenBody body, @Path("deviceToken") String deviceToken);


    @POST( ServiceConstants.POST_UPDATE_DEVICE_TOKEN )
    Call< StringResponse > updateNewFirebaseToken(@Body DeviceTokenBody body, @Path("deviceToken") String deviceToken);


    @GET( ServiceConstants.POST_GET_CONFIGURATION )
    ServiceCall<HomeConfigurationResponse> getConfiguration(@Path("deviceToken") String deviceToken );

    @GET( ServiceConstants.POST_GET_USER_INF )
    ServiceCall<ZoneEntity> getUserInfo(@Path("customerID") String customerID );


    @POST( ServiceConstants.POST_SEND_USER_LOCATION )
    ServiceCall< StringResponse > sendUserLocation(@Body UserLocationBody body, @Path("customerId") String deviceToken);


    @POST( ServiceConstants.POST_SEND_USER_STATUS )
    ServiceCall< StringResponse > sendUserStatus(@Body UserStatusBody body, @Path("customerId") String customerId);


    @POST( ServiceConstants.POST_SEND_COMPLIANT_STATUS )
    Call< StringResponse > updateCompliantStatus(@Body CompliantBody body, @Path("deviceToken") String deviceToken);


    @Headers({"Content-Type: application/json"})
    @POST( ServiceConstants.POST_CONTACT_US )
    ServiceCall<StringResponse> contactUs(@Body String body );


    @GET( ServiceConstants.GET_LAST_VERSION )
    ServiceCall<HomeConfigurationResponse> getLastVersion(@Path("tenantId") String tenantId );

    @GET( ServiceConstants.GET_CHECK_BRACELET )
    ServiceCall<BraceletListResponse> checkBracelet(@Query("macAddress") String macAddress, @Query("deviceId") String deviceId, @Query("tenantId") String tenantId,
                                                   @Query("customerId") String customerId);

    @GET( ServiceConstants.GET_LAST_DAY )
    ServiceCall<LastDayResponse> getLastDay(@Path("customerId") String customerId);

    @GET( ServiceConstants.GET_BRACELET_LIST)
    ServiceCall<BraceletListResponse> getBraceletList(@Path("customerId") String customerId);

}
