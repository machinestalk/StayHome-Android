package com.machinestalk.android.service;


import android.content.Context;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class has to be overridden for Web service implementation
 * <br/> This class has the boiler plate code for initialization
 * and needs a {@link ServiceProtocol} subclass instance to work.
 */

public abstract class ServiceFactory {
    protected final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    /**
     * Call this method to initialize the Factory.
     * Call to this method is a must.
     *
     * @throws Exception when {@link ServiceFactory#getServiceProtocol()}
     *                   returns {@code null}
     * @see ServiceFactory#getServiceProtocol()
     */

    public final void initialize(Context context, boolean forceInit) {

        ServiceManager.getInstance().initialize(getServiceProtocol(), context, forceInit);
    }

    public final void initialize(ServiceProtocol serviceProtocol, Context context, boolean forceInit) {

        ServiceManager.getInstance().initialize(serviceProtocol, context, forceInit);
    }


    /**
     * Use this method to create and load your service interfaces
     *
     * @param serviceClass Class of Service that you want to load
     * @return Service implementation
     */

    protected final <S> S loadService(Class<S> serviceClass) {
        if (!instances.containsKey(serviceClass)) {
            instances.put(serviceClass, ServiceManager.getInstance().loadService(serviceClass));
        }
        return (S) instances.get(serviceClass);
    }
    /**
     * Return a {@link ServiceProtocol} subclass in this method
     */
    protected abstract ServiceProtocol getServiceProtocol();

}
