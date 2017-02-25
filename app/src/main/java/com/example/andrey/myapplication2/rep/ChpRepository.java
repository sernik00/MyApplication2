package com.example.andrey.myapplication2.rep;


import com.example.andrey.myapplication2.Core.AllConfig;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.HashMap;
import java.util.Map;

public class ChpRepository {

    private String _BearerToken;

    public ChpRepository(String BearerToken) {
        _BearerToken = BearerToken;
    }

   public HttpRequest GetAllCategoryAndDurationday() {
        HttpRequest response = HttpRequest.get(AllConfig.API_URL + "/api/ChpApi/GetAllCategoryAndDurationday").authorization("Bearer "+_BearerToken);
        return response;
     /*  CategoryandDuration r = new CategoryandDuration();
        if (response.ok()) try {
            JSONObject jObject = new JSONObject(response.body());
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Durationday>>() {
            }.getType();
            Type type2 = new TypeToken<ArrayList<CategoryChp>>() {
            }.getType();
            ArrayList<Durationday> res = gson.fromJson(jObject.getString("Durationday"), type);
            ArrayList<CategoryChp> res2 = gson.fromJson(jObject.getString("CategoryChp"), type2);
            r.CategoryChp = res2;
            r.Durationday = res;
            return r;
        } catch (JSONException e) {

        }
        else {

        }
        return r;*/

    }
    public static void SubscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }
    public static void UnSubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }
/*
    public Myresponse SaveModeratorKeyOnFcm(String key) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("key", key);

        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpApi/SaveModeratorKeyOnFcm").accept("Authorization: Bearer " + _BearerToken).form(data);
        return AllConfig.ExtractMyresponse(response);

    }
*/

    public Myresponse AddOtzuv(String Description) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("Description", Description);

        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpApi/AddOtzuv").authorization("Bearer "+_BearerToken).form(data);
        return AllConfig.ExtractMyresponse(response);

    }

}
