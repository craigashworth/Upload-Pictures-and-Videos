package sate2012.avatar.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import org.mapsforge.android.maps.GeoPoint;

public class CoordinateUpdater {
	private String pointID;			//The point's identification
	private String pointLat;		//The point's latitude
	private String pointLng;		//The point's longitude
	private double pointLatD;		
	private double pointLngD;
	private String pointType;		//The point's type (Audio, Video, Photo, Comment)
	private URL pointURL;			//The point's URL
	private String input;			//The string downloaded from the server
	private LinkedList<DataObjectItem> dataList;	//Stores all points downloaded

	public CoordinateUpdater() {
		input = "";
		try {
			URL site = new URL("http://virtualdiscoverycenter.net/avatar/php_files/email_rec_VW.php");
			URLConnection siteConnection = site.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(siteConnection.getInputStream()));
			String inputLine;
			in.readLine(); // Removes the extra '\n' character in the beginning
			while ((inputLine = in.readLine()) != null)
				input += inputLine;
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void CoordinateDataTranslator() {
		dataList = new LinkedList<DataObjectItem>();
		int startPos = input.indexOf(" ");
		int endPos = input.indexOf("_***");
		while(endPos != input.indexOf("@@@END")){
			pointID = input.substring(startPos, endPos);
			startPos = input.indexOf("*_", endPos);
			endPos = input.indexOf("_***", startPos);
			pointLat = input.substring(startPos + 2, endPos);
			startPos = input.indexOf("*_", endPos);
			endPos = input.indexOf("_***", startPos);
			pointLng = input.substring(startPos + 2, endPos);
			startPos = input.indexOf("*_", endPos);
			endPos = input.indexOf("_***", startPos);
			pointType = input.substring(startPos + 2, endPos);
			startPos = input.indexOf("*_", endPos);
			endPos = input.indexOf("@@@", startPos);
			try { pointURL = new URL("http://" + input.substring(startPos + 2, endPos) + "/");
			} catch (MalformedURLException e) { e.printStackTrace(); }
			input = input.substring(endPos);
			startPos = input.indexOf(" ");
			if(input.indexOf("@@@END") != 0) endPos = input.indexOf("_***");
			else endPos = input.indexOf("@@@END");
			//Convert the latitude and the longitude to doubles for plotting on MapsView
			pointLatD = Double.parseDouble(pointLat);
			pointLngD = Double.parseDouble(pointLng);
			DataObject pointData = new DataObject(pointLatD, pointLngD, pointID, pointType, pointURL);
			GeoPoint pointCoord = new GeoPoint(pointLatD, pointLngD);
			DataObjectItem newPoint = new DataObjectItem(pointCoord, pointData);
			dataList.add(newPoint);
		}
	}

	public LinkedList<DataObjectItem> getDataList() {
		return dataList;
	}

	public void setDataList(LinkedList<DataObjectItem> d) {
		this.dataList = d;
	}
}
