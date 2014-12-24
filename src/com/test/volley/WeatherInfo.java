package com.test.volley;

public class WeatherInfo {

	@Override
	public String toString() {
		return "WeatherInfo [city=" + city + ", temp=" + temp + ", time="
				+ time + "]";
	}

	private String city;

	private String temp;

	private String time;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	
	
}