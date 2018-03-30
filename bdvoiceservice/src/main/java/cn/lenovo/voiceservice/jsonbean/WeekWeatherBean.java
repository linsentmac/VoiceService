package cn.lenovo.voiceservice.jsonbean;

/**
 * Created by linsen3 on 2017/11/23.
 */

public class WeekWeatherBean {


    /**
     * dateTime : 2017-12-15 00:00:00.0
     * country : 中国
     * sunrise : 06:45:00
     * hourWeather : {'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '70', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150045'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '71', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150145'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '70', 'precipitation': '0.0', 'wind': '0'}, 'observeTime': '201712150245'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东风', 'humidity': '68', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150345'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '70', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150445'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '71', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150544'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '69', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150645'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '71', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150745'},{'weather': {'weatherCondition': '阴', 'temperature': '9', 'windDire': '东北风', 'humidity': '78', 'precipitation': '0.0', 'wind': '1'}, 'observeTime': '201712150845'},
     * flag : 0
     * city : 上海
     * county : 上海
     * highTem : 9
     * precipitation : 0.0
     * province : 上海
     * index_GanMao : 天冷空气湿度大，易发生感冒，请注意适当增加衣服，加强自我防护避免感冒。
     * weather : 小雨
     * humidity : 78
     * id : 237810077
     * index_XiChe : 不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。
     * index_ChuanYi : 建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。
     * wind_night : 微风
     * pm10 : 31
     * wind_direction : 东北风
     * pm25 : 13
     * countyID : 101020100
     * lowTem : 6
     * crawlTime : 2017-12-09 06:02:07.0
     * sunset : 16:53:00
     * weather_UpdateTime : 2017-12-15 08:00:00.0
     * wind_direction_night : 无持续风向
     * aqi : 31
     * weather_night : 小雨
     * wind : 微风
     */

    private String dateTime;
    private String country;
    private String sunrise;
    private String hourWeather;
    private String flag;
    private String city;
    private String county;
    private String highTem;
    private String precipitation;
    private String province;
    private String index_GanMao;
    private String weather;
    private String humidity;
    private String id;
    private String index_XiChe;
    private String index_ChuanYi;
    private String wind_night;
    private String pm10;
    private String wind_direction;
    private String pm25;
    private String countyID;
    private String lowTem;
    private String crawlTime;
    private String sunset;
    private String weather_UpdateTime;
    private String wind_direction_night;
    private String aqi;
    private String weather_night;
    private String wind;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getHourWeather() {
        return hourWeather;
    }

    public void setHourWeather(String hourWeather) {
        this.hourWeather = hourWeather;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getHighTem() {
        return highTem;
    }

    public void setHighTem(String highTem) {
        this.highTem = highTem;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getIndex_GanMao() {
        return index_GanMao;
    }

    public void setIndex_GanMao(String index_GanMao) {
        this.index_GanMao = index_GanMao;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex_XiChe() {
        return index_XiChe;
    }

    public void setIndex_XiChe(String index_XiChe) {
        this.index_XiChe = index_XiChe;
    }

    public String getIndex_ChuanYi() {
        return index_ChuanYi;
    }

    public void setIndex_ChuanYi(String index_ChuanYi) {
        this.index_ChuanYi = index_ChuanYi;
    }

    public String getWind_night() {
        return wind_night;
    }

    public void setWind_night(String wind_night) {
        this.wind_night = wind_night;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getCountyID() {
        return countyID;
    }

    public void setCountyID(String countyID) {
        this.countyID = countyID;
    }

    public String getLowTem() {
        return lowTem;
    }

    public void setLowTem(String lowTem) {
        this.lowTem = lowTem;
    }

    public String getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(String crawlTime) {
        this.crawlTime = crawlTime;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getWeather_UpdateTime() {
        return weather_UpdateTime;
    }

    public void setWeather_UpdateTime(String weather_UpdateTime) {
        this.weather_UpdateTime = weather_UpdateTime;
    }

    public String getWind_direction_night() {
        return wind_direction_night;
    }

    public void setWind_direction_night(String wind_direction_night) {
        this.wind_direction_night = wind_direction_night;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getWeather_night() {
        return weather_night;
    }

    public void setWeather_night(String weather_night) {
        this.weather_night = weather_night;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }
}
