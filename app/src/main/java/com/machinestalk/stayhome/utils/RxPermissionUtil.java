package com.machinestalk.stayhome.utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Maher on 02/02/2018.
 */

public class RxPermissionUtil {

    private onPermissionListener mOnPermissionListener;
    private static RxPermissionUtil mInstance;

    /**
     *
     * @return
     */
    public static RxPermissionUtil getInstance() {
        if (mInstance == null) {
            mInstance = new RxPermissionUtil();
        }
        return mInstance;
    }
    /**
     * @param rxPermissions
     * @param permissions
     */
    public void checkRxPermission(RxPermissions rxPermissions, String[] permissions, final onPermissionListener listener) {
        rxPermissions.request(permissions)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            if(listener != null){
                                listener.onPermissionAllowed();
                            }
                        } else {
                            if(listener != null){
                                listener.onPermissionDenied();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public interface onPermissionListener {
        void onPermissionAllowed();

        void onPermissionDenied();
    }
}
