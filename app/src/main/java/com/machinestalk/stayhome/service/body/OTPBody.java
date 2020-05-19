package com.machinestalk.stayhome.service.body;

/**
 * Created on 12/29/2016.
 */

public class OTPBody {

    private String phoneNumber;
    private String phoneOtp;
    private String phoneUdid;


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneOtp(String phoneOtp) {
        this.phoneOtp = phoneOtp;
    }

    public void setPhoneUdid(String phoneUdid) {
        this.phoneUdid = phoneUdid;
    }
}


