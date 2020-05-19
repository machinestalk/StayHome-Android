package com.machinestalk.stayhome.database.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Maher on 02/12/2017.
 */

public class ListIntegerConverter {


    @TypeConverter
    public static List<Integer> toListInteger(String ids) {
        if (TextUtils.isEmpty(ids)){
            return null;
        }
        List<String> listDriverId = new ArrayList<String>(Arrays.asList(ids.split(",")));
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0 ; i < listDriverId.size(); i++) {
            integerList.add(Integer.parseInt(listDriverId.get(i)));
        }
        return integerList;
    }

    @TypeConverter
    public static String toString(List<Integer> integerList) {
        StringBuilder driversIdsBuilder = new StringBuilder();
        if (integerList != null && integerList.size() > 0) {
            for (int i = 0; i < integerList.size(); i++) {
                driversIdsBuilder.append(String.valueOf(integerList.get(i))).append(",");
            }
            String driversIds = driversIdsBuilder.toString();
            return driversIds;
        }
        return null ;
    }
}
