package com.example.andrey.myapplication2.Core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.q42.qlassified.Qlassified;
import com.q42.qlassified.Storage.QlassifiedSharedPreferencesService;

/**
 * Created by 1 on 14.02.2017.
 */
//http://stackoverflow.com/questions/12405699/android-non-activity-getsharedpreferences
public class LocalStorage {
    private Context context;

    public LocalStorage(Context c) {
        context = c;
        Qlassified.Service.start(c);
        Qlassified.Service.setStorageService(new QlassifiedSharedPreferencesService(c, "PersonalAccount"));
    }

    private String GetNotNullString(String data){
        if (data==null || data.isEmpty()) return ""; else return data;
    }
    public void SaveStringSettings(String key, String Value) {
        SharedPreferences settingsActivity = context.getSharedPreferences("PersonalAccount", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settingsActivity.edit();
        prefEditor.putString(key, Value);
        prefEditor.commit();
    }

    public String GetStringSettings(String key) {
        SharedPreferences settingsActivity = context.getSharedPreferences("PersonalAccount", Context.MODE_PRIVATE);
        return settingsActivity.getString(key, "");
    }

    public void SaveBooleanSettings(String key, Boolean Value) {
        SharedPreferences settingsActivity = context.getSharedPreferences("PersonalAccount", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settingsActivity.edit();
        prefEditor.putBoolean(key, Value);
        prefEditor.commit();
    }

    public Boolean GetBooleanSettings(String key) {

        SharedPreferences settingsActivity = context.getSharedPreferences("PersonalAccount", Context.MODE_PRIVATE);
        return settingsActivity.getBoolean(key, false);
    }

    public String GetBearerToken() {
        return GetNotNullString(Qlassified.Service.getString("BearerTokenfirst")+GetStringSettings("BearerTokenend"));
    }

    public void SaveBearerToken(String data) {
        Qlassified.Service.put("BearerTokenfirst", data.substring(0,20));
        SaveStringSettings("BearerTokenend",data.substring(20));
    }
    public void ClearBearerToken(){
        SaveStringSettings("BearerTokenend","");
    }

  /*  public void SaveIsEnableNotifications(Boolean data) {



        SaveBooleanSettings("IsEnableNotifications", data);
    }*/

    public Boolean GetIsEnableNotifications() {
        return  PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_preference_1",false);
        //return GetBooleanSettings("switch_preference_1");
    }

/*    public void SaveIsEnableSoundInNotif(Boolean data) {
        SaveBooleanSettings("IsEnableSoundInNotif", data);
    }*/

    public Boolean GetIsEnableSoundInNotif() {

        return  PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_preference_2",false);
        //return GetBooleanSettings("switch_preference_2");
    }

    public void SaveFirstRunParametr() {
        SaveBooleanSettings("firstrun", true);
    }

    public Boolean GetFirstRunParametr() {
        return GetBooleanSettings("firstrun");
    }

    public void SaveEmail(String data) {
        Qlassified.Service.put("Email", data);
    }
    public String GetEmail() {
        return GetNotNullString(Qlassified.Service.getString("Email"));
    }
    public void SavePassword(String data) {
        Qlassified.Service.put("Password", data);
    }
    public String GetPassword() {
        return GetNotNullString(Qlassified.Service.getString("Password"));
    }
}
