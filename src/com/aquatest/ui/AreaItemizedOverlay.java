/**
 * Water Quality Manager for Android
 * Copyright (C) 2011 iCOMMS (University of Cape Town)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aquatest.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Object to store groups of overlay items (AquaOverlayItem). </p>
 * 
 * These could be used to display towns or samples on a map.
 */
public class AreaItemizedOverlay extends ItemizedOverlay
{

	MapListener mapListener;

	private Context context;
	private boolean showLabels;
	private ArrayList<AquaOverlayItem> mOverlays = new ArrayList<AquaOverlayItem>();

	AquaTestApp application;

	/**
	 * Constructor </p>
	 * 
	 * An {@link AreaItemizedOverlay} could be used to display towns or samples
	 * on a map.
	 * 
	 * @param _context
	 * @param _defaultMarker
	 *            default image to use as marker for this group of overlays
	 * @param _showLabels
	 *            whether to show labels
	 */
	public AreaItemizedOverlay(Context _context, Drawable _defaultMarker,
			boolean _showLabels)
	{
		super(boundCenterBottom(_defaultMarker));
		showLabels = _showLabels;
		context = _context;

		// TODO Auto-generated constructor stub
	}

	/**
	 * Get overlay item at specified index.
	 * 
	 * @param index
	 *            of item to retrieve
	 */
	@Override
	protected OverlayItem createItem(int i)
	{
		return mOverlays.get(i);
	}

	/**
	 * Set whether to show labels.
	 * 
	 * @param _showLabels
	 */
	public void setShowLabels(boolean _showLabels)
	{
		showLabels = _showLabels;
	}


	/**
	 * Draw the overlay items on the map.
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{

		super.draw(canvas, mapView, shadow);

		if (showLabels)
		{
			Paint paint = new Paint();

			for (int i = 0; i < mOverlays.size(); i++)
			{

				Point myScreenCoords = new Point();

				mapView.getProjection().toPixels(mOverlays.get(i).getPoint(),
						myScreenCoords);

				paint.setStrokeWidth(2);
				paint.setTypeface(Typeface.create(Typeface.MONOSPACE,
						Typeface.NORMAL));

				paint.setARGB(80, 55, 55, 55);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				paint.setTextSize(14);
				paint.setAntiAlias(true);

				// canvas.drawRect(myScreenCoords.x, myScreenCoords.y - 8,
				// myScreenCoords.x + 100, myScreenCoords.y + 8, paint);

				Rect bounds = new Rect();
				String text = mOverlays.get(i).getSnippet();

				paint.getTextBounds(text, 0, text.length(), bounds);
				bounds.inset(-2, -4);
				bounds.offsetTo(myScreenCoords.x + 5,
						myScreenCoords.y - bounds.height() / 2 + 1);

				canvas.drawRect(bounds, paint);
				paint.setStrokeWidth(1);
				// paint.setStyle(Paint.Style.FILL_AND_STROKE);
				paint.setARGB(250, 240, 240, 240);
				canvas.drawText(text, myScreenCoords.x + 6,
						myScreenCoords.y + 6, paint);


			}
			Canvas.freeGlCaches();

		}
	}


	/**
	 * Get the number of overlays in this object
	 */
	@Override
	public int size()
	{

		return mOverlays.size();
	}

	/**
	 * Add an overlay item to this collection.
	 * 
	 * @param overlay
	 *            the overlay item to add
	 */
	public void addOverlay(AquaOverlayItem overlay)
	{
		mOverlays.add(overlay);
		// populate();
	}


	/**
	 * Perform processing on a new ItemizedOverlay object.
	 */
	// TODO is there a reason this doesn't just override populate and call
	// super.populate()?
	public void Populate()
	{
		populate();
	}

	/**
	 * Set the listener for item clicks.
	 * 
	 * @param mapListener
	 *            the listener to handle item clicks.
	 */
	public void setMapListener(MapListener mapListener)
	{
		this.mapListener = mapListener;
	}

	/**
	 * Called when an item is tapped.
	 * 
	 * @param index
	 *            index of item tapped
	 */
	@Override
	public boolean onTap(int index)
	{

		AquaOverlayItem item = mOverlays.get(index);

		Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();

		mapListener.itemClicked(item.itemId, item.itemType, item.hasData);

		return true;
	}

}
