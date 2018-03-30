package cn.lenovo.voiceservice.jsonbean;

/**
 * Created by linsen on 17-12-26.
 */

public class HourWeatherBean {


    /**
     * weather : {"weatherCondition":"阴","temperature":"9","windDire":"东北风","humidity":"78","precipitation":"0.0","wind":"1"}
     * observeTime : 201712150845
     */

    private WeatherBean weather;
    private String observeTime;

    public WeatherBean getWeather() {
        return weather;
    }

    public void setWeather(WeatherBean weather) {
        this.weather = weather;
    }

    public String getObserveTime() {
        return observeTime;
    }

    public void setObserveTime(String observeTime) {
        this.observeTime = observeTime;
    }

    public static class WeatherBean {
        /**
         * weatherCondition : 阴
         * temperature : 9
         * windDire : 东北风
         * humidity : 78
         * precipitation : 0.0
         * wind : 1
         */

        private String weatherCondition;
        private String temperature;
        private String windDire;
        private String humidity;
        private String precipitation;
        private String wind;

        public String getWeatherCondition() {
            return weatherCondition;
        }

        public void setWeatherCondition(String weatherCondition) {
            this.weatherCondition = weatherCondition;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getWindDire() {
            return windDire;
        }

        public void setWindDire(String windDire) {
            this.windDire = windDire;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(String precipitation) {
            this.precipitation = precipitation;
        }

        public String getWind() {
            return wind;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }
    }
}
