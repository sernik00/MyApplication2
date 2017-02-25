package com.example.andrey.myapplication2.Core;



import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.github.kevinsawicki.http.HttpRequest;


import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 1 on 02.02.2017.
 */

public class AllConfig {
    public static String API_URL = "http://edin2.fuxspace.ru";

    public static Myresponse ExtractMyresponse(HttpRequest response) {
        String body=response.body();
        if (response.ok()) {
                if (body.contains("Message"))
                {
                    try {
                        JSONObject jObject = new JSONObject(body);
                        return Myresponse.GetOk(jObject.getString("Message"));
                    } catch (Exception e) {}
                }
            return Myresponse.GetOk(body);
        } else {
            if (response.code() == 400) {
               //проверка через contenttype устраивать
                // response.contentType()
                //badrequest обрабатываем
                if (body.contains("Message")) {
                    try {
                        JSONObject jObject = new JSONObject(body);
                        return Myresponse.GetBadRequest(response.code(), jObject.getString("Message"));
                    } catch (Exception e) {
                    }
                }
            }
            return Myresponse.GetBadRequest(response.code(), body);
        }
    }
    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }
    //http://nelenkov.blogspot.ru/2012/05/storing-application-secrets-in-androids.html
    //http://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/
    ////https://github.com/scottyab/secure-preferences
    //http://forum.startandroid.ru/viewtopic.php?f=47&t=4633
    //https://habrahabr.ru/company/xakep/blog/128741/
    //если у чела andorid>4.3 версиси, то использовать keysoter, иначе секратная фраза Getdeviceid

}
