package sate2012.avatar.android;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayItem;

public class DataObjectItem extends OverlayItem {
	private DataObject data;
	private GeoPoint point;

	public DataObjectItem(GeoPoint p, DataObject dataIn) {
		super(p, null, null);
		point = p;
		data = dataIn;
	}

	public DataObject getData() {
		return data;
	}

	public void setData(DataObject d) {
		data = d;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint p) {
		point = p;
	}
}
