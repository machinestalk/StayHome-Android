package com.machinestalk.android.listeners;

import com.machinestalk.android.entities.ExpandableGroup;

/**
 * Created on 2/22/2017.
 */

public interface GroupExpandCollapseListener {

	/**
	 * Called when a group is expanded
	 * 
	 * @param group the {@link ExpandableGroup} being expanded
	 */
	void onGroupExpanded (ExpandableGroup group);

	/**
	 * Called when a group is collapsed
	 * 
	 * @param group the {@link ExpandableGroup} being collapsed
	 */
	void onGroupCollapsed (ExpandableGroup group);
}
