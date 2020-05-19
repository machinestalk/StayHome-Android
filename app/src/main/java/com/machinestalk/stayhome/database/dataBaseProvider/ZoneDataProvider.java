package com.machinestalk.stayhome.database.dataBaseProvider;

import android.content.Context;

import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.entities.ZoneEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maher on 02/12/2017.
 */

public class ZoneDataProvider {

    /**
     *
     * @param context
     * @param driverId
     * @return
     */
    public static List<ZoneEntity> getZoneByDriverId(Context context, int driverId)
    {
       List<ZoneEntity> zoneEntityList = AppDatabase.getInstance(context).getZonesDao().getAll();
       List<ZoneEntity> zoneEntityListResult = new ArrayList<>();
        if (zoneEntityList != null && zoneEntityList.size() > 0){
            for (ZoneEntity zoneEntity : zoneEntityList) {
                if(zoneEntity != null) {
                    List<Integer> driverIdList = zoneEntity.getListDriverIds();
                    if (driverIdList != null && driverIdList.size() > 0) {
                        if (driverIdList.contains(driverId)) {
                            zoneEntityListResult.add(zoneEntity);
                        }
                    }
                }
            }
        }
        return zoneEntityListResult;
    }
}
