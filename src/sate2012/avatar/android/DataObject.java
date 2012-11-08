package sate2012.avatar.android;

import java.net.URL;

public class DataObject {
	private double lat;
	private double lon;
	private String type;
	private String id;
	private URL url;

	public DataObject() {
		lat = 0;
		lon = 0;
		type = null;
		id = null;
		url = null;
	}

	public DataObject(double l, double lo, String i, String t, URL u) {
		lat = l;
		lon = lo;
		url = u;
		type = t;
		id = i;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double l) {
		lat = l;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double l) {
		lon = l;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL u) {
		url = u;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
