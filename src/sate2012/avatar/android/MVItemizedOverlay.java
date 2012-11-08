package sate2012.avatar.android;

import java.util.ArrayList;

import org.mapsforge.android.maps.ItemizedOverlay;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class MVItemizedOverlay extends ItemizedOverlay<DataObjectItem>
{
    public Context mContext;
    public ArrayList<DataObjectItem> mOverlays;
    public MapsForgeMapViewer mActivtyl;

    public static Drawable boundCenterBottom(Drawable drawable)
    {
		return ItemizedOverlay.boundCenter(drawable);
    }

    public MVItemizedOverlay(Drawable drawable)
    {
		super(boundCenter(drawable));
		mOverlays = new ArrayList<DataObjectItem>();
    }

    public MVItemizedOverlay(Drawable drawable, Context context)
    {
		super(boundCenter(drawable));
		this.mContext = context;
		mOverlays = new ArrayList<DataObjectItem>();
    }

    public void addOverlay(DataObjectItem overlay)
    {
		mOverlays.add(overlay);
		populate();
    }

    public void removeOverlay(int index)
    {
		mOverlays.remove(index);
		populate();
    }

    public void clear() 
    {
	mOverlays.clear();
	populate();
    }
    
    @Override
    protected DataObjectItem createItem(int i)
    {
		return mOverlays.get(i);
    }

    @Override
    public int size()
    {
		return mOverlays.size();
    }

    @Override
    public boolean onTap(int i)
    {
    	return true;
    }
}
