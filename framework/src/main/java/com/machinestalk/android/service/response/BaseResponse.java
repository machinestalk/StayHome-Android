package com.machinestalk.android.service.response;

import com.google.gson.JsonElement;
import com.machinestalk.android.interfaces.WebServiceResponse;

import java.util.ArrayList;

/**
 * An abstract class that is to be used as base for all responses
 * that are a collection of entities e.g a Json Array response
 */


public abstract class BaseResponse<T> implements WebServiceResponse{

	protected ArrayList<T> list = new ArrayList<>();
	@SuppressWarnings("WeakerAccess")
	protected T value;

	/**
	 * User this method to get array list of objects
	 * @return {@link ArrayList}
	 *
	 * Note : You'll have to populate this yourself in
	 * {@link WebServiceResponse#loadJson(JsonElement)} method
	 */

	public final ArrayList<T> getList(){
		return list;
	}

	/**
	 * You'll have to populate this yourself in
	 * {@link WebServiceResponse#loadJson(JsonElement)} method
	 */

	public final T getValue(){
		if(value == null) {
			return null;
		}

		return value;
	}
}
