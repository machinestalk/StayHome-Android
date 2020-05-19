package com.machinestalk.android.listeners;

/**
 * Created on 2/22/2017.
 */

public interface OnGroupClickListener {

	/**
	 * @param flatPos the flat position (raw index within the list of visible
	 *            items in the RecyclerView of a GroupViewHolder)
	 * @return false if click expanded group, true if click collapsed group
	 */
	boolean onGroupClick (int flatPos);
}