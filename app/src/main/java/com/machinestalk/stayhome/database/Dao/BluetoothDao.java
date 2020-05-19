package com.machinestalk.stayhome.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.machinestalk.stayhome.entities.BluetoothEntity;

import java.util.List;

/**
 * Created by AhmedElyakoubiMachin on 29/11/2017.
 */



@Dao
public interface BluetoothDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BluetoothEntity> bluetoothEntities);

    @Query("UPDATE bluetoothEntity SET name = :name, frameName= :frameName " +
            ", adresse= :adresse, rssi= :rssi" +
            ", txPower= :txPower, namespace= :namespace" +
            ", instanceID= :instanceID, minor= :minor" +
            ", major= :major, udid= :udid" +
            ", url= :url, x= :x " +
            ", y= :y, z= :z" +
            ", rx= :rx, tx= :tx, battery= :battery, temperature= :temperature" +
            " WHERE adresse LIKE :adresse")
    void update(String name,String frameName, String adresse,String rssi,String txPower,String namespace,String instanceID,String minor
            ,String major,String udid,String url,String x,String y,String z
            ,String rx,String tx, String battery,String temperature);

    @Query("SELECT * FROM BluetoothEntity")
    List<BluetoothEntity> getAll();

    @Query("SELECT * FROM BluetoothEntity WHERE id LIKE :id ")
    BluetoothEntity getBluetoothEntityById(String id);

    @Query("SELECT * FROM BluetoothEntity WHERE name LIKE :name ")
    BluetoothEntity getBluetoothEntityByName(String name);

    @Query("SELECT * FROM BluetoothEntity WHERE adresse LIKE :adresse ")
    BluetoothEntity getBluetoothEntityByAdress(String adresse);


    @Delete
    void deleteAll(BluetoothEntity bluetoothEntity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBluetoothEntity(BluetoothEntity bluetoothEntity);

    @Query("DELETE FROM BluetoothEntity")
    void deleteAll();

    @Query("DELETE FROM BluetoothEntity WHERE adresse Not LIKE :adresse")
    void deleteByAdress(String adresse);

    @Query("DELETE FROM BluetoothEntity WHERE frameName Not LIKE :frameName")
    void deleteByFrameName(String frameName);

    @Query("SELECT * FROM BluetoothEntity WHERE frameName LIKE :FrameName ")
    BluetoothEntity getBluetoothEntityByFrameName(String FrameName);

}
