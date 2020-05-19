package com.machinestalk.stayhome.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Logger.i(" App Started :  ");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.i(" App is Killed");
        Logger.i("All service down ----Due-----> simple kill ");
//        AppDatabase.getInstance(getBaseContext()).getVehicleDao().unSelectAllVehicle();
        //Code here
        stopSelf();
    }
}
