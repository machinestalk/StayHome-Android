package com.machinestalk.stayhome.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.machinestalk.stayhome.database.Dao.BluetoothDao;
import com.machinestalk.stayhome.database.Dao.ConfigurationDao;
import com.machinestalk.stayhome.database.Dao.TrackingInfoDao;
import com.machinestalk.stayhome.database.Dao.ZoneDao;
import com.machinestalk.stayhome.database.converters.DateTypeConverter;
import com.machinestalk.stayhome.database.converters.ListIntegerConverter;
import com.machinestalk.stayhome.database.converters.LocationTypeConverter;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.machinestalk.stayhome.entities.TrackingInfoEntity;
import com.machinestalk.stayhome.entities.ZoneEntity;
import com.machinestalk.stayhome.responses.Configuration;

/**
 * Created by Maher on 09/11/2017.
 */
@Database(entities = {TrackingInfoEntity.class, BluetoothEntity.class, ZoneEntity.class, Configuration.class}, version = 1, exportSchema = false)

@TypeConverters({DateTypeConverter.class, ListIntegerConverter.class, LocationTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "stayhome.db";
    private static AppDatabase mInstance;

    public abstract TrackingInfoDao getTrackingInfoDao();

    public abstract ConfigurationDao getConfigurationDao();

    public abstract BluetoothDao getBluetoothDao();

    public abstract ZoneDao getZonesDao();


    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {

        return null;
    }

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                mInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return mInstance;
    }

    /**
     * delete all data from tables
     */
    public void deleteAllTable() {
        getZonesDao().deleteAllZone();
        getTrackingInfoDao().deleteAll();
        getBluetoothDao().deleteAll();
        getConfigurationDao().deleteAll();
    }

}