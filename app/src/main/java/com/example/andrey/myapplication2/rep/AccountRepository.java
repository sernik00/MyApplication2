package com.example.andrey.myapplication2.rep;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;

import com.example.andrey.myapplication2.Core.AllConfig;
import com.example.andrey.myapplication2.SimpleClass.ApplicationUser;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1 on 09.01.2017.
 */

public class AccountRepository {

    private String _BearerToken;

    public AccountRepository(String BearerToken) {
        _BearerToken = BearerToken;
    }

    public static Myresponse Register(String UserName, String Email, String Password) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("UserName", UserName);
        data.put("Email", Email);
        data.put("Password", Password);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpLkApi/Register").form(data);
        return AllConfig.ExtractMyresponse(response);
    }
    public static Myresponse GetToken(String Email, String Password) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("grant_type", "password");
        data.put("Username", Email);
        data.put("Password", Password);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/Token").form(data);
        if (response.ok()) {
            try {
                JSONObject jObject = new JSONObject(response.body());
                return Myresponse.GetOk(jObject.getString("access_token"));
            } catch (JSONException e) {
            }
        } else {
            if (response.code() == 400) {
                //badrequest обрабатываем
                try {
                    JSONObject jObject = new JSONObject(response.body());
                    return Myresponse.GetBadRequest(response.code(),jObject.getString("error_description"));
                } catch (JSONException e) {
                }
            }
        }
        return Myresponse.GetBadRequest(response.code(), response.body());
    }

    public  Myresponse ChangePassword(String OldPassword, String NewPassword, String ConfirmPassword) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("OldPassword", OldPassword);
        data.put("NewPassword", NewPassword);
        data.put("ConfirmPassword", ConfirmPassword);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpLkApi/ChangePassword").authorization("Bearer "+_BearerToken).form(data);
        return AllConfig.ExtractMyresponse(response);
    }

    public static Myresponse ResetPassword1(String Email) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("Email", Email);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpLkApi/ResetPassword1").form(data);
        return AllConfig.ExtractMyresponse(response);
    }
    public  Myresponse SaveMyInfa(String Name, String Vk) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("Name", Name);
        data.put("Code", Vk);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpLkApi/SaveMyInfa").authorization("Bearer "+_BearerToken).form(data);
        return AllConfig.ExtractMyresponse(response);
    }
    public  Myresponse LoadAvatarPhoto(String FileName) {
        //File input = new File("/input/data.txt");
        File input = new File(FileName);
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpLkApi/LoadAvatarPhoto").authorization("Bearer "+_BearerToken).send(input);
        return AllConfig.ExtractMyresponse(response);
    }
    public ApplicationUser OnePerson(String UserId) {
        HttpRequest response = HttpRequest.get(AllConfig.API_URL + "/api/ChpLkApi/OnePerson",true,"UserId",UserId).authorization("Bearer "+_BearerToken);
        if (response.ok()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ApplicationUser>() {}.getType();
            return gson.fromJson(response.body(), type);
        } else {
            return null;
        }
    }
    public ApplicationUser MyProfile() {
        HttpRequest response = HttpRequest.get(AllConfig.API_URL + "/api/ChpLkApi/MyProfile",true).authorization("Bearer "+_BearerToken);
        if (response.ok()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ApplicationUser>() {}.getType();
            return gson.fromJson(response.body(), type);
        } else {
            return null;
        }
    }
    public static class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {

        }
    }
}
