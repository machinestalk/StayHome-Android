package com.machinestalk.stayhome.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.machinestalk.stayhome.entities.ZoneEntity;

import java.util.List;

@Dao
public interface ZoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ZoneEntity> zoneEntities);

    @Update
    void updateAll(ZoneEntity... zoneEntities);

    @Query("SELECT * FROM zone")
    List<ZoneEntity> getAll();

    @Delete
    void deleteAll(ZoneEntity... zoneEntities);

    /**
     * delete user by id
     * @param id
     * @return
     */
    @Query("DELETE FROM zone WHERE id = :id")
    void deleteZoneById(int id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertZone(ZoneEntity zoneEntity);

    @Query("DELETE FROM zone")
    void deleteAllZone();

    @Update
    void updateZone(ZoneEntity entity);

    @Query("SELECT * FROM zone WHERE id = :id")
    ZoneEntity getZoneById(int id);

}
