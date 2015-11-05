package com.example.android.sunshine.app;

/**
 * Created by Teddy on 10/27/2015.
 */
public class Weather {

    int dt;
    TempForecast temp;
    double speed;
    int degrees;
    int clouds;
    WeatherInfo weather;
    double pressure;
    int humidity;
    double rain;

    Weather(){}

    Weather(int fullDT, double speedValue, int deg, int cloudsValue, double pressureValue,
            int humidityValue, TempForecast tempForecast, WeatherInfo weatherInfo, double rainValue){
        dt = fullDT;
        speed = speedValue;
        degrees = deg;
        pressure = pressureValue;
        humidity = humidityValue;
        temp =  tempForecast;
        weather = weatherInfo;
        rain = rainValue;
    }

    public String toString(){
        return Double.toString(temp.getMin()) +"/"+Double.toString(temp.getMax())+"----"+ weather.getMain()
                + "----" + weather.getDescription() + " " + weather.getIcon();
    }

}

class WeatherInfo{

    int id;
    String main;
    String description;
    String icon;

    WeatherInfo(int idValue, String mainWeatherCondition, String desc,  String iconValue){
        id = idValue;
        main = mainWeatherCondition;
        description = desc;
        icon = iconValue;
    }

    public String getMain() {
        return main;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}

class TempForecast{

    double day;
    double night;
    double morn;
    double eve;
    double min;
    double max;

    TempForecast(double daytime, double minimum, double maximum, double nighttime,  double evening, double morning){
        day = daytime;
        min = minimum;
        max = maximum;
        night = nighttime;
        eve = evening;
        morn = morning;
    }

    public double getDay() {
        return day;
    }

    public double getEve() {
        return eve;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMorn() {
        return morn;
    }

    public double getNight() {
        return night;
    }
}
