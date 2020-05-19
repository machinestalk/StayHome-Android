package com.machinestalk.stayhome.adapterdelegates;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.machinestalk.android.entities.BaseEntity;
import com.machinestalk.android.listeners.OnBottomReachedListener;
import com.machinestalk.android.utilities.CollectionUtility;
import com.machinestalk.android.viewholders.BaseViewHolder;

import java.util.List;

/**
 * Created on 20/06/2016.
 */
public class BaseRecyclerAdapter< VH extends BaseViewHolder > extends RecyclerView.Adapter< VH > {

    List< ? >                     entityList;
    AdapterDelegatesManager< VH > delegatesManager;
    private ClickInterfaceListener listener;
    OnBottomReachedListener onBottomReachedListener;

    @SuppressWarnings( "WeakerAccess" )
    public BaseRecyclerAdapter( List< ? > entityList ) {
        this.entityList = entityList;
        delegatesManager = new AdapterDelegatesManager<>();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setDataList(List<? extends BaseEntity> entityList) {
        this.entityList = entityList;
        notifyDataSetChanged();
    }
    public void setDataListt( List< ?  > entityList ) {
        this.entityList = entityList;
        notifyDataSetChanged();
    }

    public void addAdapterDelegates( @NonNull AdapterDelegate< VH > adapterDelegate ) {
        delegatesManager.addDelegate( adapterDelegate );
    }

    @Override
    public int getItemViewType( int position ) {
        return delegatesManager.getItemViewType( entityList, position );
    }

    @Override
    public VH onCreateViewHolder( ViewGroup parent, int viewType ) {
        return delegatesManager.onCreateViewHolder( parent, viewType );
    }

    @Override
    public void onBindViewHolder( final VH holder, int position ) {
        delegatesManager.onBindViewHolder( entityList, position, holder );
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                holder.onClick( v );

                if(holder.getAdapterPosition()==-1)
                    return;

                if ( listener != null ){
                    listener.onViewClicked( holder.itemView, entityList.get( holder.getAdapterPosition() ), holder.getAdapterPosition() );
                }
            }
        } );

        if (position == entityList.size() - 1){
            if (onBottomReachedListener != null){
                onBottomReachedListener.onBottomReached(position);
            }
        }

    }

    @Override
    public int getItemCount() {
        if ( CollectionUtility.isEmptyOrNull( entityList ) ) {
            return 0;
        }

        return entityList.size();
    }

    public void setClickInterface( ClickInterfaceListener listener ) {
        this.listener = listener;
    }

    public interface ClickInterfaceListener {
        void onViewClicked( View holder, Object entity, int position );
    }

    public List< ? > getList() {
        return entityList;
    }
}
