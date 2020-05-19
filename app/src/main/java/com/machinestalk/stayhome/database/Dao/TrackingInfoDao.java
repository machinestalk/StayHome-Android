package com.machinestalk.stayhome.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.machinestalk.stayhome.entities.TrackingInfoEntity;

import java.util.List;

/**
 * Created by AhmedElyakoubiMachin on 29/11/2017.
 */

@Dao
public interface TrackingInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TrackingInfoEntity> trackingInfoEntities);

    @Update
    void updateAll(TrackingInfoEntity configurationEntities);

    @Query("SELECT * FROM TrackingInfoEntity")
    List<TrackingInfoEntity> getAll();

    @Query("SELECT * FROM TrackingInfoEntity WHERE id LIKE :id ")
    TrackingInfoEntity getTrackingInfoEntityById(String id);


    @Delete
    void deleteAll(TrackingInfoEntity trackingInfoEntity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackingInfoEntity(TrackingInfoEntity trackingInfoEntity);

    @Query("DELETE FROM TrackingInfoEntity")
    void deleteAll();
}
