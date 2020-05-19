package com.machinestalk.stayhome.service;

import android.util.Log;

import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.responses.StringResponse;
import com.machinestalk.stayhome.service.body.CompliantBody;
import com.machinestalk.stayhome.service.body.DeviceTokenBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceApi {
    private static ServiceApi mInstance;

    public static ServiceApi getInstance() {
        if (mInstance == null) {
            mInstance = new ServiceApi();
        }

        return mInstance;
    }


    /**
     *
     * @param token
     */
    public  void sendFirebseToken(String token){

        DeviceTokenBody deviceTokenBody = new DeviceTokenBody();
        deviceTokenBody.setFirebaseToken(token);

        ConnectedCar.getInstance().getServiceFactory().getUserService()
                .updateNewFirebaseToken( deviceTokenBody, AppConfig.getInstance().getUser().getDeviceId())
                .enqueue(new Callback<StringResponse>() {
                    @Override
                    public void onResponse(Call<StringResponse> call, Response<StringResponse> stringResponse) {
                      //  Logger.i("update new token response :" + stringResponse.code());

                        if (stringResponse.isSuccessful()) {
                        //    Logger.i("update new token success");

                        } else {
                          //  Logger.i("update new token failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<StringResponse> call, Throwable t) {
                      //  Logger.i("update new token failed");

                    }
                });
    }


    /**
     *
     * @param compliantBody
     */
    public  void sendCompliantStatus(CompliantBody compliantBody){


        ConnectedCar.getInstance().getServiceFactory().getUserService()
                .updateCompliantStatus(compliantBody, AppConfig.getInstance().getUser().getDeviceToken())
                .enqueue(new Callback<StringResponse>() {
                    @Override
                    public void onResponse(Call<StringResponse> call, Response<StringResponse> stringResponse) {
                     //   Logger.i("update compliant status response :" + stringResponse.code());

                        if (stringResponse.isSuccessful()) {
                          //  Logger.i("update compliant status success");

                        } else {
                           // Logger.i("update compliant status failed ");

                        }
                    }

                    @Override
                    public void onFailure(Call<StringResponse> call, Throwable t) {
                        Log.i(getClass().getName(),"update compliant status failed");
                    }
                });
    }

}
