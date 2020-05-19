package com.machinestalk.android.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2/22/2017.
 */

public class ExpandableGroup<T extends BaseEntity> implements Parcelable {

	private String	title;
	private List<T>	items;
	private String	titleForToolbar;

	public ExpandableGroup () {}

	public ExpandableGroup (String title, List<T> items) {

		this.title = title;
		this.items = items;
	}

	public String getTitle () {

		return title;
	}

	public List<T> getItems () {

		return items;
	}

	public int getItemCount () {

		return items == null ? 0 : items.size ();
	}

	@Override
	public String toString () {

		return "ExpandableGroup{" + "title='" + title + '\'' + ", items=" + items + '}';
	}

	protected ExpandableGroup (Parcel in) {

		title = in.readString ();
		titleForToolbar = in.readString ();

		byte hasItems = in.readByte ();
		int size = in.readInt ();
		if (hasItems == 0x01) {
			items = new ArrayList<T> (size);
			Class<?> type = (Class<?>) in.readSerializable ();
			in.readList (items, type.getClassLoader ());
		}
		else {
			items = null;
		}
	}

	@Override
	public int describeContents () {

		return 0;
	}

	@Override
	public void writeToParcel (Parcel dest, int flags) {

		dest.writeString (title);
		dest.writeString (titleForToolbar);

		if (items == null) {
			dest.writeByte ((byte) (0x00));
			dest.writeInt (0);
		}
		else {

			dest.writeByte ((byte) (0x01));
			dest.writeInt (items.size ());
			final Class<?> objectsType = items.get (0).getClass ();
			dest.writeSerializable (objectsType);
			dest.writeList (items);
		}
	}

	public void setTitle (String title) {

		this.title = title;
	}

	public void setItems (List<T> items) {

		this.items = items;
	}

	public String getTitleForToolbar () {

		return titleForToolbar;
	}

	public void setTitleForToolbar (String titleForToolbar) {

		this.titleForToolbar = titleForToolbar;
	}
}