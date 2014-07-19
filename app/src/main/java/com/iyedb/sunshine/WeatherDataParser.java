package com.iyedb.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iyed on 19/07/2014.
 */
public class WeatherDataParser {


    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        double maxTemp = jsonArray.getJSONObject(dayIndex).getJSONObject("temp").getDouble("max");

        return maxTemp;
    }
}
