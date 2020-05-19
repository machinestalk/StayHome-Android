package com.machinestalk.stayhome.service.body;

import com.machinestalk.android.service.input.BaseFormInput;
import com.machinestalk.stayhome.config.AppConfig;

import java.util.HashMap;

/**
 * Created on 12/27/2016.
 */

public class LoginBodyForm extends BaseFormInput {

    String username;
    String password;

    @Override
    public HashMap< String, Object > getParams() {

        HashMap< String, Object > map = new HashMap<>();
        map.put( "grant_type", "password" );
        map.put( "username", username );
        map.put( "password", password );
        map.put ("language", AppConfig.getInstance().getLanguage());
        map.put( "scope", "All offline_access" );
        map.put( "Client_Id", "MobileTestClient" );
        map.put( "Client_Secret", "nomd@123" );

        return map;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

}
