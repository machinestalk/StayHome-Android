package com.machinestalk.android.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is responsible for determining collection types or states. For
 * example, a collection is null or empty.
 *
 * 
 */
public final class CollectionUtility {

	private CollectionUtility() {
	}

	/**
	 * Determines if an {@link ArrayList} is {@code null} or empty.
	 * 
	 * @param list The array list
	 * @return {@code true} if the provided string is {@code null} or empty.
	 *         {@code false} otherwise
	 */
	public static boolean isEmptyOrNull (Collection<?> list) {

		return (list == null) || list.isEmpty();

	}

	public static String getCommaSeparatedStringFromArray(List list)
	{
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < list.size(); i++)
		{
			result.append(list.get(i));
			result.append(",");
		}

		return (result.length() > 0) ? result.substring(0, result.length() - 1) : "";
	}
}
