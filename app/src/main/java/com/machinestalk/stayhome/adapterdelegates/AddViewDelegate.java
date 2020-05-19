package com.machinestalk.stayhome.adapterdelegates;

import android.view.View;

import androidx.annotation.NonNull;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.entities.AddEntity;
import com.machinestalk.stayhome.viewholders.AddViewHolder;

import java.util.List;

/**
 * Created on 1/10/2017.
 */
public class AddViewDelegate implements AdapterDelegate< AddViewHolder > {

    Controller controller;

    public AddViewDelegate( Controller controller ) {
        this.controller = controller;
    }

    @Override
    public boolean isForViewType( @NonNull List< ? > items, int position ) {
        return items.get( position ) instanceof AddEntity;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_add;
    }

    @NonNull
    @Override
    public AddViewHolder getViewHolder( View itemView ) {
        return new AddViewHolder( controller, itemView );
    }

    public interface AddClickListener {

        void onAddClicked();
    }
}
