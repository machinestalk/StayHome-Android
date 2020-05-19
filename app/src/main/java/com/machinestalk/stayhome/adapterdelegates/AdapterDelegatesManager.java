package com.machinestalk.stayhome.adapterdelegates;

/*
 * Copyright (c) 2015 Hannes Dorfmann.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.machinestalk.android.viewholders.BaseViewHolder;

import java.util.List;

/**
 * This class is the element that ties {@link RecyclerView.Adapter} together with {@link
 * AdapterDelegate}.
 * <p>
 * So you have to add / register your {@link AdapterDelegate}s to this manager by calling {@link
 * #addDelegate(AdapterDelegate)}
 * </p>
 *
 * <p>
 * Next you have to add this AdapterDelegatesManager to the {@link RecyclerView.Adapter} by calling
 * corresponding methods:
 * <ul>
 * <li> {@link #getItemViewType(List, int)}: Must be called from {@link
 * RecyclerView.Adapter#getItemViewType(int)}</li>
 * <li> {@link #onCreateViewHolder(ViewGroup, int)}: Must be called from {@link
 * RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}</li>
 * <li> {@link #onBindViewHolder(List, int, BaseViewHolder)}: Must be called from {@link
 * RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}</li>
 * </ul>
 *
 * You can also set a fallback {@link AdapterDelegate} by using {@link
 * #setFallbackDelegate(AdapterDelegate)} that will be used if no {@link AdapterDelegate} is
 * responsible to handle a certain view type. If no fallback is specified, an Exception will be
 * thrown if no {@link AdapterDelegate} is responsible to handle a certain view type
 * </p>
 *
 * @param <VH> The type of the datasource of the adapter
 * @author Hannes Dorfmann
 */
public class AdapterDelegatesManager<VH extends BaseViewHolder> {

    /**
     * This id is used internally to claim that the {@link}
     */
    static final int FALLBACK_DELEGATE_VIEW_TYPE = Integer.MAX_VALUE - 1;

    /**
     * Map for ViewType to AdapterDeleage
     */
    SparseArrayCompat<AdapterDelegate> delegates = new SparseArrayCompat();
    private AdapterDelegate fallbackDelegate;

