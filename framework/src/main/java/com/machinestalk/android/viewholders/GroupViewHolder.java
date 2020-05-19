package com.machinestalk.android.viewholders;

import android.view.View;

import com.machinestalk.android.listeners.OnGroupClickListener;

/**
 * Created on 2/22/2017.
 */

public abstract class GroupViewHolder<T> extends BaseViewHolder<T> {

	private OnGroupClickListener listener;

	public GroupViewHolder (View itemView) {
		super (itemView);
	}

	@Override
	public void onClick (View v) {

		if (listener != null) {
			listener.onGroupClick (getAdapterPosition ());
		}
	}

	public void setOnGroupClickListener (OnGroupClickListener listener) {

		this.listener = listener;
	}

	public void expand () {}

	public void collapse () {}
}
