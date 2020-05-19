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


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.machinestalk.android.viewholders.BaseViewHolder;

import java.util.List;

/**
 * This delegate provide method to hook in this delegate to {@link RecyclerView.Adapter} lifecycle.
 * This "hook in" mechanism is provided by {@link AdapterDelegatesManager} and that is the
 * component
 * you have to use.
 *
 * @param <VH> The type of the View holder
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface AdapterDelegate<VH extends BaseViewHolder> {

    /**
     * Called to determine whether this AdapterDelegate is the responsible for the given data
     * element.
     *
     * @param items The data source of the Adapter
     * @param position The position in the datasource
     * @return true, if this item is responsible,  otherwise false
     */
    boolean isForViewType(@NonNull List<?> items, int position);

    int getLayoutRes();

    @NonNull  VH getViewHolder(View itemView);
}
