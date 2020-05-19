package com.machinestalk.stayhome.service;

import com.machinestalk.stayhome.service.interfaces.UserService;
import com.machinestalk.stayhome.service.interfaces.UtilService;

/**
 * Created on 08/02/2016.
 */
public class ServiceFactory extends com.machinestalk.android.service.ServiceFactory {

    private UserService userService;
    private UtilService utilService;


    public UserService getUserService() {

        if (userService == null) {

            loadUserService();
        }
        return userService;
    }



    public void loadUserService() {

        userService = loadService(UserService.class);
    }


    public UtilService getUtilService() {

        if (utilService == null) {

            loadUtilService();
        }
        return utilService;
    }

    public void loadUtilService() {

        utilService = loadService(UtilService.class);
    }


    @Override
    public com.machinestalk.android.service.ServiceProtocol getServiceProtocol() {

        return ServiceProtocol.getInstance();
    }
}
