package com.frederick.remocon;

import java.util.HashMap;
/**
 * Created by Frederick.
 */

public class GlobalEnv {

    public static HashMap<String,String> stringValues;
    public static HashMap<String,Boolean> booleanValues;

    public static void init(){
        stringValues = new HashMap<>();
        booleanValues = new HashMap<>();
    }

    public static synchronized String getString(String key){
        return stringValues.get(key);
    }
    public static synchronized Boolean getBoolean(String key, Boolean defaultValue){
        Boolean b =  booleanValues.get(key);
        if(b==null)return defaultValue;
        return b;
    }

    public static synchronized void put(String key, String value){
        stringValues.put(key,value);
    }

    public static synchronized void put(String key, Boolean value){
        booleanValues.put(key,value);
    }

}
