package zhiyuan.com.loan.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.utils
 * @time 12/4/2017 3:50 PM
 * @describe
 */

public class SharedPreferencesUtil {

    public static final String spName = "userinfo";
    //创建一个写入器
    private static SharedPreferences mPreferences;



    //初始化mPreferences对象
    public static void initSharedPreferences(Context context) {
        mPreferences =   context.getSharedPreferences(spName,Context.MODE_PRIVATE);
    }

    public static void putIntValueFromSP(String key,int value){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt(key,value).apply();
    }

    public static int getIntValueFromSP(String key){
        return mPreferences.getInt(key,-1);
    }


    public static void putStringValueFromSP(String key,String value){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(key,value).apply();
    }

    public static String getStringValueFromSP(String key){
        return mPreferences.getString(key,"");
    }

    public static void putBooleanValueFromSP(String key,Boolean value){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(key,value).apply();
    }


    public static boolean getBooleanValueFromSP(String key){
        return mPreferences.getBoolean(key,false);
    }

    //清楚SP里的所有数据
    public static void clearAllSPData(){
        mPreferences.edit().clear().apply();
    }

}