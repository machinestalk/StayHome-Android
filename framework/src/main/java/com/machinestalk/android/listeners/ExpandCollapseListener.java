package com.machinestalk.android.listeners;

import com.machinestalk.android.entities.ExpandableGroup;

/**
 * Created on 2/22/2017.
 */

public interface ExpandCollapseListener {

	/**
	 * Called when a group is expanded
	 *
	 * @param positionStart the flat position of the first child in the
	 *            {@link ExpandableGroup}
	 * @param itemCount the total number of children in the
	 *            {@link ExpandableGroup}
	 */
	void onGroupExpanded (int positionStart, int itemCount);

	/**
	 * Called when a group is collapsed
	 *
	 * @param positionStart the flat position of the first child in the
	 *            {@link ExpandableGroup}
	 * @param itemCount the total number of children in the
	 *            {@link ExpandableGroup}
	 */
	void onGroupCollapsed (int positionStart, int itemCount);
}