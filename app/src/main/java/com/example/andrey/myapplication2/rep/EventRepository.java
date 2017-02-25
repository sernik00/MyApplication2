package com.example.andrey.myapplication2.rep;


import android.support.test.espresso.core.deps.guava.reflect.TypeToken;

import com.example.andrey.myapplication2.Core.AllConfig;
import com.example.andrey.myapplication2.SimpleClass.EventChp;
import com.example.andrey.myapplication2.SimpleClass.EventListCountall;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1 on 09.01.2017.
 */

public class EventRepository {

    private String _BearerToken;

    public EventRepository(String BearerToken) {
        _BearerToken = BearerToken;
    }

    public EventListCountall GetAllEvent(String vid, String searchterm, int categoryChpId, String typeevents, int pageNumber, int CountAll) {
        HttpRequest response = HttpRequest.get(AllConfig.API_URL + "/api/ChpEventApi/GetAllEvent", true, "vid", vid, "searchterm",
                searchterm, "categoryChpId", categoryChpId, "typeevents", typeevents, "pageNumber", pageNumber, "CountAll", CountAll).
                authorization("Bearer " + _BearerToken);
        EventListCountall res = new EventListCountall();
        if (response.ok()) {
            try {
                String r=response.body();
                JSONObject jObject = new JSONObject(r);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<EventChp>>() {}.getType();
                res.EventList = gson.fromJson(jObject.getString("EventChp"), type);
                res.CountAll = Integer.parseInt(jObject.getString("CountAll"));
            } catch (Exception ex) {
                res.EventList=new ArrayList<>();
                res.CountAll=0;
                return res;
            }
        }
        return res;
    }
    public Myresponse CreateEvent(EventChp ct) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("Description", ct.Description);
        data.put("Koordinates", ct.Koordinates);
        data.put("Adress", ct.Adress);
        data.put("CategoryChpId", String.valueOf(ct.CategoryChpId));
        data.put("DaysForShow", String.valueOf(ct.DaysForShow));

        HttpRequest request2 = HttpRequest.post(AllConfig.API_URL + "/api/ChpEventApi/CreateEvent").authorization("Bearer " + _BearerToken).form(data);

        //request2.part("status[body]", "Making a multipart request");
        //request2.part("status[Description]", "Description");
        //request2.part("status[image]", new File("/home/kevin/Pictures/ide.png"));

        return AllConfig.ExtractMyresponse(request2);
    }

    public Myresponse DeleteEvent(int EventChpId) {
        HttpRequest response = HttpRequest.delete(AllConfig.API_URL + "/api/ChpEventApi/DeleteEvent?EventChpId="+EventChpId).authorization("Bearer " + _BearerToken);
        return AllConfig.ExtractMyresponse(response);
    }

    public Myresponse PublishEvent(int EventChpId) {
        HttpRequest response = HttpRequest.post(AllConfig.API_URL + "/api/ChpEventApi/PublishEvent?EventChpId="+EventChpId).authorization("Bearer " + _BearerToken);
        return AllConfig.ExtractMyresponse(response);
    }

    public ArrayList<EventChp> GetEventForModerator() {
        HttpRequest response = HttpRequest.get(AllConfig.API_URL + "/api/ChpEventApi/GetEventForModerator").authorization("Bearer " + _BearerToken);
        if (response.ok()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EventChp>>() {}.getType();
            ArrayList<EventChp> res = gson.fromJson(response.body(), type);
            return res;
        } else {
            return new ArrayList<>();
        }
    }
}
