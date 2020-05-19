package com.machinestalk.stayhome.entities.base;


import android.text.TextUtils;

import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;

/**
 * Created on 2/3/17.
 */

public abstract class BaseEntity extends com.machinestalk.android.entities.BaseEntity {

    public String validateEmptyValue(String value) {
        if (TextUtils.isEmpty(value)){
            return ConnectedCar.getInstance().getStringLang(R.string.Gen_Gen_lbl_not_available);
        }

        return value;
    }

}
