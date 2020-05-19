package com.machinestalk.stayhome.adapterdelegates;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.viewholders.BraceletViewHolder;

import java.util.List;

/**
 * Created on 4/20/2017.
 */

public class BraceletListDelegate implements AdapterDelegate<BraceletViewHolder> {

    Activity controller;
    MultiSelector multiSelector;

    public BraceletListDelegate(Activity controller, MultiSelector multiSelector) {
        this.controller = controller;
        this.multiSelector = multiSelector;
    }
    public BraceletListDelegate(Activity controller) {
        this.controller = controller;
    }

    @Override
    public boolean isForViewType(@NonNull List< ? > items, int position ) {
        return true;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_beacon_recyclerview;
    }

    @NonNull
    @Override
    public BraceletViewHolder getViewHolder(View itemView ) {
        return new BraceletViewHolder( controller, multiSelector,itemView);
    }
}
