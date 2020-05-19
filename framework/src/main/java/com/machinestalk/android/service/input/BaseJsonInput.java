package com.machinestalk.android.service.input;

import com.machinestalk.android.interfaces.JsonServiceRequest;

/**
 * An abstract class that is to be used as base for all the entities(POJO)
 * that are used for web service input i-e for input to {@link retrofit2.http.POST} calls
 */

@SuppressWarnings({"ClassMayBeInterface", "EmptyClass"})
public abstract class BaseJsonInput implements JsonServiceRequest {

}
