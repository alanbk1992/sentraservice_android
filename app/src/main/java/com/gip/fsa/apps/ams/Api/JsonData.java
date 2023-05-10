package com.gip.fsa.apps.ams.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonData {

    static JSONObject json_object;

    public static String get_data(String response, String dataName){
        if(response.trim().charAt(0) == '[') {
            response = response.substring(1,response.length()-1);
            try {
                json_object = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(response.trim().charAt(0) == '{') {
            try {
                json_object = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String data_out = "";
        try {
            json_object = new JSONObject(response);
            data_out = json_object.getString(dataName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  data_out;
    }

    public static ArrayList<String> get_listdata(String response, String dataName){
        ArrayList<String> list_data = new ArrayList<String>();
        if(response.trim().charAt(0) == '[') {
            response = response.substring(1,response.length()-1);
            try {
                json_object = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(response.trim().charAt(0) == '{') {
            try {
                json_object = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONArray json_array = null;
        try {
            json_array = json_object.getJSONArray("data");
            for (int i=0;i<json_array.length();i++){
                JSONObject item = json_array.getJSONObject(i);
                list_data.add(item.getString(dataName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  list_data;
    }
}