    /**
     * Adds an {@link AdapterDelegate}.
     * <b>This method automatically assign internally the view type integer by using the next
     * unused</b>
     *
     * Internally calls {@link #addDelegate(int, boolean, AdapterDelegate)} with
     * allowReplacingDelegate = false as parameter.
     *
     * @param delegate the delegate to add
     * @return self
     * @throws NullPointerException if passed delegate is null
     * @see #addDelegate(int, AdapterDelegate)
     * @see #addDelegate(int, boolean, AdapterDelegate)
     */
    public AdapterDelegatesManager<VH> addDelegate(@NonNull AdapterDelegate<VH> delegate) {
        // algorithm could be improved since there could be holes,
        // but it's very unlikely that we reach Integer.MAX_VALUE and run out of unused indexes
        int viewType = delegates.size();
        while (delegates.get(viewType) != null) {
            viewType++;
            if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
                throw new IllegalArgumentException(
                        "Oops, we are very close to Integer.MAX_VALUE. It seems that there are no more free and unused view type integers left to add another AdapterDelegate.");
            }
        }
        return addDelegate(viewType, false, delegate);
    }

    /**
     * Adds an {@link AdapterDelegate} with the specified view type.
     *
     * Internally calls {@link #addDelegate(int, boolean, AdapterDelegate)} with
     * allowReplacingDelegate = false as parameter.
     *
     * @param viewType the view type integer if you want to assign manually the view type. Otherwise
     * use {@link #addDelegate(AdapterDelegate)} where a viewtype will be assigned manually.
     * @param delegate the delegate to add
     * @return self
     * @throws NullPointerException if passed delegate is null
     * @see #addDelegate(AdapterDelegate)
     * @see #addDelegate(int, boolean, AdapterDelegate)
     */
    public AdapterDelegatesManager<VH> addDelegate(int viewType,
                                                  @NonNull AdapterDelegate<VH> delegate) {
        return addDelegate(viewType, false, delegate);
    }

    /**
     * Adds an {@link AdapterDelegate}.
     *
     * @param viewType The viewType id
     * @param allowReplacingDelegate if true, you allow to replacing the given delegate any previous
     * delegate for the same view type. if false, you disallow and a {@link IllegalArgumentException}
     * will be thrown if you try to replace an already registered {@link AdapterDelegate} for the
     * same view type.
     * @param delegate The delegate to add
     * @throws IllegalArgumentException if <b>allowReplacingDelegate</b>  is false and an {@link
     * AdapterDelegate} is already added (registered)
     * with the same ViewType.
     * @throws IllegalArgumentException if viewType is {@link #FALLBACK_DELEGATE_VIEW_TYPE} which is
     * reserved
     * @see #addDelegate(AdapterDelegate)
     * @see #addDelegate(int, AdapterDelegate)
     * @see #setFallbackDelegate(AdapterDelegate)
     */
    public AdapterDelegatesManager<VH> addDelegate(int viewType, boolean allowReplacingDelegate,
                                                  @NonNull AdapterDelegate<VH> delegate) {

        if (delegate == null) {
            throw new NullPointerException("AdapterDelegate is null!");
        }

        if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
            throw new IllegalArgumentException("The view type = "
                    + FALLBACK_DELEGATE_VIEW_TYPE
                    + " is reserved for fallback adapter delegate (see setFallbackDelegate() ). Please use another view type.");
        }

        if (!allowReplacingDelegate && delegates.get(viewType) != null) {
            throw new IllegalArgumentException(
                    "An AdapterDelegate is already registered for the viewType = "
                            + viewType
                            + ". Already registered AdapterDelegate is "
                            + delegates.get(viewType));
        }

        delegates.put(viewType, delegate);

        return this;
    }

    /**
     * Removes a previously registered delegate if and only if the passed delegate is registered
     * (checks the reference of the object). This will not remove any other delegate for the same
     * viewType (if there is any).
     *
     * @param delegate The delegate to remove
     * @return self
     */
    public AdapterDelegatesManager<VH> removeDelegate(@NonNull AdapterDelegate<VH> delegate) {

        if (delegate == null) {
            throw new NullPointerException("AdapterDelegate is null");
        }

        int indexToRemove = delegates.indexOfValue(delegate);

        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove);
        }
        return this;
    }

    /**
     * Removes the adapterDelegate for the given view types.
     *
     * @param viewType The Viewtype
     * @return self
     */
    public AdapterDelegatesManager<VH> removeDelegate(int viewType) {
        delegates.remove(viewType);
        return this;
    }

    /**
     * Must be called from {@link RecyclerView.Adapter#getItemViewType(int)}. Internally it scans all
     * the registered {@link AdapterDelegate} and picks the right one to return the ViewType integer.
     *
     * @param items Adapter's data source
     * @param position the position in adapters data source
     * @return the ViewType (integer). Returns {@link #FALLBACK_DELEGATE_VIEW_TYPE} in case that the
     * fallback adapter delegate should be used
     * @throws IllegalArgumentException if no {@link AdapterDelegate} has been found that is
     * responsible for the given data element in data set (No {@link AdapterDelegate} for the given
     * ViewType)
     * @throws NullPointerException if items is null
     */
    public int getItemViewType(@NonNull List<?> items, int position) {

        if (items == null) {
            throw new NullPointerException("Items datasource is null!");
        }

        int delegatesCount = delegates.size();
        for (int i = 0; i < delegatesCount; i++) {
            AdapterDelegate delegate = delegates.valueAt(i);
            if (delegate.isForViewType(items, position)) {
                return delegates.keyAt(i);
            }
        }

        if (fallbackDelegate != null) {
            return FALLBACK_DELEGATE_VIEW_TYPE;
        }

        throw new IllegalArgumentException(
                "No AdapterDelegate added that matches position=" + position + " in data source");
    }

    /**
     * This method must be called in {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     *
     * @param parent the parent
     * @param viewType the view type
     * @return The new created ViewHolder
     * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
     * viewType
     */
    @NonNull public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterDelegate delegate = delegates.get(viewType);
        if (delegate == null) {
            if (fallbackDelegate == null) {
                throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
            } else {
                delegate = fallbackDelegate;
            }
        }

        View view = null;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(delegate.getLayoutRes(), parent, false);

        } catch (Exception ex) {

        }

        VH vh = (VH) delegate.getViewHolder(view);
        if (vh == null) {
            throw new NullPointerException("ViewHolder returned from AdapterDelegate "
                    + delegate
                    + " for ViewType ="
                    + viewType
                    + " is null!");
        }
        return vh;
    }

    /**
     * Must be called from{@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param items Adapter's data source
     * @param position the position in data source
     * @param viewHolder the ViewHolder to bind
     * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
     * viewType
     */
    public void onBindViewHolder(@NonNull List<?> items, int position,
                                 @NonNull VH viewHolder) {

        viewHolder.bind(items.get(position));
    }


    /**
     * Set a fallback delegate that should be used if no {@link AdapterDelegate} has been found that
     * can handle a certain view type.
     *
     * @param fallbackDelegate The {@link AdapterDelegate} that should be used as fallback if no
     * other AdapterDelegate has handled a certain view type. <code>null</code> you can set this to
     * null if
     * you want to remove a previously set fallback AdapterDelegate
     */
    public AdapterDelegatesManager<VH> setFallbackDelegate(
            @Nullable AdapterDelegate<VH> fallbackDelegate) {
        this.fallbackDelegate = fallbackDelegate;
        return this;
    }

    /**
     * Get the view type integer for the given {@link AdapterDelegate}
     *
     * @param delegate The delegate we want to know the view type for
     * @return -1 if passed delegate is unknown, otherwise the view type integer
     */
    public int getViewType(@NonNull AdapterDelegate<VH> delegate) {
        if (delegate == null) {
            throw new NullPointerException("Delegate is null");
        }

        int index = delegates.indexOfValue(delegate);
        if (index == -1) {
            return -1;
        }
        return delegates.keyAt(index);
    }

    /**
     * Get the {@link AdapterDelegate} associated with the given view type integer
     * @param viewType The view type integer we want to retrieve the associated
     *                 delegate for.
     * @return The {@link AdapterDelegate} associated with the view type param if it exists,
     * the fallback delegate otherwise if it is set.
     * @throws NullPointerException if no delegates are associated with this view type
     * and if no fallback delegate is set.
     */
    @NonNull public AdapterDelegate<VH> getDelegateForViewType(int viewType) {
        AdapterDelegate<VH> delegate = delegates.get(viewType);
        if (delegate == null) {
            if (fallbackDelegate == null) {
                throw new NullPointerException(
                        "No AdapterDelegate added for ViewType " + viewType);
            } else {
                delegate = fallbackDelegate;
            }
        }

        return delegate;
    }
}