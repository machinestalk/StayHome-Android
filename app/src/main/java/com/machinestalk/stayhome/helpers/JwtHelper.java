package com.machinestalk.stayhome.helpers;

import android.util.Base64;

import com.google.gson.JsonElement;
import com.machinestalk.android.utilities.JsonUtility;

import java.io.UnsupportedEncodingException;

/**
 * Created on 1/10/2017.
 */

public class JwtHelper {

    String      token;
    JsonElement json;

    public JwtHelper( String token ) {
        this.token = token;
        try {
            decode();
        } catch ( Exception e ) {

        }

    }

    public void decode() throws Exception {
        try {
            String[] split = token.split( "\\." );
            this.json = JsonUtility.parse( convertToJsonString( split[1] ) );
        } catch ( UnsupportedEncodingException e ) {
        }
    }

    private String convertToJsonString( String strEncoded ) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode( strEncoded, Base64.URL_SAFE );
        return new String( decodedBytes, "UTF-8" );
    }

    public JsonElement getJson() {
        return json;
    }
}
