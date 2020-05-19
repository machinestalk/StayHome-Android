package com.machinestalk.stayhome.viewholders;

import android.view.View;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.viewholders.BaseViewHolder;
import com.machinestalk.stayhome.adapterdelegates.AddViewDelegate;
import com.machinestalk.stayhome.entities.AddEntity;

/**
 * Created on 12/27/2016.
 */

public class AddViewHolder extends BaseViewHolder< AddEntity > {

    Controller controller;
    AddEntity entity;

    public AddViewHolder( Controller controller, View itemView ) {
        super( itemView );
        this.controller = controller;
    }

    @Override
    public void bind( AddEntity entity ) {

        this.entity = entity;

    }

    @Override
    public void onClick( View view ) {
        super.onClick( view );
        if ( controller instanceof AddViewDelegate.AddClickListener )
            ( ( AddViewDelegate.AddClickListener ) controller ).onAddClicked();
    }
}
