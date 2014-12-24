package com.test.volley;

public class Weather {

	@Override
	public String toString() {
		return "Weather [weatherinfo=" + weatherinfo.toString() + "]";
	}

	private WeatherInfo weatherinfo;

	public WeatherInfo getWeatherinfo() {
		return weatherinfo;
	}

	public void setWeatherinfo(WeatherInfo weatherinfo) {
		this.weatherinfo = weatherinfo;
	}

}
