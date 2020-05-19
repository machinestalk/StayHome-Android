package com.machinestalk.stayhome.entities;

import com.google.gson.JsonElement;
import com.machinestalk.android.entities.BaseEntity;

/**
 * Created on 1/10/2017.
 */
public class AddEntity extends BaseEntity {
    private boolean visible = true;

    @Override
    public void loadJson( JsonElement jsonElement ) {

    }

    public void setVisibility( boolean visible ) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
