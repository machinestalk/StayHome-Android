package com.machinestalk.android.viewholders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.MultiSelector;


/**
 * Created on 20/06/2016.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

    public BaseViewHolder(View itemView, MultiSelector multiSelector) {
        super(itemView);
        //itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    /**
     * Use this method to bind entity to the view
     * @param entity An instance of subclass of BaseEntity
     */
    public abstract void bind(T entity);

    /**
     * Override this method to use the
     * click event of the {@link View}
     *
     */
    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick( View view ) {
        return false;
    }


}
