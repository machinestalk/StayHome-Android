package com.machinestalk.stayhome.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.machinestalk.stayhome.responses.Configuration;

import java.util.List;

/**
 * Created by AhmedElyakoubiMachin on 29/11/2017.
 */

@Dao
public interface ConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Configuration> configurations);

    @Update
    void updateAll(Configuration Configuration);

    @Query("SELECT * FROM Configuration")
    List<Configuration> getAll();

    @Query("SELECT * FROM Configuration WHERE id LIKE :id ")
    Configuration getconfbyId(String id);

    @Query("SELECT * FROM Configuration WHERE `key` LIKE :key ")
    Configuration getconfbyKey(String key);


    @Delete
    void deleteAll(Configuration configuration);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertConfigurationEntity(Configuration configuration);

    @Query("DELETE FROM Configuration")
    void deleteAll();
}
